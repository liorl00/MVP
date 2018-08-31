package mvp.cli

import scala.io.StdIn
import scala.concurrent.Future
import akka.actor.Actor
import akka.util.ByteString
import mvp.MVP.{context, system}
import mvp.actors.Messages.{BlockchainAnswer, HeadersAnswer}
import mvp.cli.Commands._
import mvp.cli.ConsoleActor.{BlockchainRequest, HeadersRequest, SendMyName, UserMessageFromCLI}
import mvp.utils.Base16

class ConsoleActor extends Actor {

  override def receive: Receive = {
    case Response("app help") => showHelp
    case Response("node shutdown") => nodeShutdown()
    case Response("blockchain height") => context.system.actorSelection("/user/stateHolder") ! BlockchainRequest
    case Response("headers height") => context.system.actorSelection("/user/stateHolder") ! HeadersRequest
    case Response("send my name") => context.system.actorSelection("/user/stateHolder") ! SendMyName
    case BlockchainAnswer(blockchain) => showCurrentBlockchainHight(blockchain)
    case HeadersAnswer(blockchain) => showCurrentHeadersHight(blockchain)
    case Response("send message") => println("Введите текст и outputID")
    case Response(string: String) =>
      val words: Array[String] = string.split(' ')
      if (words.head == "sendTx") {
        val (outputID, wordsToSend: Array[String]) =
          if (words.length < 3) (None, words.tail)
          else (Some(Base16.decode(words.last).getOrElse(ByteString.empty)), words.tail.dropRight(1))
        system.actorSelection("/user/stateHolder") !
          UserMessageFromCLI(wordsToSend, outputID.map(_.toArray))
      }
  }
}

object ConsoleActor {

  def consoleListener = Future {
    Iterator.continually(StdIn.readLine("$> ")).foreach(input =>
      system.actorSelection("/user/starter/cliActor") ! Response(input))
  }

  case object SendMyName

  case object BlockchainRequest

  case object HeadersRequest

  case class UserMessageFromCLI(array: Array[String], array1: Option[Array[Byte]])
}