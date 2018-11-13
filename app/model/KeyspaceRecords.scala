package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

object KeyspaceRecords
{
  implicit val ksseqWrites = new Writes[KeyspaceRecords] {
    def writes(ksseq: KeyspaceRecords) = Json.obj(
      "id" -> ksseq.id.getOrElse[Int](0),
      "records" -> ksseq.records
    )
  }

  implicit val ksseqReads: Reads[KeyspaceRecords] = (
    (__ \ "id").readNullable[Int] and
      (JsPath \ "records").read[Seq[KeyspaceRecord]]
    )(KeyspaceRecords.apply _)
}

case class KeyspaceRecords(id: Option[Int],records:Seq[KeyspaceRecord]) {

}
