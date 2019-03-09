package json

import scala.collection.mutable.ArrayBuffer

/**
  *
  *  JSON Serializer
  *
  *
  */
object JSONSerializer {
  def serialize(member:Any):String = {
    member match {
      case _: List[Any] => {
        val buf = new StringBuffer("[")
        val asList = member.asInstanceOf[List[Any]]
        val serList = new ArrayBuffer[String]()
        asList foreach(item => {
          serList += serialize(item)
        })
        buf.append(serList.mkString(",")).append("]").toString
      }
      case _: Map[String,Any] => {
        val map = member.asInstanceOf[Map[String,Any]]
        val list = new ArrayBuffer[String]()
        map foreach (x => {
          val buf = new StringBuffer()
          buf.append("\"").append(x._1).append("\"").append(":")
          list += (buf.append(serialize(x._2)).toString)
        })
        s"{${list.mkString(",")}}"
      }
      case _ => {
        f(member).toString
      }
    }
  }

  def f[T](v: T) = v match {
    case _: String => "\"" + v + "\""
    case _         => {
      if (v != null)
        v
      else
        "null"
    }
  }
}
