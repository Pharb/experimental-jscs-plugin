package pharb.intellijPlugin.jscsSupport;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import pharb.intellijPlugin.jscsSupport.dialog.JscsDialog;
import pharb.intellijPlugin.jscsSupport.util.PluginProperties;

import static pharb.intellijPlugin.jscsSupport.util.PluginProperties.GlobalPluginProperties;

public class JscsProjectComponent implements ProjectComponent {

    public static final String VERSION = "0.2.0";

    private final Project project;

    public JscsProjectComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {
        JscsDialog.init(project);
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "JscsProjectComponent";
    }

    public void projectOpened() {
        if (PluginProperties.isVersionFirstUsed()) {
            GlobalPluginProperties.JSCS_PLUGIN_VERSION_LAST_USED.set(VERSION);
            JscsDialog.showPluginFirstUseDialog();
        }
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
