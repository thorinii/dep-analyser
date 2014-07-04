/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

/**
 *
 * @author lachlan
 */
public class ClassResult {

    private final JavaClass javaClass;
    private final Map<String, Dependency> dependencies;

    public ClassResult(JavaClass javaClass) {
        this.javaClass = javaClass;
        dependencies = new HashMap<>();
    }

    public JavaClass getJavaClass() {
        return javaClass;
    }

    public boolean isMain() {
        for (Method m : javaClass.getMethods()) {
            if (m.isStatic() && m.getName().equals("main"))
                return true;
        }
        return false;
    }

    public void addDependency(Dependency dep) {
        if (dep.getJavaClass().equals(javaClass))
            return;

        if (dependencies.containsKey(dep.getKey())) {
            Dependency other = dependencies.get(dep.getKey());

            if (dep.isStrongerThan(other)) {
                dependencies.put(dep.getKey(), dep);
            }
        } else {
            dependencies.put(dep.getKey(), dep);
        }
    }

    public List<Dependency> getDependencies() {
        return new ArrayList<>(dependencies.values());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Output ").append(javaClass.getClassName());

        for (Dependency dep : dependencies.values()) {
            builder.append('\n');

            switch (dep.getType()) {
                case Genealogical:
                    builder.append(" => ");
                    break;
                case Static:
                    builder.append(" +> ");
                    break;
                case Executable:
                    builder.append(" -> ");
                    break;
            }

            builder.append(dep.getJavaClass().getClassName());
        }

        builder.append(']');

        return builder.toString();
    }
}
