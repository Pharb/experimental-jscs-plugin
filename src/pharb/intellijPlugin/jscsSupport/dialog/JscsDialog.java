package pharb.intellijPlugin.jscsSupport.dialog;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.intellij.openapi.ui.Messages.YES;
import static pharb.intellijPlugin.jscsSupport.util.PluginProperties.GlobalPluginProperties.JSCS_NATIVE_MIN_VERSION_REQUIREMENT;

public class JscsDialog {

    private static Project project;

    private static AtomicBoolean invalidVersionDialogShown = new AtomicBoolean(false);

    public static void init(Project project) {
        JscsDialog.project = project;
    }

    public static void showAskForInterruptDialog(final String fileName, final Runnable yesRunnable, final Runnable noRunnable) {

        if (project == null || !project.isOpen()) {
            throw new IllegalStateException("Project in JscsDialog is not set or is closed!");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int choice = Messages.showYesNoDialog(
                        project,
                        "Jscs is not responding for 10 seconds while checking " + fileName + ".\n\n" +
                                "Do you  want to stop the jscs process for this file?",
                        "Jscs Not Responding",
                        "Yes, abort for this file.",
                        "No, continue.",
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
                "This jscs plugin is currently an alpha version. \n\n" +
                        "Jscs currently has to be installed globally with npm. \n" +
                        "Only Linux is currently supported.\n" +
                        "Make sure your configuration file [.jscsrc] is in the project root directory! \n",
                "Jscs Plugin Information"
        );
    }

    public static void showInvalidJscsVersion() {
        if (!invalidVersionDialogShown.getAndSet(true)) {
            final String minRequiredVersion = JSCS_NATIVE_MIN_VERSION_REQUIREMENT.get();

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Messages.showWarningDialog(
                            "This jscs plugin requires version " + minRequiredVersion + " or later of jscs to work correctly.\n\n" +
                                    "Currently jscs also has to be installed globally.",
                            "Jscs No Valid Version Found"
                    );
                }
            });
        }
    }
}