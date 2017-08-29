package common

import com.google.inject.Singleton

/**
  * Created by Rachel on 2017. 7. 19..
  */
@Singleton
object Request {

  def checkRequestParameters(parameters: Map[String, String]): Either[String, Boolean] = {

    for ((key, value) <- parameters) if(value.isEmpty) return Left(key)

    Right(true)
  }

}
