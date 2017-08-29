package common

import com.google.inject.Singleton

import scala.concurrent.duration.Duration

/**
  * Created by Rachel on 2017. 7. 19..
  */
@Singleton
object Common {

  val COMMON_ASYNC_DURATION = Duration(3000, "millis")
  val SUCCESS = "success"

  def createAuthSMSNumber: String = {
    val now = System.currentTimeMillis().toString
    val preNow = now.substring(0, 4).toInt
    val postNow = now.substring(now.length-4, now.length).toInt
    (preNow * postNow).toString.substring(0, 4)
  }

}
