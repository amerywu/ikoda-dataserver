package model

import play.api.libs.json._
import play.api.libs.functional.syntax._
object KeyspaceRecord
{
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
}

case class KeyspaceRecord(id: Option[Int],keyspaceName:String) {

}
