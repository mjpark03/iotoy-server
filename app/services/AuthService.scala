package services

import com.google.inject.{Inject, Singleton}
import play.api.libs.ws.WSClient

/**
  * Created by Rachel on 2017. 7. 22..
  */
@Singleton
class AuthService @Inject()(ws: WSClient) {

  def getAccessToken(phoneNumber: String): String = {

    // TODO: modify logic for calling auth server request after re-building auth server
    "Ra2uLPm0CTRQYXdzMglpbs+N7eLv437rXEjd9YLACEI="
  }
}
