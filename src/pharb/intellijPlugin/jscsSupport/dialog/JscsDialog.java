package pharb.intellijPlugin.jscsSupport.dialog;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.awt.*;

import static com.intellij.openapi.ui.Messages.YES;

public class JscsDialog {

    private static Project project;

    public static void init(Project project) {
        JscsDialog.project = project;
    }

    public static void showAskForInterruptDialog(final Runnable yesRunnable, final Runnable noRunnable) {

        if (project == null || !project.isOpen()) {
            throw new IllegalStateException("Project in JscsDialog is not set or is closed!");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int choice = Messages.showYesNoDialog(
                        project,
                        "Jscs is not responding for 10 seconds.\n\n" +
                                "Do you  want to stop jscs checking this file?",
                        "Jscs Not Responding",
                        "Yes, stop jscs for this file.",
                        "No, continue jscs for this file.",
                        null
                );
                if (choice == YES) {
                    yesRunnable.run();
                } else {
                    noRunnable.run();
                }
            }
        });
    }

    public static void showPluginFirstUseDialog() {
        Messages.showInfoMessage(
                "This jscs plugin is currently a pre-alpha prototype. \n\n" +
                        "You have to install the latest jscs development version globally to use this plugin currently: \n\n" +
                        "sudo npm install -g mdevils/node-jscs \n\n\n" +
                        "Only Linux is currently supported.\n" +
                        "Make sure .jscsrc is in the project root directory! \n",

                "Jscs Plugin Information"
        );
    }
}