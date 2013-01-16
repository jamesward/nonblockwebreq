package controllers

import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import concurrent.{Promise, Future}
import scala.concurrent.duration._
import play.api.Play.current
import play.api.Logger
import play.api.libs.concurrent.Akka
import util.Try

object PausingController extends Controller {
  
  def pause(duration: Int) = Action {
    Async {
      val blocker = Promise[Int]
      Logger.debug(s"Start $duration second wait")
      Akka.system.scheduler.scheduleOnce(duration seconds)(blocker.complete(Try(duration)))
      blocker.future map { duration =>
        Logger.debug(s"Finished $duration second wait")
        Ok(duration.toString)
      }
    }
  }

}
