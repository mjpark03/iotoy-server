package services

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import common.{Common, ThirdParty}
import data.ErrorMessage
import models._

import scala.async.Async._
import scala.concurrent.{Await}

/**
  * Created by Rachel on 2017. 7. 17..
  */
@Singleton
class UserService @Inject()(
                             accessTokenRepo: AccessTokenRepo,
                             customerRepo: CustomerRepo,
                             customerDeviceRepo: CustomerDeviceRepo,
                             deviceTokenRepo: DeviceTokenRepo,
                             authService: AuthService,
                             thirdParty: ThirdParty) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def checkAccessToken(accessToken: String): Either[String, (AccessToken, CustomerDevice)] = {
    accessTokenRepo.findByAccessToken(accessToken) match {
      case Some(result) => Right(result._1, result._2)
      case None => Left(ErrorMessage.NO_ACCESS_TOKEN)
    }
  }

  def createDeviceToken(token: String, accessToken: String): Either[String, Long] = {
    deviceTokenRepo.createDeviceToken(token, accessToken) match {
      case Some(id) => Right(id)
      case None => Left(ErrorMessage.NO_ACCESS_TOKEN)
    }
  }

  def getUserDetail(accessToken: AccessToken): (Customer, Seq[Linker]) = {

   val userDetailFuture = async {
      val Some(customerDevice) = await(customerRepo.findCustomerDeviceId(accessToken.customerDeviceId.getOrElse(0)))
      val Some(customer) = await(customerRepo.findById(customerDevice.customerId.getOrElse(0)))
      val linkerList = customerRepo.findLinkerListByCustomerId(customerDevice.customerId.get)

      (customer, linkerList)
    }

    Await.result(userDetailFuture, Common.COMMON_ASYNC_DURATION)
  }

  def sendAuthSMS(phoneNumber: String): Either[String, String] = {

    val authNumber = Common.createAuthSMSNumber
    val now = new Timestamp(System.currentTimeMillis())

    thirdParty.sendSMS(List(phoneNumber), authNumber) match {
      case Common.SUCCESS => {
        checkExistingUser(phoneNumber) match {
          case Some(customer) => {
            customer.authNumber = Option(authNumber)
            customerRepo.updateCustomer(customer)
          }
          case None => {
            val customer = Customer(
              phoneNumber = Option(phoneNumber),
              authNumber = Option(authNumber),
              createdAt = now,
              updatedAt = now
            )
            customerRepo.createCustomer(customer)
          }
        }
        Right(Common.SUCCESS)
      }
      case _ => Left(ErrorMessage.FAIL_SMS_SEND)
    }
  }

  def checkExistingUser(phoneNumber: String): Option[Customer] = {
    Await.result(customerRepo.findByPhoneNumber(phoneNumber), Common.COMMON_ASYNC_DURATION)
  }

  def checkAuthSMS(phoneNumber: String, authNumber: String, uuid: String): Either[String, String] = {
    Await.result(customerRepo.findByPhoneNumber(phoneNumber), Common.COMMON_ASYNC_DURATION) match {
      case Some(customer) => {
        customer.authNumber.get == authNumber match {
          case true => {
            val accessToken = getAccessToken(customer.id, phoneNumber, uuid)
            Right(accessToken)
          }
          case false => Left(ErrorMessage.INVALID_AUTH_NUMBER)
        }
      }
      case None => Left(ErrorMessage.NO_CUSTOMER)
    }
  }

  def getAccessToken(customerId: Long, phoneNumber: String, uuid: String): String = {

    val now = new Timestamp(System.currentTimeMillis())

    customerDeviceRepo.findByCustomerId(customerId, uuid) match {
      case Some(customerDevice) => {
        val customerDeviceId = customerDevice.id
        accessTokenRepo.findByCustomerDeviceId(customerDeviceId) match {
          case Some(accessTokenInfo) => accessTokenInfo.accessToken.get
        }
      }
      case None => {
        val createAccessTokenFuture = async {
          val accessToken = authService.getAccessToken(phoneNumber)
          val customerDevice = CustomerDevice(
            0,
            Some(customerId),
            Some(uuid),
            now,
            now,
            None
          )
          val customerDeviceId = customerDeviceRepo.insertCustoemrDevice(customerDevice)

          val accessTokenInfo = AccessToken(
            0,
            Some(customerDeviceId),
            Some(accessToken),
            now,
            now,
            None
          )
          accessTokenRepo.insertAccessToken(accessTokenInfo)
          accessToken
        }
        Await.result(createAccessTokenFuture, Common.COMMON_ASYNC_DURATION)
      }
    }
  }
}
