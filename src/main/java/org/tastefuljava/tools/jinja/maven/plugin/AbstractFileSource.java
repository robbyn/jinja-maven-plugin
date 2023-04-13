package org.tastefuljava.tools.jinja.maven.plugin;

import java.io.File;

public abstract class AbstractFileSource implements ValueSource {    
    private String fileName;

    public void set(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    protected File getFile(File defaultDir) {
        File file = new File(getFileName());
        if (!file.isAbsolute()) {
            file = new File(defaultDir, getFileName());
        }
        return file;
    }
}
