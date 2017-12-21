package com.example.trip.api

import play.api.libs.json.{Format, Json}

case class ReportLocation(latitude:Double, longitude:Double)
object ReportLocation{
    implicit val ReportLocationFormat:
    Format[ReportLocation] = Json.format[ReportLocation]
}