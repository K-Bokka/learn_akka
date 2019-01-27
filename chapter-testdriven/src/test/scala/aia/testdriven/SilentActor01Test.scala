package aia.testdriven

import org.scalatest.{MustMatchers, WordSpecLike}
import akka.testkit.{TestActorRef, TestKit}
import akka.actor._

//This test is ignored in the BookBuild, it's added to the defaultExcludedNames

class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {
  "A Silent Actor" must {
    "change state when it receives a message, single threaded" in {
      import SilentActor._
      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must contain("whisper")
    }
    "change state when it receives a message, multi-threaded" in {
      import SilentActor._
      val silentActor = system.actorOf(Props[SilentActor], "s3")
      silentActor ! SilentMessage("whisper1")
      silentActor ! SilentMessage("whisper2")
      silentActor ! GetState(testActor)

      expectMsg(Vector("whisper1", "whisper2"))
    }
  }

}

object SilentActor {

  case class SilentMessage(data: String)

  case class GetState(receiver: ActorRef)

}

class SilentActor extends Actor {

  import SilentActor._

  var internalState: Vector[String] = Vector[String]()

  def receive = {
    case SilentMessage(data) => internalState = internalState :+ data
    case GetState(receiver) => receiver ! internalState
  }

  def state = internalState
}

