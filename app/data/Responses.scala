package data

import common.Common
import models.{Thing, Customer}
import play.api.libs.json.{JsArray, JsValue, Json, Writes}

/**
  * Created by mijeongpark on 2017. 7. 10..
  */
case class SuccessResponse(status: String = Common.SUCCESS, data: Option[JsValue] = None)
case class ErrorResponse(status: String = "error", message: String)
case class CreateDeviceTokenResponse(id: Long)
case class GetUserDetailResponse(
                                customer: Customer,
                                linkerList: Seq[String],
                                switcherList: List[String],
                                requestList: List[String]
                                )
case class GetAuthInfoResponse(accessToken: String)
case class GetLinkerDetailResponse(
                                  active: Boolean,
                                  host: Host,
                                  things: Seq[Thing]
                                  )
case class Host(phoneNumber: String, name: String)

object SuccessResponse {

  implicit val implicitSuccessResponse = new Writes[SuccessResponse] {
    def writes(response: SuccessResponse): JsValue = {
      Json.obj(
        "status" -> response.status,
        "data" -> response.data
      )
    }
  }
}

object ErrorResponse {

  implicit val implicitErrorResponse = new Writes[ErrorResponse] {
    def writes(response: ErrorResponse): JsValue = {
      Json.obj(
        "status" -> response.status,
        "message" -> response.message
      )
    }
  }
}

object CreateDeviceTokenResponse {

  implicit val implicitCreateDeviceTokenResponse = new Writes[CreateDeviceTokenResponse] {
    def writes(response: CreateDeviceTokenResponse): JsValue = {
      Json.obj(
        "id" -> response.id
      )
    }
  }
}

object GetUserDetailResponse {

  implicit val implicitGetUserDetailResponse = new Writes[GetUserDetailResponse] {
    def writes(response: GetUserDetailResponse): JsValue = {
      Json.obj(
        "id" -> response.customer.id,
        "phoneNumber" -> response.customer.phoneNumber,
        "name" -> response.customer.name,
        "authNumber" -> response.customer.authNumber,
        "postNo" -> response.customer.postNo,
        "addr1" -> response.customer.addr1,
        "addr2" -> response.customer.addr2,
        "email" -> response.customer.email,
        "createdAt" -> response.customer.createdAt,
        "updatedAt" -> response.customer.updatedAt,
        "linkerList" -> response.linkerList,
        "switcherList" -> response.switcherList,
        "requestList" -> response.requestList
      )
    }
  }
}

object GetAuthInfoResponse {

  implicit val implicitGetAuthInfoResponse = new Writes[GetAuthInfoResponse] {
    def writes(response: GetAuthInfoResponse): JsValue = {
      Json.obj(
        "accessToken" -> response.accessToken
      )
    }
  }
}

object GetLinkerDetailResponse {

  implicit val implicitGetLinkerDetailResponse = new Writes[GetLinkerDetailResponse] {
    def writes(response: GetLinkerDetailResponse): JsValue = {
      Json.obj(
        "active" -> response.active,
        "host" -> Json.obj(
          "phoneNumber" -> response.host.phoneNumber,
          "name" -> response.host.name
        ),
        "things" -> JsArray(response.things.map(thing =>
        Json.obj(
          "macAddress" -> thing.macAddress,
          "type" -> thing.`type`,
          "active" -> thing.active
        )))
      )
    }
  }
}







