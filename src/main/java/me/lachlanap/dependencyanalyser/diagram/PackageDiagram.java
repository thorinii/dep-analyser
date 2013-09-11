/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.diagram;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.lachlanap.dependencyanalyser.analysis.DependencyType;
import static me.lachlanap.dependencyanalyser.analysis.DependencyType.Executable;
import static me.lachlanap.dependencyanalyser.analysis.DependencyType.Genealogical;
import static me.lachlanap.dependencyanalyser.analysis.DependencyType.Static;
import me.lachlanap.dependencyanalyser.graph.Matrix;

/**
 *
 * @author lachlan
 */
public class PackageDiagram implements Diagram {

    private boolean showGenealogical = true;
    private boolean showStatic = true;
    private boolean showExecutable = true;

    @Override
    public void setShowExecutable(boolean showExecutable) {
        this.showExecutable = showExecutable;
    }

    @Override
    public void setShowGenealogical(boolean showGenealogical) {
        this.showGenealogical = showGenealogical;
    }

    @Override
    public void setShowStatic(boolean showStatic) {
        this.showStatic = showStatic;
    }

    @Override
    public void generate(PrintStream ps, Matrix matrix) {
        ps.println("digraph {");
        //ps.println("  rankdir=LR;");

        doPackages(ps, matrix.getClasses());

        ps.println();
        ps.println();

        doConnections(ps, matrix);

        ps.println("}");
    }

    private void doPackages(PrintStream ps, List<String> packages) {
        for (String pack : packages) {
            ps.println("  "
                    + "\"" + pack + "\""
                    + "[shape=box];");
        }
    }

    private void doConnections(PrintStream ps, Matrix matrix) {
        for (String klass : matrix.getClasses()) {
            for (String dependency : matrix.getFor(klass)) {
                DependencyType type = matrix.get(klass, dependency);

                if (type == DependencyType.Genealogical && !showGenealogical)
                    continue;
                else if (type == DependencyType.Static && !showStatic)
                    continue;
                else if (type == DependencyType.Executable && !showExecutable)
                    continue;

                ps.println("  \"" + klass + "\" -> " + "\"" + dependency + "\""
                        + " [" + getStyle(klass, type) + "];");
            }

            ps.println();
        }
    }

    @Deprecated
    private void doConnections(PrintStream ps, Set<String> packages, Matrix matrix) {
        for (String pack : packages) {
            Set<String> genDeps = new HashSet<>();
            Set<String> staticDeps = new HashSet<>();
            Set<String> execDeps = new HashSet<>();

            for (String klass : matrix.getClasses()) {
                String packageName = klass.substring(0, klass.lastIndexOf('.'));
                if (!packageName.equals(pack))
                    continue;

                for (String dependency : matrix.getFor(klass)) {
                    DependencyType type = matrix.get(klass, dependency);
                    String dependencyPackage = dependency.substring(0, dependency.lastIndexOf('.'));

                    if (packageName.equals(dependencyPackage))
                        continue;

                    switch (type) {
                        case Genealogical:
                            if (showGenealogical)
                                genDeps.add(dependencyPackage);
                            break;
                        case Static:
                            if (showStatic)
                                staticDeps.add(dependencyPackage);
                            break;
                        case Executable:
                            if (showExecutable)
                                execDeps.add(dependencyPackage);
                            break;
                    }
                }
            }

            /*// for (ClassResult output : outputs) {
             String thisone = output.getJavaClass().getPackageName();
             if (!thisone.equals(pack))
             continue;

             for (Dependency dep : output.getDependencies()) {
             if (dep.getJavaClass().getPackageName().equals(pack))
             continue;

             switch (dep.getType()) {
             case Genealogical:
             if (showGenealogical)
             genDeps.add(dep.getJavaClass().getPackageName());
             break;
             case Static:
             if (showStatic)
             staticDeps.add(dep.getJavaClass().getPackageName());
             break;
             case Executable:
             if (showExecutable)
             execDeps.add(dep.getJavaClass().getPackageName());
             break;
             }
             }
             }*/

            for (String dep : genDeps) {
                ps.println("  \"" + pack + "\" -> " + "\"" + dep + "\""
                        + " [style=bold,color=grey30,weight=4];");
            }
            for (String dep : staticDeps) {
                ps.println("  \"" + pack + "\" -> " + "\"" + dep + "\""
                        + " [color=green,weight=1];");
            }
            for (String dep : execDeps) {
                ps.println("  \"" + pack + "\" -> " + "\"" + dep + "\""
                        + " [color=red,weight=0,style=dashed];");
            }

            ps.println();
        }
    }

    private String getStyle(String klass, DependencyType type) {
        switch (type) {
            case Genealogical:
                //if (klass.isInterface())
                //    return "style=bold,color=grey30,weight=4";
                //else
                return "style=bold,color=grey30,weight=4";
            case Static:
                return "color=green,weight=1";
            case Executable:
            default:
                return "color=red,weight=0,style=dashed";
        }
    }
}
