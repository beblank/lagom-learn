package com.example.consumer.impl

import com.example.consumer.api.{ConsumeRequest, ConsumerService}
import com.example.token.api.{TokenService, ValidateTokenRequest}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext

class ConsumerServiceImpl(tokenService: TokenService)(implicit ec: ExecutionContext)  extends ConsumerService {
  override def consume = ServiceCall { request =>
    val validateTokenRequest = ValidateTokenRequest(request.clientId, request.token)
    tokenService.validateToken.invoke(validateTokenRequest).map(_.successful)
  }
}

