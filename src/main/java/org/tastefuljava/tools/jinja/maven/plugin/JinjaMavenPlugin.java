package org.tastefuljava.tools.jinja.maven.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class JinjaMavenPlugin extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(required=false)
    private String json;

    @Parameter(required=false)
    private String jsonFile;

    @Parameter(required=false)
    private String yaml;

    @Parameter(required=false)
    private String yamlFile;

    @Parameter(required = true)
    private List<Rendering> renderings;

    @Parameter(property = "gina.jinja.sourceDirectory",
            defaultValue = "${basedir}/src/main/jinja")
    private File sourceDirectory;

    @Parameter(property = "javacc.outputDirectory",
            defaultValue = "${project.build.directory}/generated-resources/jinja")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JinjavaConfig jc
                = JinjavaConfig.newBuilder()
//                      .withFailOnUnknownTokens(failOnMissingValues)
                        .build();
        Jinjava jinja = new Jinjava(jc);
        Rendering topRendering = new Rendering();
        topRendering.setJsonFile(jsonFile);
        topRendering.setJson(json);
        topRendering.setYamlFile(yamlFile);
        topRendering.setYaml(yaml);
        Map<String, Object> context = topRendering.buildContext(
                null, sourceDirectory);
        for (Rendering rendering: renderings) {
            rendering.render(jinja,
                    sourceDirectory,
                    outputDirectory,
                    rendering.buildContext(
                            context, sourceDirectory));
        }
        if (outputDirectory.isDirectory()) {
            addResourceDir(outputDirectory);
        }
    }

    private void addResourceDir(File dir) {
        Resource resource = new Resource();
        resource.setDirectory(dir.getAbsolutePath());
        project.addResource(resource);
    }
}
