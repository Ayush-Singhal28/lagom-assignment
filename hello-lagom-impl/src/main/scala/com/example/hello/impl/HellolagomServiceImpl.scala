package com.example.hello.impl

import akka.NotUsed
import com.example.hello.api
import com.example.hello.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.concurrent.{ExecutionContext, Future}


/**
  * Implementation of the HellolagomService.
  */
class HellolagomServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, externalService: ExternalService)(implicit ec: ExecutionContext) extends HellolagomService  {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the hello-lagom entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HellolagomEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }



  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the hello-lagom entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HellolagomEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(HellolagomEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[HellolagomEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }

  def age(id: Int) = ServiceCall { request =>
    Future.successful(id)
  }

  def empInfo(emp: EmpDetails) = ServiceCall { request =>
    Future.successful(emp)
  }

  def getEmpDetails(id: Int): ServiceCall[NotUsed,List[EmpDetails]] = ServiceCall { request =>
    val emp1 = EmpDetails(1,"ayush","knoldus")
    val emp2 = EmpDetails(2,"vaibhav","byju")
    val emp3 = EmpDetails(3,"divik","capgemini")
    val empList = List(emp1,emp2,emp3)
    val filteredList = empList.filter(_.id == id)
     Future.successful(filteredList)
  }

  override def greetUser(name: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    Future.successful("Hi, " + name)
  }

  override def testUser(): ServiceCall[NotUsed, UserData] = ServiceCall { _ =>
    val result: Future[UserData] = externalService.getUser().invoke()
    result.map(response => response)
  }


}




