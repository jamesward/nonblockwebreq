package controllers

import views.html.index
import play.api.mvc.{Action, Controller}
import play.api.libs.ws.{Response, WS}
import concurrent.{duration, Await, Future, future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._

object ScalaApplication extends Controller {
  
  def getPlaySocialStuff = Action {
    Async {
      // queue the requests
      val twitterPromise = WS.url("http://search.twitter.com/search.json").withQueryString(("q", "playframework")).get()
      val githubPromise = WS.url("https://api.github.com/legacy/repos/search/playframework").get()

      // wait for responses and when they arrive, render the template and return a 200 response
      for {
        twitterResponse <- twitterPromise
        githubResponse <- githubPromise
      } yield Ok(index.render((twitterResponse.json \\ "text").map(_.as[String]), (githubResponse.json \\ "name").map(_.as[String])))
    }
  }
  
  def simple = Action {

    def pause(msg:String, duration:Int): String = {
      println(s":>$msg")
      Thread.sleep(1000 * duration)
      println(s"*>$msg")
      msg
    }
    
    Async {
      val t = future { pause("three", 3) }
      val o = future { pause("one", 1) }
      val f = future { pause("four", 4) }
      
      println("scheduled 3 futures")
      
      val futureResult = for {
        three <- t
        one <- o
        four <- f
      } yield one + three + four

      println("have a future result")

      val futureResponse = futureResult.map { response =>
        println("sending response")
        Ok(response)
      }
      
      println("async off and running")
      
      futureResponse
    }
  }

}
