package pharb.intellijPlugin.jscsSupport.error;


public class ErrorReporter {

    public static void throwJscsExecutionFailed(String message, Throwable cause) {
        throw new JscsPluginException("Execution failed: " + message, cause);
    }
}
