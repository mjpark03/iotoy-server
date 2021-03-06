package controllers

import data.ErrorMessage
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}
import services.{UserService, LinkerService}

/**
  * Created by Rachel on 2017. 9. 19..
  */
class CreateThingCommandSpec extends PlaySpecification with Mockito {

  private val controllerComponents = stubControllerComponents()
  private implicit val executionContext = controllerComponents.executionContext

  private val mockLinkerService = mock[LinkerService]
  private val mockUserService = mock[UserService]

  val controller = new LinkerController(controllerComponents, mockLinkerService, mockUserService)

  private val PATH = "/v1/things/command"
  private val ACCESS_TOKEN = "sdfkhsdkjfhsdkjfh"

  private val IOTOY_ID = 12L
  private val THING_TYPE = "switcher"
  private val THING_ID = 34L
  private val COMMAND = "sc-ON"

  "createThingCommand" should {

    "return error response if iotoy id is empty" in new WithApplication() {

      // given
      val iotoyId = ""
      val thingType = THING_TYPE
      val thingId = THING_ID
      val command = COMMAND
      val request = FakeRequest(POST, PATH)
                  .withJsonBody(Json.parse(
                    s"""{ "iotoyId": "$iotoyId", "thingType": "$thingType",
                       | "thingId": "$thingId", "command": "$command"}""".stripMargin))
                  .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createThingCommand(request)

      // then
      val expectedResult = s"""{"status":"error","message":"iotoyId${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if thing type is empty" in new WithApplication() {

      // given
      val iotoyId = IOTOY_ID
      val thingType = ""
      val thingId = THING_ID
      val command = COMMAND
      val request = FakeRequest(POST, PATH)
        .withJsonBody(Json.parse(
          s"""{ "iotoyId": "$iotoyId", "thingType": "$thingType",
              | "thingId": "$thingId", "command": "$command"}""".stripMargin))
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createThingCommand(request)

      // then
      val expectedResult = s"""{"status":"error","message":"thingType${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if thing id is empty" in new WithApplication() {

      // given
      val iotoyId = IOTOY_ID
      val thingType = THING_TYPE
      val thingId = ""
      val command = COMMAND
      val request = FakeRequest(POST, PATH)
        .withJsonBody(Json.parse(
          s"""{ "iotoyId": "$iotoyId", "thingType": "$thingType",
              | "thingId": "$thingId", "command": "$command"}""".stripMargin))
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createThingCommand(request)

      // then
      val expectedResult = s"""{"status":"error","message":"thingId${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }

    "return error response if thing id is empty" in new WithApplication() {

      // given
      val iotoyId = IOTOY_ID
      val thingType = THING_TYPE
      val thingId = THING_ID
      val command = ""
      val request = FakeRequest(POST, PATH)
        .withJsonBody(Json.parse(
          s"""{ "iotoyId": "$iotoyId", "thingType": "$thingType",
              | "thingId": "$thingId", "command": "$command"}""".stripMargin))
        .withHeaders("Authorization" -> s"Bearer ${ACCESS_TOKEN}")

      // when
      val result = controller.createThingCommand(request)

      // then
      val expectedResult = s"""{"status":"error","message":"command${ErrorMessage.NO_REQUEST_PARAMETER}"}"""

      status(result) must beEqualTo(OK)
      contentAsString(result) mustEqual(expectedResult)
    }
  }

}
