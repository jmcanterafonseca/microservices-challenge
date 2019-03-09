package utils

import json.JSONParser

/**
  *
  * JSON Parser utilities
  *
  *
  *
  */
object ParserUtil {
  def parse(data:String) ={
    val parser = new JSONParser
    parser.parse(parser.value, data) match {
      case parser.Success(matched,_) => matched
      case parser.Failure(msg,_) => throw new Exception(s"Unexpected failure: ${msg}")
      case parser.Error(msg,_) => throw new Exception(s"Unexpected error: ${msg}")
    }
  }

  def parseObj(data:String) = {
    parse(data:String).asInstanceOf[Map[String,Any]]
  }
}
