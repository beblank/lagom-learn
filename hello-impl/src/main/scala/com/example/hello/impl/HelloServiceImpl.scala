package com.example.hello.impl

import akka.{Done, NotUsed}
import com.example.hello.api
import com.example.hello.api.HelloService
import com.lightbend.lagom.scaladsl.api.{ServiceCall, ServiceLocator}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Implementation of the HelloService.
  */
class HelloServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, serviceLocator: ServiceLocator)(implicit ex:ExecutionContext) extends HelloService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the Hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the Hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(HelloEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[HelloEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }

  override def healthCheck() = ServiceCall { _ =>
    Await.result(serviceLocator.locate("login"), 2 seconds)
    match {
      case Some(serviceUri) => println(s"Service found at: [$serviceUri]")
      case None => println("Service not Found")
    }
    Future.successful(Done)
  }

  override def toUppercase = ServiceCall{x =>
    Future.successful(x.toUpperCase)
  }

  override def toLowercase = ServiceCall{ x =>
    Future.successful(x.toLowerCase)
  }

  override def isEmpty(str: String) = ServiceCall{ _ =>
    Future.successful(str.isEmpty)
  }

  override def areEqual(str1: String, str2: String) = ServiceCall{ _ =>
    Future.successful(str1 == str2)
  }
}
