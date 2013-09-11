/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import me.lachlanap.dependencyanalyser.analysis.AnalysisFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import me.lachlanap.dependencyanalyser.analysis.Analysis;
import me.lachlanap.dependencyanalyser.analysis.ClassResult;
import me.lachlanap.dependencyanalyser.diagram.ClassDiagram;
import me.lachlanap.dependencyanalyser.diagram.PackageDiagram;
import me.lachlanap.dependencyanalyser.graph.Matrix;
import me.lachlanap.dependencyanalyser.processing.ExclusionProcessingFilter;
import me.lachlanap.dependencyanalyser.processing.Processor;
import me.lachlanap.dependencyanalyser.processing.TaskSet;
import org.apache.bcel.util.Repository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * @author lachlan
 */
@Mojo(name = "dep-analyser",
      defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST,
      requiresProject = true)
public class DependencyAnalyserMojo extends AbstractMojo {

    @Component
    private MavenProject project;
    //
    @Parameter(defaultValue = "true")
    private boolean excludePlatformClasses;
    //
    @Parameter
    private String[] exclusions;
    //
    @Parameter(defaultValue = "true")
    private boolean drawGenealogicalDependencies;
    @Parameter(defaultValue = "true")
    private boolean drawStaticDependencies;
    @Parameter(defaultValue = "true")
    private boolean drawExecutableDependencies;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting up");

        Artifact artifact = project.getArtifact();
        if (artifact == null)
            throw new MojoFailureException("Error couldn't access project artifact");

        ArtifactHandler handler = artifact.getArtifactHandler();
        if (!handler.getLanguage().equals("java"))
            throw new MojoFailureException("Must run on a Java project; tried to run on: " + handler.getLanguage());

        File jar = artifact.getFile();
        if (!jar.exists())
            throw new MojoFailureException("JAR artifact (" + jar + ") does not exist");

        processJar(jar);
    }

    private void processJar(File jarFile) throws MojoExecutionException {
        try {
            String destination = project.getBasedir() + "/target/dependency-analyser/";

            Analysis analysis = new Analysis();
            Matrix matrix = new Matrix();
            analyseJar(jarFile, matrix);

            getLog().info("Filtering irrelevant data...");
            AnalysisFilter filter = new AnalysisFilter(excludePlatformClasses, exclusions);
            analysis = filter.filter(analysis);

            writeOutput(destination, analysis);
        } catch (IOException ex) {
            getLog().error("Error processing jar artifact", ex);
            throw new MojoExecutionException("Error processing jar artifact", ex);
        }
    }

    private void analyseJar(File jarFile, Matrix matrix) throws IOException, MalformedURLException {
        URL jar = jarFile.toURI().toURL();
        TaskSet taskSet = new TaskSet();

        getLog().info("Opening jar repository...");
        Repository repo = RepositoryFactory.getRepo(jar);

        getLog().info("Loading class list...");
        JarSpider spider = new JarSpider(taskSet, repo);
        spider.spider(jar);

        getLog().info("Processing...");
        Processor processor = new Processor(repo, taskSet, matrix);
        processor.setProcessingFilter(new ExclusionProcessingFilter(excludePlatformClasses, exclusions));
        processor.run();
    }

    private void writeOutput(String destination, Analysis analysis) throws FileNotFoundException {
        PrintStream ps;

        File dest = new File(destination);
        if (!dest.exists())
            dest.mkdirs();

        getLog().info("Writing class diagram...");

        /*ClassDiagram classDiag = new ClassDiagram();
         classDiag.setShowGenealogical(drawGenealogicalDependencies);
         classDiag.setShowStatic(drawStaticDependencies);
         classDiag.setShowExecutable(drawExecutableDependencies);

         ps = new PrintStream(destination + "class.dot");
         classDiag.generate(ps, matrix);
         ps.close();


         getLog().info("Writing package diagram...");

         PackageDiagram packDiag = new PackageDiagram();
         packDiag.setShowGenealogical(drawGenealogicalDependencies);
         packDiag.setShowStatic(drawStaticDependencies);
         packDiag.setShowExecutable(drawExecutableDependencies);

         ps = new PrintStream(destination + "package.dot");
         packDiag.generate(ps, analysis);
         ps.close();*/
    }
}
