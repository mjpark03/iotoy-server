package controllers

import javax.inject.Inject

import common.Request
import data._
import play.api.libs.json.{Json}
import play.api.mvc._
import services.{UserService, LinkerService}

import scala.concurrent.{Future, ExecutionContext}

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
class LinkerController @Inject()(cc: ControllerComponents,
                                 linkerService: LinkerService,
                                 userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getLinkerDetail(macAddress: String) = Action.async { implicit request: Request[AnyContent] =>

    val authorization = request.headers.get("Authorization")
    val accessToken = authorization.get.split(" ")(1)

    val parameterMap = Map("macAddress" -> macAddress)

    Request.checkRequestParameters(parameterMap) match {
      case Right(_) => {
        userService.checkAccessToken(accessToken) match {
          case Right(result) => {
            val customerId = result._2.customerId.get
            linkerService.getLinkerDetail(macAddress, customerId) match {
              case Right(result) => {
                val linkerDetail = result._1
                val customerList = result._2
                val thingList = result._3

                if (customerList.isEmpty) Future.successful(Ok(Json.toJson(ErrorResponse(message = ErrorMessage.NO_HOST))))
                else {
                  val customer = customerList.head
                  val linkerDetailResult = GetLinkerDetailResponse(
                    linkerDetail.active.get,
                    Host(customer.phoneNumber.get, customer.name.get),
                    thingList
                  )
                  Future.successful(Ok(Json.toJson(SuccessResponse(data = Option(Json.toJson(linkerDetailResult))))))
                }
              }
              case Left(message) => Future.successful(Ok(Json.toJson(ErrorResponse(message = message))))
            }
          }
        }
      }
      case Left(parameter) =>
        Future.successful(Ok(Json.toJson(ErrorResponse(message = parameter + ErrorMessage.NO_REQUEST_PARAMETER))))
    }

  }

}
