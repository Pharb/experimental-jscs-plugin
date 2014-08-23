package pharb.intellijPlugin.jscsSupport;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class JscsProjectComponent implements ProjectComponent {

    private final Project project;

    public JscsProjectComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "JscsProjectComponent";
    }

    public void projectOpened() {
        DialogBuilder dialog = new DialogBuilder(project);
        dialog.setTitle("jscs plugin Warning");

        JTextComponent centerText = new JTextPane();
        centerText.setText("This jscs plugin is currently a pre-alpha prototype. \n\n" +
                "You have to install the latest jscs development version globally to use this plugin currently: \n\n" +
                "sudo npm install -g mdevils/node-jscs \n\n\n" +
                "Only Linux is currently supported.\n" +
                "Make sure .jscsrc is in the project root directory! \n");
        dialog.setCenterPanel(centerText);

        dialog.setOkActionEnabled(true);

        dialog.show();
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
