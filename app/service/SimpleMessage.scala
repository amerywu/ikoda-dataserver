package service

import play.api.libs.json._
import play.api.libs.functional.syntax._

object SimpleMessage
{
  //////////////////////////////

  implicit val mWrites = new Writes[SimpleMessage] {
    def writes(m: SimpleMessage) = Json.obj(
      "id" -> m.id.getOrElse[Int](0),
      "message" -> m.message,
      "uuid" -> m.uuid
    )
  }

  implicit val mReads: Reads[SimpleMessage] = (
    (__ \ "id").readNullable[Int] and
      (JsPath \ "message").read[String] and
      (JsPath \ "uuid").read[String]
    )(SimpleMessage.apply _)
  //////////////////////////////
}
case class SimpleMessage(id: Option[Int],message:String,uuid:String) {

}
