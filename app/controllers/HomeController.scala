package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import javax.inject._
import model.{KeyspaceRecord, KeyspaceRecords, TestObject}
import play.api.libs.json._
import play.api.mvc._
import service.{SimpleMessage, TestObjQueries, Triage}
import play.api.libs.functional.syntax._
import dataConnection._
import akka.stream.alpakka.cassandra.scaladsl.CassandraSource
import akka.stream.scaladsl.{Sink, Source}
import com.datastax.driver.core.{Cluster, Row, Session, SimpleStatement}
import play.api.Logger
import javax.inject.Inject
import play.api.Configuration

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,configuration: Configuration) extends AbstractController(cc) {





  implicit val toWrites = new Writes[TestObject] {
    def writes(user: TestObject) = Json.obj(
      "id" -> user.id.getOrElse[Int](0),
      "email" -> user.email,
      "firstName" -> user.firstName.getOrElse[String]("_"),
      "lastName" -> user.lastName.getOrElse[String]("_")
    )
  }

  implicit val toReads:  Reads[TestObject] = (
    (__ \ "id").readNullable[Int] and
      (__ \ "email").read[String] and
      (__ \ "firstName").readNullable[String] and
      (__ \ "lastName").readNullable[String]
    )(TestObject.apply _)







  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.index())
  }


  def test() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.test())
  }

  def users() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.testobj(TestObjQueries.testObjects()))
  }

  def jsonstart() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.jsontest())
  }

  def tryjson() = Action { implicit request: Request[AnyContent] =>
    val toObj = TestObject(Some(23),"fasd@ads.bt",Some("tyre"),Some("gwert"))
    Ok(Json.toJson(toObj))

  }

  def tc() = Action { implicit request: Request[AnyContent] =>
    val cq = new CassandraQueries(configuration)
    Ok(views.html.cassandratest(cq.init()))
  }









  def keyspaces() = Action {
    implicit request: Request[AnyContent] =>
      val uuid:String=java.util.UUID.randomUUID().toString
      val sm=SimpleMessage(None,"Retrieving data. It will appear here when the query is complete.",uuid)
      val t=new Triage(configuration)
      t.triage("ks",uuid)
      Ok(Json.toJson(sm))
  }

  def checkin(uuid:String) = Action {
    implicit request: Request[AnyContent] =>
      Logger.info("uuid checked in "+uuid)
      Triage.currentRequests.get(uuid).isDefined match
      {
        case true =>
          val sm=SimpleMessage(None,"ok",uuid)
          Ok(Json.toJson(sm))
        case false =>
          val sm=SimpleMessage(None,"wait",uuid)
          Ok(Json.toJson(sm))
      }
  }


  def data(uuid:String) = Action {
    implicit request: Request[AnyContent] =>
      Logger.info("data for "+uuid)
      Triage.currentRequests.get(uuid).isDefined match
      {
        case true =>
          Logger.info(Triage.currentRequests.get(uuid).get)
          Ok(Triage.currentRequests.get(uuid).get)
        case false =>
          val sm=SimpleMessage(None,"wait",uuid)
          Ok(Json.toJson(sm))
      }
  }





}