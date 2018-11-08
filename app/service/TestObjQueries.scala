package service

import model.TestObject

object TestObjQueries {


  def testObjects():Seq[TestObject]=
  {
    val t1=TestObject(Some(1),"gropple@werty.com",Some("Gropllewick"),Some("Thring"))
    val t2=TestObject(Some(1),"tropple@werty.com",Some("Tropllewick"),Some("Fring"))
    val t3=TestObject(Some(1),"jopple@werty.com",Some("Jopllewick"),Some("Kring"))
    Seq(t1,t2,t3)
  }

}
