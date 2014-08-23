package pharb.intellijPlugin.jscsSupport.inspection;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.inspections.JSInspection;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pharb.intellijPlugin.jscsSupport.parser.MessageContainer;

public class JscsInspection extends JSInspection {


    public static void registerProblem(ProblemsHolder problemsHolder, JSFile file, MessageContainer message) {
        PsiElement problemElement = findPsiElement(message.line, message.column, file);
        problemsHolder.registerProblem(problemElement, message.getDisplayMessage(), LocalQuickFix.EMPTY_ARRAY);
    }

    private static PsiElement findPsiElement(int line, int column, JSFile file) {
        com.intellij.openapi.editor.Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);

        //intellij starts counting here at line 0 and column 0
        int elementOffset = document.getLineStartOffset(line - 1) + (column - 1);

        PsiElement found = file.findElementAt(elementOffset);


        if (found == null) {
            return file;
        } else {
            return found;
        }
    }

    @NotNull
    @Override
    protected PsiElementVisitor createVisitor(final ProblemsHolder problemsHolder, final LocalInspectionToolSession localInspectionToolSession) {
        return new JscsFileVisitor(problemsHolder);
    }

    @NotNull
    @Override
    public String[] getGroupPath() {
        return new String[]{
                "JavaScript",
                JSBundle.message("js.linters.inspection.group.name")
        };
    }

    @Nullable
    @Override
    public String getStaticDescription() {
        return "jscs support for Intellij. \n\n" +
                "This is a alpha preview version. \n\n" +
                "More Information at: https://github.com/Pharb/jscs-intellij-plugin";
    }
}
