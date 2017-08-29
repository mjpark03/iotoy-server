package controllers

import data.ErrorMessage
import common.Common
import services.{UserService, SwitcherService}

import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }

/**
  * Created by Rachel on 2017. 7. 12..
  */
class CreateDeviceTokenSpec extends PlaySpecification with Mockito {


  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockSwitcherService = mock[SwitcherService]
  private val mockUserService = mock[UserService]

  val controller = new UserController(controllerComponents, mockSwitcherService, mockUserService)

  private val PATH = "/v1/user/token"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"
  private val TOKEN = "fsdhfhjwehjfh"

  private val ID = 10

  "createDeviceToken" should {

    "return error response if access token does not exist" in new WithApplication() {

      // given
      mockUserService.createDeviceToken(anyString, anyString) returns Left(ErrorMessage.NO_ACCESS_TOKEN)

      val request = FakeRequest(POST, PATH)
                  .withJsonBody(Json.parse(s"""{ "token": "${TOKEN}" }"""))
                  .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createDeviceToken(request)

      // then
      var expectedResult = """{"status":"error","message":"There is no access token"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return response if device token is successfully created" in new WithApplication() {

      // given
      mockUserService.createDeviceToken(anyString, anyString) returns Right(ID)

      val request = FakeRequest(POST, PATH)
        .withJsonBody(Json.parse(s"""{ "token": "${TOKEN}" }"""))
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createDeviceToken(request)

      // then
      var expectedResult = s"""{"status":"${Common.SUCCESS}","data":{"id":${ID}}}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)

    }
  }

}
