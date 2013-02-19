package controllers

import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, future}
import scala.concurrent.duration._
import play.api.Play.current
import play.api.libs.concurrent.{Promise, Akka}

object PausingController extends Controller {

  def pause(duration: Int) = Action {
    Async {
      Promise.timeout(Ok(duration.toString), duration seconds)
    }
  }

}
