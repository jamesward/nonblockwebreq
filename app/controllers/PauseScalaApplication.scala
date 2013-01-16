package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext.Implicits.global
import concurrent.duration._
import concurrent.Await

object PauseScalaApplication extends Controller {
  
  // this handler occupies a thread until completed
  // three web requests run in sequence, each uses an additional thread (using only one additional thread at a time
  def sync = Action { implicit request =>
    val three = Await.result(WS.url(routes.PausingController.pause(3).absoluteURL()).get(), 10 seconds) // block here
    val one = Await.result(WS.url(routes.PausingController.pause(1).absoluteURL()).get(), 10 seconds) // block here
    val four = Await.result(WS.url(routes.PausingController.pause(4).absoluteURL()).get(), 10 seconds) // block here

    Ok(one.body + three.body + four.body)
  }

  // this handler occupies a thread until completed
  // three web requests run in parallel, when active they occupy a thread
  def partialAsync = Action { implicit request =>
    val pauseForThreeFuture = WS.url(routes.PausingController.pause(3).absoluteURL()).get() // schedule now
    val pauseForOneFuture = WS.url(routes.PausingController.pause(1).absoluteURL()).get() // schedule now
    val pauseForFourFuture = WS.url(routes.PausingController.pause(4).absoluteURL()).get() // schedule now

    // order doesn't matter
    val three = Await.result(pauseForThreeFuture, 10 seconds)
    val one = Await.result(pauseForOneFuture, 10 seconds)
    val four = Await.result(pauseForFourFuture, 10 seconds)
    
    Ok(one.body + three.body + four.body)
  }

  // this handler only occupies a thread when active
  // three web requests run in parallel, when active the occupy a thread
  def fullAsync = Action { implicit request =>
    Async {
      val pauseForThreeFuture = WS.url(routes.PausingController.pause(3).absoluteURL()).get() // schedule now
      val pauseForOneFuture = WS.url(routes.PausingController.pause(1).absoluteURL()).get() // schedule now
      val pauseForFourFuture = WS.url(routes.PausingController.pause(4).absoluteURL()).get() // schedule now

      for {
        three <- pauseForThreeFuture
        one <- pauseForOneFuture
        four <- pauseForFourFuture
      } yield Ok(one.body + three.body + four.body)
    }
  }

}
