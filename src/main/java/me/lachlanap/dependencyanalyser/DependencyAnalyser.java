package me.lachlanap.dependencyanalyser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import me.lachlanap.dependencyanalyser.diagram.ClassDiagram;
import me.lachlanap.dependencyanalyser.diagram.Diagram;
import me.lachlanap.dependencyanalyser.diagram.PackageDiagram;
import me.lachlanap.dependencyanalyser.graph.Matrix;
import me.lachlanap.dependencyanalyser.graph.MatrixOperations;
import me.lachlanap.dependencyanalyser.processing.Processor;
import me.lachlanap.dependencyanalyser.processing.Processor.Result;
import me.lachlanap.dependencyanalyser.processing.TaskSet;
import org.apache.bcel.util.Repository;

public class DependencyAnalyser {

    private final URL jar;

    public static void main(String[] args) throws
            MalformedURLException, URISyntaxException,
            ClassNotFoundException, FileNotFoundException, IOException {
        final URL jar = Paths.get(args.length > 0 ? args[0] : "./lct.jar").toUri().toURL();
        new DependencyAnalyser(jar).analyseJar("./diag/");
    }

    public DependencyAnalyser(URL jar) {
        this.jar = jar;
    }

    public void analyseJar(String destination) throws
            URISyntaxException,
            FileNotFoundException,
            IOException {
        System.out.println("Opening jar repository...");
        Repository repo = RepositoryFactory.getRepo(jar);

        TaskSet taskSet = new TaskSet();

        System.out.println("Loading class list...");
        JarSpider spider = new JarSpider(taskSet, repo);
        spider.spider(jar);

        Processor processor = new Processor(repo, taskSet);

        System.out.println("Processing...");
        Result result = processor.run();

        generateDiagrams(destination, result);
    }

    private void generateDiagrams(String destination, Result result) throws FileNotFoundException {
        generateDiagramSet(destination + "gen/", result.inheritance);
        generateDiagramSet(destination + "static/", result.staticDependencies);
        generateDiagramSet(destination + "exec/", result.executableDependencies);
    }

    private void generateDiagramSet(String destination, Matrix classMatrix) throws FileNotFoundException {
        Matrix packageMatrix = MatrixOperations.class2PackageMatrix(classMatrix);

        Matrix transitiveClassMatrix = MatrixOperations.transitiveReduction(classMatrix);
        Matrix transitivePackageMatrix = MatrixOperations.transitiveReduction(packageMatrix);

        File dest = new File(destination);
        if (!dest.exists())
            dest.mkdirs();

        System.out.println(classMatrix);
        System.out.println(transitiveClassMatrix);

        System.out.println("Writing transitive package diagram to " + destination + "...");
        doDiagram(new PackageDiagram(), destination + "package-transitive.dot", transitivePackageMatrix);

        System.out.println("Writing transitive class diagram to " + destination + "...");
        doDiagram(new ClassDiagram(), destination + "class-transitive.dot", transitiveClassMatrix);


        System.out.println("Writing package diagram to " + destination + "...");
        doDiagram(new PackageDiagram(), destination + "package.dot", packageMatrix);

        System.out.println("Writing class diagram to " + destination + "...");
        doDiagram(new ClassDiagram(), destination + "class.dot", classMatrix);
    }

    private void doDiagram(Diagram diagram, String file, Matrix matrix) throws FileNotFoundException {
        PrintStream ps = new PrintStream(file);
        diagram.generate(ps, matrix);
        ps.close();
    }
}
