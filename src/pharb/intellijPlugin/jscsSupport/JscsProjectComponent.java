package pharb.intellijPlugin.jscsSupport;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import pharb.intellijPlugin.jscsSupport.dialog.JscsDialog;

public class JscsProjectComponent implements ProjectComponent {

    public final String VERSION = "0.2.0";

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
        String key = "jscsPluginVersionLastUsed";
        PropertiesComponent properties = PropertiesComponent.getInstance();
        String versionLastUsed = properties.getValue(key);

        if (versionLastUsed == null || !versionLastUsed.equals(VERSION)) {
            properties.setValue(key, VERSION);
            JscsDialog.showPluginFirstUseDialog();
        }
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
