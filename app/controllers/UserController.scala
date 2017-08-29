package controllers

import javax.inject._

import data._
import services.{UserService, SwitcherService}
import common.{Request}

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

class UserController @Inject()(cc: ControllerComponents,
                               switcherService: SwitcherService,
                               userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def createDeviceToken = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Option[JsValue] = request.body.asJson
    val token = (jsonBody.get \ "token").as[String]

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    userService.createDeviceToken(token, accessToken) match {
      case Right(id) => Future.successful(Ok(Json.toJson(SuccessResponse(data = Option(Json.toJson(CreateDeviceTokenResponse(id)))))))
      case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
    }
  }

  def getUserDetail = Action.async { implicit request: Request[AnyContent] =>

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    userService.checkAccessToken(accessToken) match {
      case Right(result) => {
        val (customer, linkerList) = userService.getUserDetail(result._1)
        // TODO: replace "Ra2uLPm0CTRQYXdzMglpbs+N7eLv4svrXEjd9YLACEI=" to access token parameter after modifying Switcher Server
        val switcherDetail = switcherService.getSwitcherDetail("Ra2uLPm0CTRQYXdzMglpbs+N7eLv4svrXEjd9YLACEI=")
        val userDetail = GetUserDetailResponse(customer, linkerList.map(m => m.macAddress.get), switcherDetail._1, switcherDetail._2)

        Future.successful(Ok(Json.toJson(SuccessResponse(data = Option(Json.toJson(userDetail))))))
      }
      case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
    }
  }

  def getAuthSMS = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Option[JsValue] = request.body.asJson
    val phoneNumber = (jsonBody.get \ "phoneNumber").as[String]
    val parameterMap = Map("phoneNumber" -> phoneNumber)

    Request.checkRequestParameters(parameterMap) match {
      case Right(_) => {
        userService.sendAuthSMS(phoneNumber) match {
          case Right(_) => Future.successful(Ok(Json.toJson(SuccessResponse())))
          case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
        }
      }
      case Left(parameter) =>
        Future.successful(Ok(Json.toJson(ErrorResponse(message = parameter + ErrorMessage.NO_REQUEST_PARAMETER))))
    }
  }

  def getAuthInfo = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Option[JsValue] = request.body.asJson
    val phoneNumber = (jsonBody.get \ "phoneNumber").as[String]
    val uuid = (jsonBody.get \ "uuid").as[String]
    val authNumber = (jsonBody.get \ "authNumber").as[String]
    val parameterMap = Map("phoneNumber" -> phoneNumber, "uuid" -> uuid, "authNumber" -> authNumber)

    Request.checkRequestParameters(parameterMap) match {
      case Right(_) => {
        userService.checkAuthSMS(phoneNumber, authNumber, uuid) match {
          case Right(accessToken) => {
            val authInfo = GetAuthInfoResponse(accessToken)
            Future.successful(Ok(Json.toJson(SuccessResponse(data = Option(Json.toJson(authInfo))))))
          }
          case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
        }
      }
      case Left(parameter) =>
        Future.successful(Ok(Json.toJson(ErrorResponse(message = parameter + ErrorMessage.NO_REQUEST_PARAMETER))))
    }

  }

}
