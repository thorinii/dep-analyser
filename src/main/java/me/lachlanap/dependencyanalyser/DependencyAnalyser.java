package me.lachlanap.dependencyanalyser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import me.lachlanap.dependencyanalyser.analysis.Analysis;
import me.lachlanap.dependencyanalyser.analysis.AnalysisFilter;
import me.lachlanap.dependencyanalyser.diagram.ClassDiagram;
import me.lachlanap.dependencyanalyser.diagram.PackageDiagram;
import me.lachlanap.dependencyanalyser.processing.Processor;
import me.lachlanap.dependencyanalyser.processing.TaskSet;
import org.apache.bcel.util.Repository;

public class DependencyAnalyser {

    private final URL jar;
    private boolean drawGenealogical = true;
    private boolean drawStatic = true;
    private boolean drawExecutable = true;
    private AnalysisFilter filter;

    public DependencyAnalyser(URL jar) {
        this.jar = jar;
        filter = new AnalysisFilter(false, new String[]{"com"});
    }

    public void setDrawGenealogical(boolean drawGenealogical) {
        this.drawGenealogical = drawGenealogical;
    }

    public void setDrawStatic(boolean drawStatic) {
        this.drawStatic = drawStatic;
    }

    public void setDrawExecutable(boolean drawExecutable) {
        this.drawExecutable = drawExecutable;
    }

    public void setFilter(AnalysisFilter filter) {
        this.filter = filter;
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

        Analysis analysis = new Analysis();
        Processor processor = new Processor(repo, taskSet, analysis);

        System.out.println("Processing...");
        processor.run();

        System.out.println("Filtering irrelevant data...");
        Analysis output = filter.filter(analysis);

        PrintStream ps;

        File dest = new File(destination);
        if (!dest.exists())
            dest.mkdirs();

        System.out.println("Writing class diagram...");

        ClassDiagram classDiag = new ClassDiagram();
        classDiag.setShowGenealogical(drawGenealogical);
        classDiag.setShowStatic(drawStatic);
        classDiag.setShowExecutable(drawExecutable);

        ps = new PrintStream(destination + "class.dot");
        classDiag.generate(ps, output);
        ps.close();


        System.out.println("Writing package diagram...");

        PackageDiagram packDiag = new PackageDiagram();
        packDiag.setShowGenealogical(drawGenealogical);
        packDiag.setShowStatic(drawStatic);
        packDiag.setShowExecutable(drawExecutable);

        ps = new PrintStream(destination + "package.dot");
        packDiag.generate(ps, output);
        ps.close();
    }

    public static void main(String[] args) throws
            MalformedURLException, URISyntaxException,
            ClassNotFoundException, FileNotFoundException, IOException {
        final URL jar = Paths.get("./rt.jar").toUri().toURL();
        new DependencyAnalyser(jar).analyseJar("./");
    }
}
