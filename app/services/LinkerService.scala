package services

import javax.inject.Inject

import com.google.inject.Singleton
import common.Common
import data.ErrorMessage
import models.LinkerDetailRepo
import models._

import scala.async.Async.{async}
import scala.concurrent.Await

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
@Singleton
class LinkerService @Inject()(
                             linkerDetailRepo: LinkerDetailRepo,
                             purchaseRepo: PurchaseRepo,
                             thingRepo: ThingRepo) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def getLinkerDetail(macAddress: String, customerId: Long): Either[String, (LinkerDetail, Seq[Customer], Seq[Thing])] = {

    linkerDetailRepo.findLinkerDetailByMacAddress(macAddress) match {
      case Some(linkerDetailList) => {
        if (linkerDetailList.isEmpty) return Left(ErrorMessage.NO_LINKER)

        val linkerDetail = linkerDetailList.head
        val linkerId = linkerDetail.linkerId.get

        val customerAndThingDetailFuture = async {
          val customerList = purchaseRepo.findHost(customerId, linkerId)
          val thingList = thingRepo.findByLinkerId(linkerId)

          (linkerDetail, customerList, thingList)
        }

        Right(Await.result(customerAndThingDetailFuture, Common.COMMON_ASYNC_DURATION))
      }
    }
  }


}
