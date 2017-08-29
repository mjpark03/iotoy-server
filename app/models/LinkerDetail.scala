package models

import com.google.inject.Inject
import common.Common
import models.Tables.{LinkerDetailsTable, LinkersTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
class LinkerDetailRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val LinkerDetails = TableQuery[LinkerDetailsTable]
  private[models] val Linkers = TableQuery[LinkersTable]

  def findLinkerDetailByMacAddress(macAddress: String): Option[Seq[LinkerDetail]] = {

    val query = for {
      (_, linkerDetail) <-
      Linkers.filter(_.macAddress === macAddress).filter(_.deletedAt.isEmpty)
        .join(LinkerDetails).on(_.id === _.linkerId).filter(_._2.deletedAt.isEmpty)
    } yield (linkerDetail)

    Option(Await.result(db.run(query.result), Common.COMMON_ASYNC_DURATION))
  }

}
