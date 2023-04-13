package org.tastefuljava.tools.jinja.maven.plugin;

import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public interface ValueSource {
    public Map<String, Object> loadValues(File srcDir)
            throws MojoExecutionException;
}
