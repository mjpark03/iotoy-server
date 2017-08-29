package models

import com.google.inject.Inject
import common.Common
import models.Tables.{CustomersTable, PurchaseOwnersTable, PurchasesTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
class PurchaseRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val Purchases = TableQuery[PurchasesTable]
  private[models] val PurchaseOwners = TableQuery[PurchaseOwnersTable]
  private[models] val Customers = TableQuery[CustomersTable]

  def findHost(customerId: Long, linkerId: Long): Seq[Customer] = {

    val query = for {
      ((_, _), customer) <-
      Purchases.filter(_.linkerId === linkerId).filter(_.deletedAt.isEmpty)
        .join(PurchaseOwners).on(_.id === _.purchaseId).filter(_._2.deletedAt.isEmpty)
        .join(Customers).on(_._2.customerId === _.id)
    } yield (customer)

    Await.result(db.run(query.result), Common.COMMON_ASYNC_DURATION)
  }
}
