package controllers;

import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;

public class PauseJavaApplication extends Controller {

    // this handler occupies a thread until completed
    // three web requests run in sequence, each uses an additional thread (using only one additional thread at a time
    public static Result sync() {
        String three = WS.url(routes.PausingController.pause(3).absoluteURL(request())).setQueryParameter("duration", "3").get().get().getBody(); // block here
        String one = WS.url(routes.PausingController.pause(1).absoluteURL(request())).setQueryParameter("duration", "1").get().get().getBody(); // block here
        String four = WS.url(routes.PausingController.pause(4).absoluteURL(request())).setQueryParameter("duration", "4").get().get().getBody(); // block here

        return ok(one + three + four);
    }

    // this handler occupies a thread until completed
    // three web requests run in parallel, when active they occupy a thread
    public static Result partialAsync() {
        F.Promise<WS.Response> threePromise = WS.url(routes.PausingController.pause(3).absoluteURL(request())).setQueryParameter("duration", "3").get(); // schedule now
        F.Promise<WS.Response> onePromise = WS.url(routes.PausingController.pause(1).absoluteURL(request())).setQueryParameter("duration", "1").get(); // schedule now
        F.Promise<WS.Response> fourPromise = WS.url(routes.PausingController.pause(4).absoluteURL(request())).setQueryParameter("duration", "4").get(); // schedule now

        // order doesn't matter
        String three = threePromise.get().getBody();
        String one = onePromise.get().getBody();
        String four = fourPromise.get().getBody();

        return ok(one + three + four);
    }

    // this handler only occupies a thread when active
    // three web requests run in parallel, when active the occupy a thread
    public static Result fullAsync() {
        final F.Promise<WS.Response> threePromise = WS.url(routes.PausingController.pause(3).absoluteURL(request())).setQueryParameter("duration", "3").get(); // schedule now
        final F.Promise<WS.Response> onePromise = WS.url(routes.PausingController.pause(1).absoluteURL(request())).setQueryParameter("duration", "1").get(); // schedule now
        final F.Promise<WS.Response> fourPromise = WS.url(routes.PausingController.pause(4).absoluteURL(request())).setQueryParameter("duration", "4").get(); // schedule now
        
        return async(
                threePromise.flatMap(
                        new F.Function<WS.Response, F.Promise<Result>>() {
                            public F.Promise<Result> apply(final WS.Response threeResponse) {
                                return onePromise.flatMap(
                                        new F.Function<WS.Response, F.Promise<Result>>() {
                                            public F.Promise<Result> apply(final WS.Response oneResponse) {
                                                return fourPromise.map(
                                                        new F.Function<WS.Response, Result>() {
                                                            public Result apply(final WS.Response fourResponse) {
                                                                return ok(oneResponse.getBody() + threeResponse.getBody() + fourResponse.getBody());
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                                
                                
                )
        );
    }

}
