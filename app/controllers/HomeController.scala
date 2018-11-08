package controllers

import javax.inject._
import model.TestObject

import play.api.libs.json.{Json, Reads, Writes, __}
import play.api.mvc._
import service.TestObjQueries
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


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
    Ok(views.html.jsontest(TestObjQueries.testObjects()))
  }

  def tryjson() = Action { implicit request: Request[AnyContent] =>
    val toObj = TestObject(Some(23),"fasd@ads.bt",Some("tyre"),Some("gwert"))
    Ok(Json.toJson(toObj))

  }





}
