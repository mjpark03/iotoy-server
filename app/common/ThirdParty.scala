package common

import com.google.inject.{Singleton, Inject}
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WSClient
import play.libs.ws.WSBody
import scala.async.Async._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.ws.JsonBodyWritables._

/**
  * Created by Rachel on 2017. 7. 19..
  */
@Singleton
class ThirdParty @Inject()(ws: WSClient) {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val HOST = "http://3rdparty-prod.switcher.co.kr"

  def sendSMS(receivers: List[String], content: String): String = {

    val path = "/v1/sms/"

    val request = async {
      val response = await(ws.url(HOST + path).post(Json.obj("receivers" -> receivers, "content" -> content)))
      val status = ((response.json \ "status").as[String])
      status
    }

    Await.result(request, Duration(3000, "millis"))
  }

}
