package service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.cassandra.scaladsl.CassandraSource
import akka.stream.scaladsl.Sink
import com.datastax.driver.core.{Row, Session, SimpleStatement}
import dataConnection.CassandraQueries
import model.{KeyspaceRecord, KeyspaceRecords}
import play.api.{Configuration, Logger}
import play.api.libs.json.Json

import scala.collection.mutable
import scala.util.{Failure, Success}

object Triage
{
  val currentRequests:mutable.HashMap[String,String ]=new mutable.HashMap[String, String]()
}

class Triage(configuration:Configuration) {


  lazy implicit val session:Session = initSession()
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  def initSession():Session=
  {
    val cq=new CassandraQueries(configuration)
    cq.initSession()
  }

  def triage (methodf:String,uuid:String): Unit =
  {
    methodf match
    {
      case "ks" =>
        Logger.info("got ks")
        val cq:CassandraQueries=new CassandraQueries(configuration)
        cq.simpleQuery("SELECT * FROM system_schema.keyspaces;",keyspacem(uuid))
    }
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
        Triage.currentRequests.put(uuid,ksJson.toString())

      case false => Triage.currentRequests.put(uuid,message.getOrElse("No idea what went wrong here, sorry :{"))
    }
  }




}
