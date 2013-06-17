/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.analysis;

import me.lachlanap.dependencyanalyser.analysis.ClassResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class AnalysisFilter {

    private boolean excludePlatform;
    private List<String[]> exclusions;

    public AnalysisFilter() {
        this(true, new String[]{});
    }

    public AnalysisFilter(boolean excludePlatform, String[] patterns) {
        this.excludePlatform = excludePlatform;
        exclusions = new ArrayList<>();

        Pattern match = Pattern.compile("\\.");
        for (String pattern : patterns) {
            if (pattern.isEmpty())
                continue;
            exclusions.add(match.split(pattern));
        }
    }

    public Analysis filter(Analysis analysis) {
        Analysis filtered = new Analysis();

        // Go through the nonsynthetic classes
        for (ClassResult result : analysis) {
            if (leaveOut(result.getJavaClass()))
                continue;
            if (!result.getJavaClass().isSynthetic()) {
                ClassResult newO = new ClassResult(result.getJavaClass());
                for (Dependency dep : result.getDependencies()) {
                    if (leaveOut(dep.getJavaClass()))
                        continue;
                    if (dep.getJavaClass().isSynthetic()) {
                        JavaClass parent = getParent(analysis, dep.getJavaClass().getClassName());
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

        // Go through the synthetic classes
        for (ClassResult result : analysis) {
            if (leaveOut(result.getJavaClass()))
                continue;
            if (result.getJavaClass().isSynthetic()) {
                ClassResult parent = getParentResult(analysis, result);
                if (parent == null)
                    continue;

                for (Dependency dep : result.getDependencies()) {
                    if (leaveOut(dep.getJavaClass()))
                        continue;
                    if (dep.getJavaClass().isSynthetic()) {
                        JavaClass parentClass = getParent(analysis, dep.getJavaClass().getClassName());
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

    private boolean leaveOut(JavaClass jc) {
        String name = jc.getClassName();

        if (excludePlatform && (name.startsWith("java") || name.startsWith("sun")))
            return true;

        for (String[] pattern : exclusions) {
            String[] klass = name.split("\\.");

            boolean matches = true;
            for (int i = 0; i < pattern.length && i < klass.length; i++) {
                if (!pattern[i].equals(klass[i]) && !pattern[i].equals("*")) {
                    matches = false;
                }
            }

            if (matches)
                return true;
        }

        return false;
    }

    private String getParent(String sub) {
        return sub.substring(0, sub.indexOf('$'));
    }

    private JavaClass getParent(Analysis analysis, String o) {
        String name = getParent(o);
        for (ClassResult parentOutput : analysis) {
            if (parentOutput.getJavaClass().getClassName().equals(name)) {
                return parentOutput.getJavaClass();
            }
        }

        return null;
    }

    private ClassResult getParentResult(Analysis analysis, ClassResult o) {
        for (ClassResult parentOutput : analysis) {
            if (parentOutput.getJavaClass().getClassName().equals(getParent(o.getJavaClass().getClassName()))) {
                return parentOutput;
            }
        }

        return null;
    }
}
