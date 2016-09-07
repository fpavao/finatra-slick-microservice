package org.thedevpiece.finatra.slick.microservices.domain

import com.google.inject.{ Inject, Singleton }
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton
class UserRepository @Inject() (val driver: JdbcProfile) {

  import driver.api._

  class Users(tag: Tag) extends Table[(Option[Long], String, Int, String)](tag, "users") {
    def id = column[Long]("IDT_USER", O.AutoInc, O.PrimaryKey)
    def username = column[String]("DES_USERNAME")
    def age = column[Int]("NUM_AGE")
    def occupation = column[String]("DES_OCCUPATION")

    override def * = (id.?, username, age, occupation)
  }

  val users: TableQuery[Users] = TableQuery[Users]
  val db = Database.forConfig("database")

  def findById(id: Long): Future[Seq[(Option[Long], String, Int, String)]] = {
    val query = users.filter(_.id === id).result
    db.run(query)
  }

  def create(user: (Option[Long], String, Int, String)): Future[Seq[Long]] = {
    val action: DBIO[Seq[Long]] = (users returning users.map(_.id)) ++= List(user)
    db.run(action)
  }

  val insertActions = DBIO.seq(
    users.schema.create
  )

  val setupFuture = db.run(insertActions)
}