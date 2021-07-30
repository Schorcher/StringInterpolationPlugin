package io.github.schorcher.stringInterpolation;

import com.intellij.lang.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
public class StringExpression {
    private String expression;
    private int offset;
    private Language language;
}
