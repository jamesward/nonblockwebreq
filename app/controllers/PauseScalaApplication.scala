package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext.Implicits.global
import concurrent.duration._
import concurrent.Await

object PauseScalaApplication extends Controller {

  // execution occupies a thread until completed
  def sync = Action { implicit request =>
    val three = Await.result(WS.url(routes.BlockingApplication.pause("three", 3).absoluteURL()).get(), 10 seconds) // block here
    val one = Await.result(WS.url(routes.BlockingApplication.pause("one", 1).absoluteURL()).get(), 10 seconds) // block here
    val four = Await.result(WS.url(routes.BlockingApplication.pause("four", 4).absoluteURL()).get(), 10 seconds) // block here

    Ok(one.body + three.body + four.body)
  }

  // execution occupies a thread until completed
  def partialAsync = Action { implicit request =>
    val pauseForThreeFuture = WS.url(routes.BlockingApplication.pause("three", 3).absoluteURL()).get() // schedule now
    val pauseForOneFuture = WS.url(routes.BlockingApplication.pause("one", 1).absoluteURL()).get() // schedule now
    val pauseForFourFuture = WS.url(routes.BlockingApplication.pause("four", 4).absoluteURL()).get() // schedule now

    // order doesn't matter
    val three = Await.result(pauseForThreeFuture, 10 seconds)
    val one = Await.result(pauseForOneFuture, 10 seconds)
    val four = Await.result(pauseForFourFuture, 10 seconds)
    
    Ok(one.body + three.body + four.body)
  }

  // execution only occupies a thread when needed
  def fullAsync = Action { implicit request =>
    Async {
      val pauseForThreeFuture = WS.url(routes.BlockingApplication.pause("three", 3).absoluteURL()).get() // schedule now
      val pauseForOneFuture = WS.url(routes.BlockingApplication.pause("one", 1).absoluteURL()).get() // schedule now
      val pauseForFourFuture = WS.url(routes.BlockingApplication.pause("four", 4).absoluteURL()).get() // schedule now

      for {
        three <- pauseForThreeFuture
        one <- pauseForOneFuture
        four <- pauseForFourFuture
      } yield Ok(one.body + three.body + four.body)
    }
  }

}
