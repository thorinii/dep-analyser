package me.lachlanap.dependencyanalyser;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import me.lachlanap.dependencyanalyser.dot.ClassDiagram;
import me.lachlanap.dependencyanalyser.dot.PackageDiagram;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.ClassLoaderRepository;

public class App {

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, FileNotFoundException {
        URLClassLoader loader = new URLClassLoader(new URL[]{
            Paths.get("./lct.jar").toUri().toURL()
        });
        Repository repo = new ClassLoaderRepository(loader);


        Queue<Task> todo = new LinkedList<>();
        Set<String> todoQuickAccess = new HashSet<>();
        Map<String, Output> finished = new HashMap<>();

        JavaClass base = repo.loadClass("me.lachlanap.lct.MainTesting");

        Task task = new Task(base);
        todo.add(task);
        todoQuickAccess.add(task.getID());

        Processor processor = new Processor(repo, todo, todoQuickAccess, finished);
        processor.setDoNotInclude("(java).*");
        processor.run();

        List<Output> output = new ArrayList<>(finished.values());
        output = new Filter().filter(output);
        
        for(Output out : output){
            System.out.println(out);
        }
        
        PrintStream ps = new PrintStream("class.dot");
        ClassDiagram classDiag = new ClassDiagram();
        classDiag.convert(ps, output);
        ps.close();
        
        ps = new PrintStream("package.dot");
        PackageDiagram packDiag = new PackageDiagram();
        packDiag.convert(ps, output);
        ps.close();
    }
}
