package org.tastefuljava.tools.jinja.maven.plugin;

public abstract class AbstractValueSource implements ValueSource {
    private String source;

    public void set(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
