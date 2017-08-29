package controllers

import java.sql.Timestamp

import common.Common
import data.ErrorMessage
import models._
import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}
import services.{UserService, LinkerService}

/**
  * Created by Rachel on 2017. 7. 24..
  */
class GetLinkerDetailSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockLinkerService = mock[LinkerService]
  private val mockUserService = mock[UserService]

  val controller = new LinkerController(controllerComponents, mockLinkerService, mockUserService)

  private val PATH = "/v1/user/linkers"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"

  private val MAC_ADDRESS = "11:22:11:22:11:22"
  private val ACCESS_TOKEN_ID = 1L
  private val CUSTOMER_DEVICE_ID = 2L
  private val CUSTOMER_ID = 3L
  private val TIMESTAMP =  new Timestamp(System.currentTimeMillis())
  private val LINKER_DETAIL_ID = 4L
  private val LINKER_ID = 5L
  private val THING_ID = 6L
  private val PHONE_NUMBER = "01028688487"
  private val NAME = "박미정"

  "getLinkerDetail" should {

    "return error response if mac address is empty" in new WithApplication() {

      // given
      val macAddress = ""
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getLinkerDetail(macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"macAddress${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if there is no linker by mac address" in new WithApplication() {

      var accessToken = AccessToken(
        ACCESS_TOKEN_ID,
        Some(CUSTOMER_DEVICE_ID),
        Option(ACCESS_TOKEN),
        TIMESTAMP,
        TIMESTAMP,
        None
      )

      var customerDevice = CustomerDevice(
        id = CUSTOMER_DEVICE_ID,
        customerId = Some(CUSTOMER_ID),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      mockUserService.checkAccessToken(anyString) returns Right(accessToken, customerDevice)
      mockLinkerService.getLinkerDetail(anyString, anyLong) returns Left(ErrorMessage.NO_LINKER)

      // given
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getLinkerDetail(macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"${ErrorMessage.NO_LINKER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if there is no host by linker" in new WithApplication() {

      var accessToken = AccessToken(
        ACCESS_TOKEN_ID,
        Some(CUSTOMER_DEVICE_ID),
        Option(ACCESS_TOKEN),
        TIMESTAMP,
        TIMESTAMP,
        None
      )

      var customerDevice = CustomerDevice(
        id = CUSTOMER_DEVICE_ID,
        customerId = Some(CUSTOMER_ID),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val linkerDetailList = Seq(
        LinkerDetail(
          id = LINKER_DETAIL_ID,
          linkerId = Some(LINKER_ID),
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      val customerList = Seq[Customer]()

      val thingList = Seq(
        Thing(
          id = THING_ID,
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      mockUserService.checkAccessToken(anyString) returns Right(accessToken, customerDevice)
      mockLinkerService.getLinkerDetail(anyString, anyLong) returns Right(linkerDetailList.head, customerList, thingList)

      // given
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getLinkerDetail(macAddress)(request)

      // then
      val expectedResult = s"""{"status":"error","message":"${ErrorMessage.NO_HOST}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return response if there is linker detail by mac address" in new WithApplication() {

      var accessToken = AccessToken(
        ACCESS_TOKEN_ID,
        Some(CUSTOMER_DEVICE_ID),
        Option(ACCESS_TOKEN),
        TIMESTAMP,
        TIMESTAMP,
        None
      )

      var customerDevice = CustomerDevice(
        id = CUSTOMER_DEVICE_ID,
        customerId = Some(CUSTOMER_ID),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val linkerDetailList = Seq(
        LinkerDetail(
          id = LINKER_DETAIL_ID,
          linkerId = Some(LINKER_ID),
          active = Some(true),
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      val customerList = Seq(
        Customer(
          id = CUSTOMER_ID,
          phoneNumber = Some(PHONE_NUMBER),
          name = Some(NAME),
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      val thingList = Seq(
        Thing(
          id = THING_ID,
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      mockUserService.checkAccessToken(anyString) returns Right(accessToken, customerDevice)
      mockLinkerService.getLinkerDetail(anyString, anyLong) returns Right(linkerDetailList.head, customerList, thingList)

      // given
      val macAddress = MAC_ADDRESS
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getLinkerDetail(macAddress)(request)

      // then
      var expectedResult = s"""{"status":"${Common.SUCCESS}","data""""

      status(result) must beEqualTo(OK)
      contentAsString(result) must contain(expectedResult)
    }

  }
}
