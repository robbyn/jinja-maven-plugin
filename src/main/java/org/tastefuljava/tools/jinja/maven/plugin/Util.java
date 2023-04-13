package org.tastefuljava.tools.jinja.maven.plugin;

public class Util {
    private Util() {
        throw new UnsupportedOperationException("Cannot instanciate class");
    }

    public static String varName(String s) {
        StringBuilder buf = new StringBuilder();
        for (char c: s.toCharArray()) {
            if (buf.length() == 0
                    ? Character.isJavaIdentifierStart(c)
                    : Character.isJavaIdentifierPart(c)) {
                buf.append(c);
            } else if (c == '.' || c == '-') {
                buf.append('_');
            }
        }
        return buf.toString();
    }
}
