package com.github.schorcher.stringInterpolation;

import com.github.schorcher.stringInterpolator.StringInterpolation;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.psi.JavaTokenType.STRING_LITERAL;
import static com.intellij.psi.JavaTokenType.TEXT_BLOCK_LITERAL;


public class InterpolatedStringInjector implements LanguageInjector {

    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        PsiLiteralExpressionImpl stringLiteral = getStringLiteral(host);
        if (stringLiteral == null)
            return;

        if (!isStringInterpolationEnabled(stringLiteral))
            return;

        String hostText = host.getText();
        List<StringExpression> expressions = ExpressionParser.parse(index -> isEscaped(hostText, index),hostText);

        if (expressions.isEmpty())
            return;

        for(StringExpression expression : expressions){
            TextRange textRange = new TextRange(expression.getOffset(),expression.getOffset() + expression.getExpression().length());
            injectionPlacesRegistrar.addPlace(expression.getLanguage(), textRange, "class SomeClassHereA {  Object someFieldHereB = ", ";}");
        }
    }

    private boolean isStringInterpolationEnabled(PsiElement element){
        if (element == null)
            return false;

        if (element instanceof PsiModifierListOwner) {
            for (PsiAnnotation annotation : ((PsiModifierListOwner)element).getAnnotations()) {
                if (StringInterpolation.class.getTypeName().equals(annotation.getQualifiedName()))
                    return true;
            }
        }

        PsiElement parent = element.getParent();
        if (parent == null || parent instanceof com.intellij.psi.PsiJavaFile)
            return false;

        return isStringInterpolationEnabled(parent);
    }

    private boolean isEscaped(String hostText, int index) {
        return index > 0 && hostText.charAt(index - 1) == '\\' && index > 1 && hostText.charAt(index - 2) != '\\';
    }

    private PsiLiteralExpressionImpl getStringLiteral(@NotNull PsiLanguageInjectionHost host){
        if (host.getLanguage().isKindOf(JavaLanguage.INSTANCE) && host instanceof PsiLiteralExpressionImpl){
            PsiLiteralExpressionImpl literalExpression = (PsiLiteralExpressionImpl) host;
            if ((literalExpression.getLiteralElementType() == STRING_LITERAL) || (literalExpression.getLiteralElementType() == TEXT_BLOCK_LITERAL))
                return literalExpression;
        }
        return null;
    }

}
