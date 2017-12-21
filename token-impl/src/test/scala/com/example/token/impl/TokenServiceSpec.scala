package com.example.token.impl

import com.example.token.api.{RetrieveTokenRequest, TokenService, ValidateTokenRequest, ValidateTokenResult}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class TokenServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup){
    ctx => new TokenApplication(ctx) with LocalServiceLocator
  }

  lazy val serviceClient = server.serviceClient.implement[TokenService]

  "The token service" should {
    "return a token if clientId and clientSecret are correct" in {
      val retreiveTokenRequest = RetrieveTokenRequest("123456", "in9ne0dfka")
      serviceClient.retrieveToken.invoke(retreiveTokenRequest).map{
        response => response.successful shouldBe true
          response.token should not be 'empty
      }
    }

    "validate a valid token" in {
      val retrieveTokenRequest = RetrieveTokenRequest("123456", "in9ne0dfka")
      serviceClient.retrieveToken.invoke(retrieveTokenRequest)
        .flatMap{
          retrieveResponse => val validateTokenRequest = ValidateTokenRequest("123456", retrieveResponse.token.get)
            serviceClient.validateToken.invoke(validateTokenRequest).map{
              validateResponse => validateResponse shouldBe ValidateTokenResult(true)
            }
        }
    }
  }

  override protected def beforeAll() = server

  override protected def afterAll() = server.stop()

}
