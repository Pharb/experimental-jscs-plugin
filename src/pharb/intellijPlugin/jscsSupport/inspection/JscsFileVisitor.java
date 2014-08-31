package pharb.intellijPlugin.jscsSupport.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import pharb.intellijPlugin.jscsSupport.dialog.JscsDialog;
import pharb.intellijPlugin.jscsSupport.parser.CheckstyleXMLParser;
import pharb.intellijPlugin.jscsSupport.parser.MessageContainer;
import pharb.intellijPlugin.jscsSupport.runner.JscsNativeRunner;
import pharb.intellijPlugin.jscsSupport.util.PluginProperties;

import java.util.List;

public class JscsFileVisitor extends JSElementVisitor {

    private final ProblemsHolder problemsHolder;

    public JscsFileVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    @Override
    public void visitJSFile(final JSFile file) {
        super.visitFile(file);

        if (isJavaScriptFile(file) && isVisitingPossible(file)) {
            System.out.println("Visiting: " + file.getVirtualFile().getCanonicalPath());
            checkJSFile(file);
        }
    }

    /**
     * Primarily used to filter out JSON files.
     *
     * @param file
     * @return whether or not Intellij thinks, that the file is JavaScript
     */
    private boolean isJavaScriptFile(JSFile file) {
        return file.getLanguage().is(Language.findLanguageByID("JavaScript"));
    }

    private boolean isVisitingPossible(JSFile file) {
        return checkNativeVersionRequirementSatisfied() && !isFileTooLarge(file);
    }

    private boolean checkNativeVersionRequirementSatisfied() {
        boolean validVersion = PluginProperties.isNativeVersionRequirementSatisfied(JscsNativeRunner.getJscsVersion());
        if (!validVersion) {
            JscsDialog.showInvalidJscsVersion();
        }
        return validVersion;
    }

    private boolean isFileTooLarge(JSFile file) {
        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        return document.getLineCount() > 1000; //TODO: show dialog
    }

    private void checkJSFile(JSFile file) {
        String jscsResult = new JscsNativeRunner(
                file.getName(),
                file.getProject().getBaseDir().getCanonicalPath()
        ).runJscs(file.getText());

        List<MessageContainer> problemMessages = CheckstyleXMLParser.parse(jscsResult);

        for (MessageContainer message : problemMessages) {
            JscsInspection.registerProblem(problemsHolder, file, message);
        }
    }
}
