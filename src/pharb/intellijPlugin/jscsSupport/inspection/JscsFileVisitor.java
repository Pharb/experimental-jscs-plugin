package pharb.intellijPlugin.jscsSupport.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSFile;
import pharb.intellijPlugin.jscsSupport.runner.JscsNativeRunner;
import pharb.intellijPlugin.jscsSupport.parser.CheckstyleXMLParser;
import pharb.intellijPlugin.jscsSupport.parser.MessageContainer;

import java.util.List;

public class JscsFileVisitor extends JSElementVisitor {

    private final ProblemsHolder problemsHolder;

    public JscsFileVisitor(ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    @Override
    public void visitJSFile(final JSFile file) {
        System.out.println("Visitor for: " + file.getName());
        checkJSFile(file);

        super.visitFile(file);
    }

    private void checkJSFile(JSFile file) {
        String jscsResult = JscsNativeRunner.runJscs(file.getText(), file.getProject().getBaseDir().getCanonicalPath());
        List<MessageContainer> problemMessages = CheckstyleXMLParser.parse(jscsResult);

        for (MessageContainer message : problemMessages) {
            JscsInspection.registerProblem(problemsHolder, file, message);
        }
    }
}
