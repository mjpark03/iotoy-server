package models

import java.sql.Timestamp

/**
  * Created by mijeongpark on 2017. 7. 10..
  */

case class Customer(
                     id: Long = 0,
                     phoneNumber: Option[String] = None,
                     var name: Option[String] = None,
                     var authNumber: Option[String] = None,
                     var postNo: Option[String] = None,
                     var addr1: Option[String] = None,
                     var addr2: Option[String] = None,
                     var email: Option[String] = None,
                     var createdAt: Timestamp,
                     var updatedAt: Timestamp,
                     var deletedAt: Option[Timestamp] = None)

case class CustomerDevice(
                           id: Long,
                           customerId: Option[Long] = None,
                           uuid: Option[String] = None,
                           createdAt: Timestamp,
                           updatedAt: Timestamp,
                           deletedAt: Option[Timestamp] = None)

case class AccessToken(
                        id: Long,
                        customerDeviceId: Option[Long] = None,
                        accessToken: Option[String] = None,
                        createdAt: Timestamp,
                        updatedAt: Timestamp,
                        deletedAt: Option[Timestamp] = None)

case class DeviceToken(
                        id: Long,
                        customerDeviceId: Option[Long],
                        deviceToken: Option[String],
                        createdAt: Timestamp,
                        updatedAt: Timestamp,
                        deletedAt: Option[Timestamp])

case class Linker(
                   id: Long,
                   macAddress: Option[String] = None,
                   createdAt: Timestamp,
                   updatedAt: Timestamp,
                   deletedAt: Option[Timestamp] = None)

case class Purchase(
                     id: Long,
                     linkerId: Option[Long] = None,
                     customerId: Option[Long] = None,
                     price: Option[String] = None,
                     warrantyDate: Option[Timestamp] = None,
                     createdAt: Timestamp,
                     updatedAt: Timestamp,
                     deletedAt: Option[Timestamp] = None)

case class PurchaseOwner(
                          id: Long,
                          purchaseId: Option[Long] = None,
                          customerId: Option[Long] = None,
                          createdAt: Timestamp,
                          updatedAt: Timestamp,
                          deletedAt: Option[Timestamp] = None)

case class LinkerDetail(
                        id: Long,
                        linkerId: Option[Long] = None,
                        status: Option[String] = None,
                        pKey: Option[String] = None,
                        shareCode: Option[String] = None,
                        active: Option[Boolean] = None,
                        createdAt: Timestamp,
                        updatedAt: Timestamp,
                        deletedAt: Option[Timestamp] = None)

case class Thing(
                 id: Long,
                 linkerId: Option[Long] = None,
                 `type`: Option[String] = None,
                 macAddress: Option[String] = None,
                 active: Option[Boolean] = None,
                 createdAt: Timestamp,
                 updatedAt: Timestamp,
                 deletedAt: Option[Timestamp] = None)


object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._

  /* AccessTokens Table Definitions */
  class AccessTokensTable(_tableTag: Tag) extends profile.api.Table[AccessToken](_tableTag, "AccessTokens") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def accessToken = column[Option[String]]("accessToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, accessToken, createdAt, updatedAt, deletedAt) <> (AccessToken.tupled, AccessToken.unapply)
  }

  lazy val AccessTokensTable = new TableQuery(tag => new AccessTokensTable(tag))

  /* DeviceTokens Table Definitions */
  class DeviceTokensTable(_tableTag: Tag) extends profile.api.Table[DeviceToken](_tableTag, "DeviceTokens") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def deviceToken = column[Option[String]]("deviceToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, deviceToken, createdAt, updatedAt, deletedAt) <> (DeviceToken.tupled, DeviceToken.unapply)
  }

  lazy val DeviceTokensTable = new TableQuery(tag => new DeviceTokensTable(tag))

  /* Customers Table Definitions */
  class CustomersTable(_tableTag: Tag) extends profile.api.Table[Customer](_tableTag, "Customers") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def phoneNumber = column[Option[String]]("phoneNumber")
    def name = column[Option[String]]("name")
    def authNumber = column[Option[String]]("authNumber")
    def postNo = column[Option[String]]("postNo")
    def addr1 = column[Option[String]]("addr1")
    def addr2 = column[Option[String]]("addr2")
    def email = column[Option[String]]("email")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, phoneNumber, name, authNumber, postNo, addr1, addr2, email, createdAt, updatedAt, deletedAt) <> (Customer.tupled, Customer.unapply)
  }

  lazy val CustomersTable = new TableQuery(tag => new CustomersTable(tag))

  /* CustomerDevices Table Definitions */
  class CustomerDevicesTable(_tableTag: Tag) extends profile.api.Table[CustomerDevice](_tableTag, "CustomerDevices") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerId = column[Option[Long]]("customerId")
    def uuid = column[Option[String]]("uuid")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerId, uuid, createdAt, updatedAt, deletedAt) <> (CustomerDevice.tupled, CustomerDevice.unapply)
  }

  lazy val CustomerDevicesTable = new TableQuery(tag => new CustomerDevicesTable(tag))

  /* Linkers Table Definitions */
  class LinkersTable(_tableTag: Tag) extends profile.api.Table[Linker](_tableTag, "Linkers") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def macAddress = column[Option[String]]("macAddress")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, macAddress, createdAt, updatedAt, deletedAt) <> (Linker.tupled, Linker.unapply)
  }

  lazy val LinkersTable = new TableQuery(tag => new LinkersTable(tag))

  /* Purchases Table Definitions */
  class PurchasesTable(_tableTag: Tag) extends profile.api.Table[Purchase](_tableTag, "Purchases") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def linkerId = column[Option[Long]]("linkerId")
    def customerId = column[Option[Long]]("customerId")
    def price = column[Option[String]]("price")
    def warrantyDate = column[Option[Timestamp]]("warrantyDate")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, linkerId, customerId, price, warrantyDate, createdAt, updatedAt, deletedAt) <> (Purchase.tupled, Purchase.unapply)
  }

  lazy val PurchasesTable = new TableQuery(tag => new PurchasesTable(tag))

  /* PurchaseOwners Table Definitions */
  class PurchaseOwnersTable(_tableTag: Tag) extends profile.api.Table[PurchaseOwner](_tableTag, "PurchaseOwners") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def purchaseId = column[Option[Long]]("purchaseId")
    def customerId = column[Option[Long]]("customerId")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, purchaseId, customerId, createdAt, updatedAt, deletedAt) <> (PurchaseOwner.tupled, PurchaseOwner.unapply)
  }

  lazy val PurchaseOwnersTable = new TableQuery(tag => new PurchaseOwnersTable(tag))

  /* LinkerDetails Table Definitions */
  class LinkerDetailsTable(_tableTag: Tag) extends profile.api.Table[LinkerDetail](_tableTag, "LinkerDetails") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def linkerId = column[Option[Long]]("linkerId")
    def status = column[Option[String]]("status")
    def pKey = column[Option[String]]("pKey")
    def shareCode = column[Option[String]]("shareCode")
    def active = column[Option[Boolean]]("active")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, linkerId, status, pKey, shareCode, active, createdAt, updatedAt, deletedAt) <> (LinkerDetail.tupled, LinkerDetail.unapply)
  }

  lazy val LinkerDetailsTable = new TableQuery(tag => new LinkerDetailsTable(tag))

  /* Things Table Definitions */
  class ThingsTable(_tableTag: Tag) extends profile.api.Table[Thing](_tableTag, "Things") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def linkerId = column[Option[Long]]("linkerId")
    def `type` = column[Option[String]]("type")
    def macAddress = column[Option[String]]("macAddress")
    def active = column[Option[Boolean]]("active")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, linkerId, `type`, macAddress, active, createdAt, updatedAt, deletedAt) <> (Thing.tupled, Thing.unapply)
  }

  lazy val ThingsTable = new TableQuery(tag => new ThingsTable(tag))
}

