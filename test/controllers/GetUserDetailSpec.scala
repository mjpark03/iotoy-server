package controllers

import java.sql.Timestamp

import models.{CustomerDevice, Linker, Customer, AccessToken}
import common.Common
import services.{UserService, SwitcherService}

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}

/**
  * Created by Rachel on 2017. 7. 13..
  */
class GetUserDetailSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockSwitcherService = mock[SwitcherService]
  private val mockUserService = mock[UserService]

  val controller = new UserController(controllerComponents, mockSwitcherService, mockUserService)

  private val PATH = "/v1/user/detail"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"

  private val ACCESS_TOKEN_ID = 1L
  private val CUSTOMER_DEVICE_ID = 2L
  private val TIMESTAMP = new Timestamp(System.currentTimeMillis())
  private val CUSTOMER_ID = 3L
  private val LINKER_ID = 4L
  private val MAC_ADDRESS = "11:22:33:44:55:66"
  private val REQUEST_ID = "5"

  "getUserDetail" should {

    "return error response if access token does not exist" in new WithApplication() {

      mockUserService.checkAccessToken(anyString) returns Left("There is no access token")

      // given
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getUserDetail(request)

      // then
      var expectedResult = """{"status":"error","message":"There is no access token"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return response if there is user detail by access token" in new WithApplication() {

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

      var customer = Customer(
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

      var linker = Linker(
        LINKER_ID,
        Option(MAC_ADDRESS),
        TIMESTAMP,
        TIMESTAMP,
        None
      )

      mockUserService.checkAccessToken(anyString) returns Right(accessToken, customerDevice)
      mockUserService.getUserDetail(any) returns ((customer, Seq(linker)))
      mockSwitcherService.getSwitcherDetail(anyString) returns ((List(MAC_ADDRESS), List(REQUEST_ID)))

      // given
      val request = FakeRequest(GET, PATH)
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.getUserDetail(request)

      // then
      var expectedResult = s"""{"status":"${Common.SUCCESS}","data""""

      status(result) must beEqualTo(OK)
      contentAsString(result) must contain(expectedResult)
    }
  }

}
