package me.lachlanap.dependencyanalyser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import me.lachlanap.dependencyanalyser.analysis.AnalysisFilter;
import me.lachlanap.dependencyanalyser.diagram.ClassDiagram;
import me.lachlanap.dependencyanalyser.diagram.Diagram;
import me.lachlanap.dependencyanalyser.diagram.PackageDiagram;
import me.lachlanap.dependencyanalyser.graph.Matrix;
import me.lachlanap.dependencyanalyser.graph.MatrixOperations;
import me.lachlanap.dependencyanalyser.processing.Processor;
import me.lachlanap.dependencyanalyser.processing.TaskSet;
import org.apache.bcel.util.Repository;

public class DependencyAnalyser {

    private final URL jar;
    private boolean drawGenealogical = true;
    private boolean drawStatic = true;
    private boolean drawExecutable = true;

    public static void main(String[] args) throws
            MalformedURLException, URISyntaxException,
            ClassNotFoundException, FileNotFoundException, IOException {
        final URL jar = Paths.get("./lct.jar").toUri().toURL();
        new DependencyAnalyser(jar).analyseJar("./");
    }

    public DependencyAnalyser(URL jar) {
        this.jar = jar;
    }

    @Deprecated
    public void setDrawGenealogical(boolean drawGenealogical) {
        this.drawGenealogical = drawGenealogical;
    }

    @Deprecated
    public void setDrawStatic(boolean drawStatic) {
        this.drawStatic = drawStatic;
    }

    @Deprecated
    public void setDrawExecutable(boolean drawExecutable) {
        this.drawExecutable = drawExecutable;
    }

    @Deprecated
    public void setFilter(AnalysisFilter filter) {
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

        Matrix matrix = new Matrix();
        Processor processor = new Processor(repo, taskSet, matrix);

        System.out.println("Processing...");
        processor.run();

        generateDiagrams(destination, matrix);
    }

    private void generateDiagrams(String destination, Matrix classMatrix) throws FileNotFoundException {
        Matrix packageMatrix = MatrixOperations.class2PackageMatrix(classMatrix);

        Matrix transitiveClassMatrix = MatrixOperations.transitiveReduction(classMatrix);
        Matrix transitivePackageMatrix = MatrixOperations.pathMatrix(packageMatrix);

        File dest = new File(destination);
        if (!dest.exists())
            dest.mkdirs();


        System.out.println("Writing transitive package diagram...");
        doDiagram(new PackageDiagram(), destination + "package-transitive.dot", transitivePackageMatrix);

        System.out.println("Writing transitive class diagram...");
        doDiagram(new ClassDiagram(), destination + "class-transitive.dot", transitiveClassMatrix);


        System.out.println("Writing package diagram...");
        doDiagram(new PackageDiagram(), destination + "package.dot", packageMatrix);

        System.out.println("Writing class diagram...");
        doDiagram(new ClassDiagram(), destination + "class.dot", classMatrix);
    }

    private void doDiagram(Diagram diagram, String file, Matrix matrix) throws FileNotFoundException {
        diagram.setShowGenealogical(drawGenealogical);
        diagram.setShowStatic(drawStatic);
        diagram.setShowExecutable(drawExecutable);

        PrintStream ps = new PrintStream(file);
        diagram.generate(ps, matrix);
        ps.close();
    }
}
