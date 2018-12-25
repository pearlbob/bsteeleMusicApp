package com.bsteele.bsteeleMusicApp.shared;

import com.google.gwt.regexp.shared.RegExp;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Util
{
    public static final int mod(int n, int modulus)
    {
        n = n % modulus;
        if (n < 0)
            n += modulus;
        return n;
    }

    public static final double mod(double n, double modulus)
    {
        n = n % modulus;
        if (n < 0)
            n += modulus;
        return n;
    }

    public final String stripLeadingWhitespace(String s)
    {
        clear();

        if (s == null)
            return null;

        for (; s.length() > 0; ) {
            switch (s.charAt(0)) {
                case '\n':
                    wasNewline = true;
                    //  fall through
                case ' ':
                case '\t':
                case '\r':
                    leadingWhitespaceCount++;
                    s = s.substring(1);
                    continue;
            }
            break;
        }
        if (s.length() == 0)
            return null;
        return s;
    }

    public final int getLeadingWhitespaceCount()
    {
        return leadingWhitespaceCount;
    }

    public final boolean wasNewline()
    {
        return wasNewline;
    }

    public final void clear()
    {
        leadingWhitespaceCount = 0;
        wasNewline = false;
    }

    public static final String camelCaseToReadable(String s)
    {
        final RegExp camelCaseToReadableRegexp = RegExp.compile("([a-z0-9])([A-Z])", "g");
        return camelCaseToReadableRegexp.replace(s, "$1 $2");
    }

    public static final String firstToUpper(String s)
    {
        if (s == null)
            return null;
        if (s.length() < 1)
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private int leadingWhitespaceCount;
    private boolean wasNewline;
}
