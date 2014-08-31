package pharb.intellijPlugin.jscsSupport;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import pharb.intellijPlugin.jscsSupport.dialog.JscsDialog;
import pharb.intellijPlugin.jscsSupport.util.PluginProperties;

public class JscsProjectComponent implements ProjectComponent {


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
            PluginProperties.setVersionUsed();
            JscsDialog.showPluginFirstUseDialog();
        }
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
