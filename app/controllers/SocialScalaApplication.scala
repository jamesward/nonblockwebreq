package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._

object SocialScalaApplication extends Controller {
  
  def social = Action {
    Async {
      // queue the requests
      val twitterPromise = WS.url("http://search.twitter.com/search.json").withQueryString(("q", "playframework")).get()
      val githubPromise = WS.url("https://api.github.com/legacy/repos/search/playframework").get()

      // wait for responses and when they arrive, render the template and return a 200 response
      for {
        twitterResponse <- twitterPromise
        githubResponse <- githubPromise
      } yield Ok(views.html.social.render((twitterResponse.json \\ "text").map(_.as[String]), (githubResponse.json \\ "name").map(_.as[String])))
    }
  }

}
