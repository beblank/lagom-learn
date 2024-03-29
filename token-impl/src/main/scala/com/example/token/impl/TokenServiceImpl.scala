package com.example.token.impl

import java.util.UUID

import com.example.token.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.{ExecutionContext, Future}

class TokenServiceImpl(implicit val ec:ExecutionContext) extends TokenService {

  val permittedIds = Map("123456" -> "in9ne0dfka", "678901" -> "0923nsnx00")
  var tokenStore = Map.empty[String, String]


  override def retrieveToken : ServiceCall[RetrieveTokenRequest,
    RetrieveTokenResult] = ServiceCall{
    request => Future{
      permittedIds.get(request.clientId) match {
        case Some(secret) if secret == request.clientSecret =>
          val token = UUID.randomUUID().toString
          tokenStore += request.clientId -> token
          RetrieveTokenResult(true, Some(token))
      }
    }
  }

  override def validateToken : ServiceCall[ValidateTokenRequest,
    ValidateTokenResult] = ServiceCall{
    request => Future{
      tokenStore.get(request.clientId) match {
        case Some(token) if token == request.token =>
          ValidateTokenResult(true)
        case _ => ValidateTokenResult(false)
      }
    }
  }
}
