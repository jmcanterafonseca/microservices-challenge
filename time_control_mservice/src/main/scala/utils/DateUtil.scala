package utils

import java.text.SimpleDateFormat
import java.time.{LocalDateTime}
import java.time.format.DateTimeFormatter

object DateUtil {
  val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX"
  val DATE_FORMAT = "yyyy-MM-dd"

  def toISO8601(date:java.util.Date) = {
    val sdf = new SimpleDateFormat(ISO_8601_FORMAT)
    sdf.format(date)
  }

  def dateToISO8601(date:java.util.Date) = {
    val sdf = new SimpleDateFormat(DATE_FORMAT)
    sdf.format(date)
  }

  def dateToISO8601(date:java.time.LocalDate) = {
    date.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
  }

  def fromISO8601(strDateTime:String) = {
    LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(ISO_8601_FORMAT))
  }
}
