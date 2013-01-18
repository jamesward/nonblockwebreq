package controllers

import play.api.mvc.{Result, Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise, future}
import scala.concurrent.duration._
import play.api.Play.current
import play.api.Logger
import play.api.libs.concurrent.Akka

object PausingController extends Controller {

  def pause(duration: Int) = Action {
    Async {
      val blocker = Promise[Int]
      Akka.system.scheduler.scheduleOnce(duration seconds) {
        blocker.success(duration)
      }
      blocker.future map { duration =>
        Ok(duration.toString)
      }
    }
  }

  def empty() = Action {
    Async {
      val blocker = Promise[Any]
      blocker.success()
      blocker.future map { nothing =>
        Ok("")
      }
    }
  }

}
