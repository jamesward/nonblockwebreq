package controllers;

import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Await;
import views.html.*;

public class PauseJavaApplication extends Controller {

    public static Result simple() {
        return ok("");
    }

}
