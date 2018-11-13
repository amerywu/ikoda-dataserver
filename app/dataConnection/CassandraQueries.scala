package dataConnection


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.cassandra.scaladsl.CassandraSource
import akka.stream.scaladsl.{Sink, Source}
import com.datastax.driver.core.{Cluster, Row, Session, SimpleStatement}

import javax.inject.Inject
import play.api.Configuration

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
class CassandraQueries @Inject()(configuration: Configuration){

  lazy implicit val session:Session = initSession()



  lazy val connectionAddress:Try[String]= getConnectionAddress
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  def getConnectionAddress(): Try[String] =
  {
    Try(configuration.get[String]("cassandra.host.address"))
  }



  def initSession(): Session =
  {
    try
      {
    connectionAddress.isSuccess match {
      case true =>
        Cluster.builder
          .addContactPoint(connectionAddress.get)
          .withPort(9042)
          .build
          .connect()
      case false => throw connectionAddress.failed.get
    }

    }
    catch
      {
        case  e:Exception => throw new Exception(e.getMessage,e)
      }
  }

  def init(): String =
  {
    try {
        session.getState.toString
    }
    catch
      {
        case e:Exception => "Failed with "+e.getMessage
      }
  }


  def keyspaces():Future[Seq[Row]]=
  {
    val stmt = new SimpleStatement("SELECT * FROM system_schema.keyspaces;").setFetchSize(200)
    val sb:StringBuilder= new StringBuilder()
    val source = CassandraSource(stmt)
    source.runWith(Sink.seq)
  }

}
