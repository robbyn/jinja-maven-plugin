package org.tastefuljava.tools.jinja.maven.plugin;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.Context;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public class Define implements ValueSource {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void putValues(
            Jinjava jinja, Map<String, Object> context, File srcDir)
            throws MojoExecutionException {
        Context scope = new Context(jinja.getGlobalContext(), context);
        JinjavaInterpreter inter = new JinjavaInterpreter(
                jinja, scope, jinja.getGlobalConfig());
        context.put(name, inter.resolveELExpression(value, 0));
    }
}
