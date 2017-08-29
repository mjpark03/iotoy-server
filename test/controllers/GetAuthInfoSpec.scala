package controllers

import common.Common
import data.ErrorMessage
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}
import services.{UserService, SwitcherService}

/**
  * Created by Rachel on 2017. 7. 22..
  */
class GetAuthInfoSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockSwitcherService = mock[SwitcherService]
  private val mockUserService = mock[UserService]

  val controller = new UserController(controllerComponents, mockSwitcherService, mockUserService)

  private val PATH = "/v1/user/auth"

  private val AUTH_NUMBER = "1234"
  private val PHONE_NUMBER = "01028688487"
  private val UUID = "ab12"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"

  "getAuthInfo" should {

    "return error response if phone number is empty" in new WithApplication() {

      // given
      val fakeJson = Json.obj(
        "phoneNumber" -> "",
        "authNumber" -> AUTH_NUMBER,
        "uuid" -> UUID
      )
      val request = FakeRequest(PUT, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthInfo(request)

      // then
      val expectedResult = s"""{"status":"error","message":"phoneNumber${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if auth number is empty" in new WithApplication() {

      // given
      val fakeJson = Json.obj(
        "phoneNumber" -> PHONE_NUMBER,
        "authNumber" -> "",
        "uuid" -> UUID
      )
      val request = FakeRequest(PUT, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthInfo(request)

      // then
      val expectedResult = s"""{"status":"error","message":"authNumber${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if uuid is empty" in new WithApplication() {

      // given
      val fakeJson = Json.obj(
        "phoneNumber" -> PHONE_NUMBER,
        "authNumber" -> AUTH_NUMBER,
        "uuid" -> ""
      )
      val request = FakeRequest(PUT, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthInfo(request)

      // then
      val expectedResult = s"""{"status":"error","message":"uuid${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if auth number is not correct" in new WithApplication() {

      mockUserService.checkAuthSMS(anyString, anyString, anyString) returns Left(ErrorMessage.INVALID_AUTH_NUMBER)

      // given
      val wrongAuthNumber = "2222"
      val fakeJson = Json.obj(
        "phoneNumber" -> PHONE_NUMBER,
        "authNumber" -> wrongAuthNumber,
        "uuid" -> UUID
      )
      val request = FakeRequest(PUT, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthInfo(request)

      // then
      val expectedResult = s"""{"status":"error","message":"${ErrorMessage.INVALID_AUTH_NUMBER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if there is no customer by phone number" in new WithApplication() {

      mockUserService.checkAuthSMS(anyString, anyString, anyString) returns Left(ErrorMessage.NO_CUSTOMER)

      // given
      val wrongPhoneNumber = "01011112222"
      val fakeJson = Json.obj(
        "phoneNumber" -> wrongPhoneNumber,
        "authNumber" -> AUTH_NUMBER,
        "uuid" -> UUID
      )
      val request = FakeRequest(PUT, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthInfo(request)

      // then
      val expectedResult = s"""{"status":"error","message":"${ErrorMessage.NO_CUSTOMER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return response if getting access token is successful" in new WithApplication() {

      mockUserService.checkAuthSMS(anyString, anyString, anyString) returns Right(ACCESS_TOKEN)

      // given
      val fakeJson = Json.obj(
        "phoneNumber" -> PHONE_NUMBER,
        "authNumber" -> AUTH_NUMBER,
        "uuid" -> UUID
      )
      val request = FakeRequest(PUT, PATH)
        .withJsonBody(fakeJson)

      // when
      val result = controller.getAuthInfo(request)

      // then
      var expectedResult = s"""{"status":"${Common.SUCCESS}","data""""

      status(result) must beEqualTo(OK)
      contentAsString(result) must contain(expectedResult)
    }
  }
}
