/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class Filter {

    public List<Output> filter(List<Output> outputs) {
        List<Output> filtered = new ArrayList<>();

        for (Output o : outputs) {
            if (!o.getJavaClass().getClassName().contains("$")) {
                Output newO = new Output(o.getJavaClass());
                for (Dependency dep : o.getDependencies()) {
                    if (dep.getJavaClass().getClassName().contains("$")) {
                        JavaClass parent = getParent(outputs, dep.getJavaClass().getClassName());
                        if (parent != null)
                            dep = new Dependency(dep.getType(), parent);
                        if (dep.getType() != Dependency.Type.Genealogical)
                            newO.addDependency(dep);
                    } else
                        newO.addDependency(dep);
                }

                filtered.add(newO);
            }
        }

        for (Output o : outputs) {
            String name = o.getJavaClass().getClassName();

            if (name.contains("$")) {
                Output parent = getParentOutput(outputs, o);
                if (parent == null)
                    continue;

                for (Dependency dep : o.getDependencies()) {
                    if (dep.getJavaClass().getClassName().contains("$")) {
                        JavaClass parentClass = getParent(outputs, dep.getJavaClass().getClassName());
                        if (parentClass != null)
                            dep = new Dependency(dep.getType(), parentClass);
                        if (dep.getType() != Dependency.Type.Genealogical)
                            parent.addDependency(dep);
                    } else
                        parent.addDependency(dep);
                }
            }
        }

        return filtered;
    }

    private String getParent(String sub) {
        return sub.substring(0, sub.indexOf('$'));
    }

    private JavaClass getParent(List<Output> os, String o) {
        String name = getParent(o);
        for (Output parentOutput : os) {
            if (parentOutput.getJavaClass().getClassName().equals(name)) {
                return parentOutput.getJavaClass();
            }
        }

        return null;
    }

    private Output getParentOutput(List<Output> os, Output o) {
        for (Output parentOutput : os) {
            if (parentOutput.getJavaClass().getClassName().equals(getParent(o.getJavaClass().getClassName()))) {
                return parentOutput;
            }
        }

        return null;
    }
}
