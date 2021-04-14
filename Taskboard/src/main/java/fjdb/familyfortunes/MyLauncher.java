package fjdb.familyfortunes;

public class MyLauncher {

    /*
JavaFx was removed from the Java JDK as of Java 9, and as a result means JavaFx is a bit more tricky to run.
Some workarounds explained here:
https://edencoding.com/runtime-components-error/#:~:text=A%20%E2%80%9CRuntime%20Components%20are%20Missing%E2%80%9D%20error%20is%20generated,the%20runtime%20requirements%20are%20met%20prior%20to%20launching.
The first workaround is to launched a JavaFx app using a separate class like this. It should work for IDE and from a Jar.
 */
    public static void main(String[] args) {
        FamilyFortunes.main(args);
    }

    /*
    Otherwise, we need to run with runtime args:
    --module-path /path/to/javafx-sdk-14/lib
    --add-modules javafx.controls,javafx.fxml

    Alternatively, deploy project as modules, with a module-info.java file containing:
    module my.project {
        requires javafx.fxml;
        requires javafx.controls;
        opens my.project to javafx.graphics;
        exports my.project;
    }
     */

}
