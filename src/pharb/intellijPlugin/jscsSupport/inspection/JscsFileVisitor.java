package pharb.intellijPlugin.jscsSupport.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSFile;
import pharb.intellijPlugin.jscsSupport.parser.CheckstyleXMLParser;
import pharb.intellijPlugin.jscsSupport.parser.MessageContainer;
import pharb.intellijPlugin.jscsSupport.runner.JscsNativeRunner;

import java.util.List;

public class JscsFileVisitor extends JSElementVisitor {

    private final ProblemsHolder problemsHolder;

    public JscsFileVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    @Override
    public void visitJSFile(final JSFile file) {
        super.visitFile(file);

        if (isJavaScriptFile(file)) {
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

    private void checkJSFile(JSFile file) {
        String jscsResult = new JscsNativeRunner(
                file.getName(),
                file.getProject().getBaseDir().getCanonicalPath()).runJscs(file.getText()
        );
        List<MessageContainer> problemMessages = CheckstyleXMLParser.parse(jscsResult);

        for (MessageContainer message : problemMessages) {
            JscsInspection.registerProblem(problemsHolder, file, message);
        }
    }
}
