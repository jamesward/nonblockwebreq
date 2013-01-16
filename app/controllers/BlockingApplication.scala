package controllers

import play.api.mvc.{Action, Controller}
import concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global

object BlockingApplication extends Controller {
  
  def pause(msg: String, duration: Int) = Action {
    Async {
      future {
        println(s"Start $msg")
        Thread.sleep(1000 * duration)
        println(s"Finish $msg")
        msg
      } map { msg =>
        Ok(msg)
      }
    }
  }

}
