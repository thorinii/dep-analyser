/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import me.lachlanap.dependencyanalyser.processing.TaskSet;
import me.lachlanap.dependencyanalyser.processing.Task;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.Repository;

/**
 *
 * @author lachlan
 */
public class JarSpider {

    private final TaskSet taskSet;
    private final Repository repo;

    public JarSpider(TaskSet taskSet, Repository repo) {
        this.taskSet = taskSet;
        this.repo = repo;
    }

    public void spider(URL jar) throws IOException {
        Path p = Paths.get(new File(jar.getFile()).toURI());

        try (
                InputStream is = Files.newInputStream(p, StandardOpenOption.READ);
                JarInputStream jis = new JarInputStream(is)) {

            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                processJarEntry(entry);
            }
        }
    }

    private void processJarEntry(JarEntry entry) {
        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
            try {
                JavaClass jc = repo.loadClass(fileToClassName(entry.getName()));
                Task task = new Task(jc);

                taskSet.tryAddTask(task);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String fileToClassName(String filename) {
        return filename
                .replace('\\', '.')
                .replace('/', '.')
                .substring(0, filename.length() - 6);
    }
}
