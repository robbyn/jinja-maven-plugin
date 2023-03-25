package org.tastefuljava.tools.jinja.maven.plugin;

import com.hubspot.jinjava.Jinjava;
import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public interface ValueSource {
    public void putValues(
            Jinjava jinja, Map<String, Object> context, File srcDir)
            throws MojoExecutionException;
}
