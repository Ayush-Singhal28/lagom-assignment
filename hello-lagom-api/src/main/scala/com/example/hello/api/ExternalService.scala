package com.example.hello.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.{named, pathCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

case class UserData(userId: Int, id: Int, title:String, body: String)

object UserData {
  /**
    * Format for converting user messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[UserData] = Json.format[UserData]
}



trait ExternalService extends Service {
  def getUser(): ServiceCall[NotUsed, UserData]
  override final def descriptor = {
    import Service._
    // @formatter:off
    named("external-service")
      .withCalls(
        pathCall("/posts/1", getUser _)
      ).withAutoAcl(true)
    // @formatter:on
  }
}



