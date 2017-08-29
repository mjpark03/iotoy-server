package services

import java.sql.Timestamp

import common.{ThirdParty, Common}
import data.ErrorMessage
import models._
import org.specs2.mock.Mockito
import play.api.test.{PlaySpecification, WithApplication}

import scala.concurrent.Future

/**
  * Created by mijeongpark on 2017. 7. 20..
  */
class UserServiceSpec extends PlaySpecification with Mockito {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val mockAccessTokenRepo = mock[AccessTokenRepo]
  private val mockCustomerRepo = mock[CustomerRepo]
  private val mockCustomerDeviceRepo = mock[CustomerDeviceRepo]
  private val mockDeviceTokenRepo = mock[DeviceTokenRepo]
  private val mockThirdParty = mock[ThirdParty]
  private val mockUserService = mock[UserService]
  private val mockAuthService = mock[AuthService]

  val service = new UserService(
    mockAccessTokenRepo, mockCustomerRepo, mockCustomerDeviceRepo, mockDeviceTokenRepo, mockAuthService, mockThirdParty)

  private val PHONE_NUMBER = "01028688487"
  private val CUSTOMER_ID = 1L
  private val TIMESTAMP = new Timestamp(System.currentTimeMillis())
  private val AUTH_NUMBER = "1234"
  private val DEVICE_TOKEN = "1sdkfsdfdsf"
  private val ACCESS_TOKEN = "sfhsfkhskdjf"
  private val DEVICE_TOKEN_ID = 2L
  private val ACCESS_TOKEN_ID = 3L
  private val CUSTOMER_DEVICE_ID = 4L
  private val LINKER_ID = 5L
  private val UUID = "321"

  "checkExistingUser" should {

    "return None if there is no customer by phone number" in new WithApplication() {

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(None)

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.checkExistingUser(phoneNumber)

      // then
      val expectedResult = None

      result mustEqual(expectedResult)
    }

    "return Some(customer) there is customer by phone number" in new WithApplication() {

      val customer = Customer(
        CUSTOMER_ID,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        TIMESTAMP,
        TIMESTAMP,
        None
      )

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(Option(customer))

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.checkExistingUser(phoneNumber)

      // then
      val expectedResult = Some(customer)

      result mustEqual(expectedResult)
    }
  }

  "sendAuthSMS" should {
    "return Left(FAIL_SMS_SEND) if sending sms through 3rd party server is failed" in new WithApplication() {

      mockThirdParty.sendSMS(any, anyString) returns "error"

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.sendAuthSMS(phoneNumber)

      // then
      val expectedResult = Left(ErrorMessage.FAIL_SMS_SEND)

      result mustEqual(expectedResult)
    }

    "return Right if sending sms is successful in case of new customer" in new WithApplication() {

      mockThirdParty.sendSMS(any, anyString) returns Common.SUCCESS
      mockUserService.checkExistingUser(anyString) returns None
      mockCustomerRepo.createCustomer(any) returns 1

      // given
      val phoneNumber = PHONE_NUMBER

      // when
      val result = service.sendAuthSMS(phoneNumber)

      // then
      val expectedResult = Right(Common.SUCCESS)

      result mustEqual(expectedResult)
    }
  }

  "return Right if sending sms is successful in case of existing customer" in new WithApplication() {

    val customer = Customer(
      authNumber = Option(AUTH_NUMBER),
      createdAt = TIMESTAMP,
      updatedAt = TIMESTAMP
    )

    mockThirdParty.sendSMS(any, anyString) returns Common.SUCCESS
    mockUserService.checkExistingUser(anyString) returns Some(customer)
    mockCustomerRepo.updateCustomer(any) returns 1

    // given
    val phoneNumber = PHONE_NUMBER

    // when
    val result = service.sendAuthSMS(phoneNumber)

    // then
    val expectedResult = Right(Common.SUCCESS)

    result mustEqual(expectedResult)
  }

  "createDeviceToken" should {

    "return Some(id) if creating device token is successful" in new WithApplication() {

      mockDeviceTokenRepo.createDeviceToken(anyString, anyString) returns Some(DEVICE_TOKEN_ID)

      // given
      val deviceToken = DEVICE_TOKEN
      val accessToken = ACCESS_TOKEN

      // when
      val result = service.createDeviceToken(deviceToken, accessToken)

      // then
      val expectedResult = Right(DEVICE_TOKEN_ID)

      result mustEqual(expectedResult)
    }

    "return None if creating device token is failed because access token does not exist" in new WithApplication() {

      mockDeviceTokenRepo.createDeviceToken(anyString, anyString) returns None

      // given
      val deviceToken = DEVICE_TOKEN
      val accessToken = ACCESS_TOKEN

      // when
      val result = service.createDeviceToken(deviceToken, accessToken)

      // then
      val expectedResult = Left(ErrorMessage.NO_ACCESS_TOKEN)

      result mustEqual(expectedResult)
    }
  }

  "checkAccessToken" should {

    "return Left(error) if there is no access token" in new WithApplication() {

      mockAccessTokenRepo.findByAccessToken(anyString) returns None

      // given
      val accessToken = ACCESS_TOKEN

      // when
      val result = service.checkAccessToken(accessToken)

      // then
      val expectedResult = Left(ErrorMessage.NO_ACCESS_TOKEN)

      result mustEqual(expectedResult)
    }

    "return Right(accessToken) if there is access token" in new WithApplication() {

      val accessTokenInfo = AccessToken(
        id = ACCESS_TOKEN_ID,
        customerDeviceId = Some(CUSTOMER_DEVICE_ID),
        accessToken = Some(ACCESS_TOKEN),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val customerDevice = CustomerDevice(
        id = ACCESS_TOKEN_ID,
        customerId = Some(CUSTOMER_ID),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      mockAccessTokenRepo.findByAccessToken(anyString) returns Some(accessTokenInfo, customerDevice)

      // given
      val accessToken = ACCESS_TOKEN

      // when
      val result = service.checkAccessToken(accessToken)

      // then
      val expectedResult = Right(accessTokenInfo, customerDevice)

      result mustEqual(expectedResult)
    }
  }

  "getUserDetail" should {

    "return (customer, linker list) if there is user detail by access token" in new WithApplication() {

      val customerDevice = CustomerDevice(
        id = CUSTOMER_DEVICE_ID,
        customerId = Some(CUSTOMER_ID),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val customer = Customer(
        id = CUSTOMER_ID,
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val linker = Linker(
        id = LINKER_ID,
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val linkerList = Seq(linker)

      mockCustomerRepo.findCustomerDeviceId(anyLong) returns Future(Some(customerDevice))
      mockCustomerRepo.findById(anyLong) returns Future(Some(customer))
      mockCustomerRepo.findLinkerListByCustomerId(anyLong) returns linkerList

      // given
      val accessToken = AccessToken(
        id = ACCESS_TOKEN_ID,
        accessToken = Some(ACCESS_TOKEN),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      // when
      val result = service.getUserDetail(accessToken)

      // then
      val expectedResult = (customer, linkerList)

      result mustEqual(expectedResult)
    }
  }

  "checkAuthSMS" should {

    "return Left(error) if there is no customer by phone number" in new WithApplication() {

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(None)

      // given
      val phoneNumber = PHONE_NUMBER
      val authNumber = AUTH_NUMBER
      val uuid = UUID

      // when
      val result = service.checkAuthSMS(phoneNumber, authNumber, uuid)

      // then
      val expectedResult = Left(ErrorMessage.NO_CUSTOMER)

      result mustEqual(expectedResult)
    }

    "return Left(error) if auth number is not correct" in new WithApplication() {

      val customer = Customer(
        id = CUSTOMER_ID,
        authNumber = Some(AUTH_NUMBER),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(Some(customer))

      // given
      val phoneNumber = PHONE_NUMBER
      val wrongAuthNumber = "1111"
      val uuid = UUID

      // when
      val result = service.checkAuthSMS(phoneNumber, wrongAuthNumber, uuid)

      // then
      val expectedResult = Left(ErrorMessage.INVALID_AUTH_NUMBER)

      result mustEqual(expectedResult)
    }

    "return Right(access token) if auth number is correct" in new WithApplication() {

      val customer = Customer(
        id = CUSTOMER_ID,
        authNumber = Some(AUTH_NUMBER),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val customerDevice = CustomerDevice(
        id = CUSTOMER_DEVICE_ID,
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val accessToken = AccessToken(
        id = ACCESS_TOKEN_ID,
        accessToken = Some(ACCESS_TOKEN),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      mockCustomerRepo.findByPhoneNumber(anyString) returns Future(Some(customer))
      mockCustomerDeviceRepo.findByCustomerId(anyLong, anyString) returns Some(customerDevice)
      mockAccessTokenRepo.findByCustomerDeviceId(anyLong) returns Some(accessToken)

      // given
      val phoneNumber = PHONE_NUMBER
      val wrongAuthNumber = AUTH_NUMBER
      val uuid = UUID

      // when
      val result = service.checkAuthSMS(phoneNumber, wrongAuthNumber, uuid)

      // then
      val expectedResult = Right(ACCESS_TOKEN)

      result mustEqual(expectedResult)
    }

  }

  "getAccessToken" should {

    "return access token if there is no customer device" in new WithApplication() {
      mockCustomerDeviceRepo.findByCustomerId(anyLong, anyString) returns None
      mockAuthService.getAccessToken(anyString) returns ACCESS_TOKEN
      mockCustomerDeviceRepo.insertCustoemrDevice(any) returns CUSTOMER_DEVICE_ID
      mockAccessTokenRepo.insertAccessToken(any) returns ACCESS_TOKEN_ID

      // given
      val customerId = CUSTOMER_ID
      val phoneNumber = PHONE_NUMBER
      val uuid = UUID

      // when
      val result = service.getAccessToken(customerId, phoneNumber, uuid)

      // then
      val expectedResult = ACCESS_TOKEN

      result mustEqual(expectedResult)
    }

    "return access token if there is existing customer device" in new WithApplication() {

      val customerDevice = CustomerDevice(
        id = CUSTOMER_DEVICE_ID,
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val accessToken = AccessToken(
        id = ACCESS_TOKEN_ID,
        accessToken = Some(ACCESS_TOKEN),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      mockCustomerDeviceRepo.findByCustomerId(anyLong, anyString) returns Some(customerDevice)
      mockAccessTokenRepo.findByCustomerDeviceId(anyLong) returns Some(accessToken)

      // given
      val customerId = CUSTOMER_ID
      val phoneNumber = PHONE_NUMBER
      val uuid = UUID

      // when
      val result = service.getAccessToken(customerId, phoneNumber, uuid)

      // then
      val expectedResult = ACCESS_TOKEN

      result mustEqual(expectedResult)
    }

  }

}
