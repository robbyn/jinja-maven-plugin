package org.tastefuljava.tools.jinja.maven.plugin;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.RenderResult;
import com.hubspot.jinjava.interpret.TemplateError;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoExecutionException;

public class Rendering {
    private static final String JINJA_EXT = ".j2";

    private String templateFile;
    private String outputFile;
    private List<ValueSource> values;

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public List<ValueSource> getValues() {
        return values;
    }

    public void setValues(List<ValueSource> values) {
        this.values = values;
    }

    public void render(
            Jinjava jinja,
            File srcDir,
            File outDir,
            Map<String, Object> parentContext)
            throws MojoExecutionException {
        try {
            Map<String,Object> context = new HashMap<>(parentContext);
            if (values != null) {
                for (ValueSource vs: values) {
                    vs.putValues(jinja, context, srcDir);
                }
            }
            String temp = templateFile;
            String template = jinja.getResourceLocator().getString(
                    templateFile, UTF_8, null);
            String output = render(jinja, template, context);
            String outFile = outputFile;
            if (outFile == null) {
                if (temp.endsWith(JINJA_EXT)) {
                    outFile = temp.substring(
                            0, temp.length()-JINJA_EXT.length());
                } else {
                    outFile = temp;
                }
            }
            writeTextToFile(output, new File(outDir, outFile));
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error rendering from template: " + templateFile, ex);
        }
    }

    private String render(
            Jinjava jinja, String template, Map<String, Object> context)
            throws MojoExecutionException {
        RenderResult result = jinja.renderForResult(template, context);
        if (result.hasErrors()) {
            throw new MojoExecutionException(result.getErrors().stream()
                    .map(TemplateError::toString)
                    .collect(Collectors.joining("\n")));
        }
        return result.getOutput();
    }

    private void writeTextToFile(String text, File file)
            throws MojoExecutionException {
        File parent = file.getParentFile();
        if (parent != null) {
            if (!parent.mkdirs()) {
                throw new MojoExecutionException(
                    "Error creating directory: " + parent);
            }
        }
        try (Reader reader = new StringReader(text);
                BufferedReader in = new BufferedReader(reader);
                OutputStream stream = new FileOutputStream(file);
                Writer writer = new OutputStreamWriter(stream, UTF_8);
                PrintWriter out = new PrintWriter(writer)) {
            String s = in.readLine();
            while (s != null) {
                out.println(s);
                s = in.readLine();
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error writing text to file: " + file, ex);
        }
    }
}
