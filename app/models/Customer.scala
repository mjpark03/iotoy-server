package models

import java.sql.Timestamp

import Tables._
import javax.inject.Inject

import common.Common
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, Future}

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

class CustomerRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val Customers = TableQuery[CustomersTable]
  private[models] val Linkers = TableQuery[LinkersTable]
  private[models] val CustomerDevices = TableQuery[CustomerDevicesTable]
  private[models] val Purchases = TableQuery[PurchasesTable]
  private[models] val PurchaseOwners = TableQuery[PurchaseOwnersTable]

  private def _findById(id: Long): DBIO[Option[Customer]] =
    Customers.filter(_.id === id).result.headOption

  private def _findByPhoneNumber(phoneNumber: String): DBIO[Option[Customer]] =
    Customers.filter(_.phoneNumber === phoneNumber).result.headOption

  private def _insert(customer: Customer): DBIO[Long] =
    Customers returning Customers.map(_.id) += customer

  private def _update(id: Long, customer: Customer) =
    Customers.filter(_.id === id).update(customer)

  def findById(id: Long): Future[Option[Customer]] = {
    db.run(_findById(id))
  }

  def findCustomerDeviceId(id: Long): Future[Option[CustomerDevice]] = {
    db.run(CustomerDevices.filter(_.id === id).result.headOption)
  }

  def findByPhoneNumber(phoneNumber: String): Future[Option[Customer]] = {
    db.run(_findByPhoneNumber(phoneNumber))
  }

  def findLinkerListByCustomerId(customerId: Long): Seq[Linker] = {

    val query = for {
      ((purchaseOwner, purchase), linkers) <-
      PurchaseOwners.filter(_.customerId === customerId)
        .join(Purchases).on(_.purchaseId === _.id)
        .join(Linkers).on(_._2.linkerId === _.id)
    } yield (linkers)

    Await.result(db.run(query.result), Common.COMMON_ASYNC_DURATION)
  }

  def createCustomer(customer: Customer) = {
    Await.result(db.run(_insert(customer)), Common.COMMON_ASYNC_DURATION)
  }

  def updateCustomer(customer: Customer) = {
    customer.updatedAt = new Timestamp(System.currentTimeMillis())
    Await.result(db.run(_update(customer.id, customer)), Common.COMMON_ASYNC_DURATION)
  }
}

