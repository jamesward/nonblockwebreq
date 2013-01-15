package controllers;

import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class JavaApplication extends Controller {

    public static Result getPlaySocialStuff() {
        final F.Promise<WS.Response> twitterPromise = WS.url("http://search.twitter.com/search.json").setQueryParameter("q", "playframework").get();
        final F.Promise<WS.Response> githubPromise = WS.url("https://api.github.com/legacy/repos/search/playframework").get();

        return async(
                twitterPromise.flatMap(
                        new F.Function<WS.Response, F.Promise<Result>>() {
                            public F.Promise<Result> apply(final WS.Response twitterResponse) {
                                return githubPromise.map(
                                        new F.Function<WS.Response, Result>() {
                                            public Result apply(final WS.Response githubResponse) {
                                                return ok(index.render(twitterResponse.asJson().findValuesAsText("text"), githubResponse.asJson().findValuesAsText("name")));
                                            }
                                        }
                                );
                            }
                        }
                )
        );
    }

}
