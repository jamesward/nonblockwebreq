package controllers

import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.concurrent.duration._
import play.api.Play.current
import play.api.Logger
import play.api.libs.concurrent.Akka

object PausingController extends Controller {
  
  def pause(duration: Int) = Action {
    Async {
      val blocker = Promise[Int]
      Logger.debug(s"Start $duration second wait")
      Akka.system.scheduler.scheduleOnce(duration seconds)(blocker.success(duration))
      blocker.future map { duration =>
        Logger.debug(s"Finished $duration second wait")
        Ok(duration.toString)
      }
    }
  }

}
