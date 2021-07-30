package io.github.schorcher.stringInterpolation;

import com.intellij.lang.java.JavaLanguage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

@Getter @Setter
public class ExpressionParser {

    private String exp;
    private IntPredicate escapeMatcher;
    private int index;

    public ExpressionParser(IntPredicate escapeMatcher, String value) {
        this.exp = value;
        this.escapeMatcher = escapeMatcher;
    }

    public static List<StringExpression> parse(IntPredicate escapeMatcher, String value){
        return new ExpressionParser(escapeMatcher, value).parse();
    }

    private List<StringExpression> parse() {
        List<StringExpression> expressions = new ArrayList<>();
        int length = exp.length();

        for(index = 0; index < length; index++) {
            char c = exp.charAt(index);
            if (c == '$'){
                if (!escapeMatcher.test(index)) {
                    StringExpression expression = parseExpression();
                    if (expression != null)
                        expressions.add(expression);
                }
            }
        }
        return expressions;
    }

    private StringExpression parseExpression() {
        if (index + 1 == exp.length())
            return null;
        return parseBraceExpression();
    }

    private StringExpression parseBraceExpression() {
        int index = this.index + 2, offset = index, length = exp.length();
        StringBuilder expression = new StringBuilder();

        for (; index < length; index++) {
            char c = exp.charAt(index);
            if ( c != '}')
                expression.append(c);
            else {
                if (expression.length() > 0) {
                    this.index = index;
                    return new StringExpression(expression.toString(), offset, JavaLanguage.INSTANCE);
                }
                break;
            }
        }
        return null;
    }

}
