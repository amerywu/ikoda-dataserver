package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import javax.inject._
import model.{KeyspaceRecord, KeyspaceRecords, TestObject}
import play.api.libs.json._
import play.api.mvc._
import service.TestObjQueries
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

  val currentRequests:mutable.HashMap[String,String ]=new mutable.HashMap[String, String]()
  lazy implicit val session:Session = initSession()
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  def initSession():Session=
  {
    val cq=new CassandraQueries(configuration)
    cq.initSession()
  }


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
//////////////////////////////

  implicit val ksWrites = new Writes[KeyspaceRecord] {
    def writes(ksr: KeyspaceRecord) = Json.obj(
      "id" -> ksr.id.getOrElse[Int](0),
      "keyspaceName" -> ksr.keyspaceName
    )
  }

  implicit val ksReads: Reads[KeyspaceRecord] = (
    (__ \ "id").readNullable[Int] and
      (JsPath \ "keyspaceName").read[String]
    )(KeyspaceRecord.apply _)
  //////////////////////////////

  implicit val ksseqWrites = new Writes[KeyspaceRecords] {
    def writes(ksseq: KeyspaceRecords) = Json.obj(
      "id" -> ksseq.id.getOrElse[Int](0),
      "keyspaceName" -> ksseq.records
    )
  }

  implicit val ksseqReads: Reads[KeyspaceRecords] = (
    (__ \ "id").readNullable[Int] and
      (JsPath \ "records").read[Seq[KeyspaceRecord]]
    )(KeyspaceRecords.apply _)

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
    Ok(views.html.jsontest(TestObjQueries.testObjects()))
  }

  def tryjson() = Action { implicit request: Request[AnyContent] =>
    val toObj = TestObject(Some(23),"fasd@ads.bt",Some("tyre"),Some("gwert"))
    Ok(Json.toJson(toObj))

  }

  def tc() = Action { implicit request: Request[AnyContent] =>
    val cq = new CassandraQueries(configuration)
    Ok(views.html.cassandratest(cq.init()))
  }


  val keyspacem = (uuid:String)=>(rows:Option[Seq[Row]],message:Option[String]) =>
    {
      Logger.info("got seq for "+uuid)
      rows.isDefined match
        {
        case true =>
          val seqKs=rows.get.map
          {
            r=>
              Logger.info(r.getString(0))
              KeyspaceRecord(None, r.getString(0))
          }
          val ksJson=Json.toJson(KeyspaceRecords(None,seqKs))
          Logger.debug(ksJson.toString())
          currentRequests.put(uuid,ksJson.toString())

        case false => currentRequests.put(uuid,message.getOrElse("No idea what went wrong here, sorry :{"))
      }
    }

  def factory (methodf:String,uuid:String): Unit =
  {
    methodf match
    {
      case "ks" =>
        Logger.info("got ks")
        keyspacesa("SELECT * FROM system_schema.keyspaces;",keyspacem(uuid))
    }
  }

  def keyspacesa[A](query:String, out:(Option[Seq[Row]], Option[String]) => A):Unit=
  {
    try {
      val stmt = new SimpleStatement("SELECT * FROM system_schema.keyspaces;").setFetchSize(200)
      val sb: StringBuilder = new StringBuilder()
      val source = CassandraSource(stmt)
      source.runWith(Sink.seq).onComplete {
        case Success(f) => out(Some(f),None)
        case Failure(e) => out(None,Some(e.getMessage))
      }
    }
    catch
      {
        case e:Exception=> Logger.error(e.getMessage,e)
      }
  }


  def keyspaces() = Action { implicit request: Request[AnyContent] =>
      val uuid:String=java.util.UUID.randomUUID().toString
      factory("ks",uuid)
      Ok(views.html.cassandratest(uuid))
  }



}