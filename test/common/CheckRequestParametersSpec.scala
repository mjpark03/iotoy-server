package common

import org.specs2.mock.Mockito
import play.api.test.{WithApplication, PlaySpecification}

/**
  * Created by Rachel on 2017. 7. 19..
  */
class CheckRequestParametersSpec extends PlaySpecification with Mockito {

  "checkRequestParameters" should {

    "return Left() if there is the parameter that does not have value" in new WithApplication() {

      // given
      val parameters = Map("key1" -> "")

      // when
      val result = Request.checkRequestParameters(parameters)

      // then
      val expectedResult = Left("key1")

      result mustEqual(expectedResult)
    }
  }

  "return Right() if all parameters has the value" in new WithApplication() {

    // given
    val parameters = Map("key1" -> "value1", "key2" -> "value2")

    // when
    val result = Request.checkRequestParameters(parameters)

    // then
    val expectedResult = Right(true)

    result mustEqual(expectedResult)
  }
}
