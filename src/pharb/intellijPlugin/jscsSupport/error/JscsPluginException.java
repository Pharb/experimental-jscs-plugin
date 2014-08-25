package pharb.intellijPlugin.jscsSupport.error;


import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.extensions.PluginId;

public class JscsPluginException extends PluginException {

    JscsPluginException(String message, Throwable cause) {
        super(message, cause, PluginId.getId("pharb.intellijPlugin.jscs.id"));
    }

    JscsPluginException(String message) {
        super(message, PluginId.getId("pharb.intellijPlugin.jscs.id"));
    }
}
