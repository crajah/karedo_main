package core

import akka.actor.{ActorContext, ActorLogging}
import akka.event.{Logging, LoggingAdapter}

trait WrapLog {

  val log: LoggingAdapter
  /**
   * wrapLog("name",input) <block>
   *
   * To be used for automagically display traces of calls
   * @param method Name to be printed in trace
   * @param input  input to the block to be traced
   * @param log implicit logger to be used
   * @param block to be executed which is producing a resulting value to be traced
   * @tparam S Type or input
   * @tparam T Type produced by block
   * @return will return exactly the same as block
   */
  def wrapLog[S,T]
    (method:String,input:S)
    (block : => T):T =
  {


    log.info(s">>> {$method} Input {$input}")
    val ret=block

    log.info(s"<<< {$method} Returning {$ret}")
    ret
  }

}