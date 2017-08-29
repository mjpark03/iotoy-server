package common

import org.specs2.mock.Mockito
import play.api.test.{WithApplication, PlaySpecification}

/**
  * Created by Rachel on 2017. 7. 19..
  */
class CommonSpec extends PlaySpecification with Mockito {

  "createAuthSMSNumber" should {

    "return 4 digit number" in new WithApplication() {

      // when
      val result = Common.createAuthSMSNumber

      // then
      val length = 4

      result.length mustEqual(length)
    }

  }

}
