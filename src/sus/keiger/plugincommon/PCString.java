package sus.keiger.plugincommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PCString
{
    // Private static fields.p
    private static final int DEFAULT_MAX_CHARS_PER_LINE = 20;


    // Constructors.
    private PCString() { }


    // Static methods.
    public static String Pluralize(String word, boolean isPlural)
    {
        if (!isPlural)
        {
            return word;
        }
        return word.endsWith("s") ? word : word + "s";
    }

    public static String Pluralize(String word, long count)
    {
        return Pluralize(word, (count != 1) && (count != -1));
    }

    public static String PossessiveForm(String word)
    {
        return word.toLowerCase().endsWith("s") ? word + "'" : word + "'s";
    }

    public static String ExceptionToString(Throwable e)
    {
        return "%s: %s. Stacktrace: %s".formatted(e.getClass().getName(), e.getMessage(),
                String.join("\n    at ", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()));
    }

    public static List<String> SplitTextIntoLines(String text)
    {
        return SplitTextIntoLines(text, DEFAULT_MAX_CHARS_PER_LINE);
    }

    public static List<String> SplitTextIntoLines(String text, int maxCharsPerLine)
    {
        Objects.requireNonNull(text, "text is null");
        if (maxCharsPerLine < 1)
        {
            throw new IllegalArgumentException("Max characters per line must be >= 1");
        }

        List<String> Lines = new ArrayList<>();
        StringBuilder LineBuilder = new StringBuilder();
        int LastWhitespaceCharIndex = -1;
        int LineStartIndex = 0;

        for (int i = 0; i < text.length(); i++)
        {
            char CharCur = text.charAt(i);
            boolean IsCurCharWhitespace = Character.isWhitespace(CharCur);
            if (IsCurCharWhitespace && ((i - LineStartIndex + 1) > maxCharsPerLine))
            {
                LineStartIndex = i + 1;
                Lines.add(LineBuilder.toString());
                LineBuilder.setLength(0);
            }
            else
            {
                LineBuilder.append(CharCur);
            }
            LastWhitespaceCharIndex = IsCurCharWhitespace ? i : LastWhitespaceCharIndex;
        }

        if (!LineBuilder.isEmpty())
        {
            Lines.add(LineBuilder.toString());
        }

        return Lines;
    }
}