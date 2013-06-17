/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.diagram;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.lachlanap.dependencyanalyser.analysis.Analysis;
import me.lachlanap.dependencyanalyser.analysis.Dependency;
import me.lachlanap.dependencyanalyser.analysis.ClassResult;

/**
 *
 * @author lachlan
 */
public class PackageDiagram implements Diagram {

    private boolean showGenealogical = true;
    private boolean showStatic = true;
    private boolean showExecutable = true;

    public void setShowExecutable(boolean showExecutable) {
        this.showExecutable = showExecutable;
    }

    public void setShowGenealogical(boolean showGenealogical) {
        this.showGenealogical = showGenealogical;
    }

    public void setShowStatic(boolean showStatic) {
        this.showStatic = showStatic;
    }

    @Override
    public void generate(PrintStream ps, Analysis analysis) {
        List<ClassResult> results = analysis.getResults();

        ps.println("digraph {");
        ps.println("  rankdir=LR;");

        Set<String> packages = new HashSet<>();
        Set<String> entryPoints = new HashSet<>();
        for (ClassResult output : analysis) {
            packages.add(output.getJavaClass().getPackageName());
            if (output.isMain())
                entryPoints.add(output.getJavaClass().getPackageName());
        }

        doPackages(ps, packages, entryPoints);

        ps.println();
        ps.println();

        doConnections(ps, packages, results);

        ps.println("}");
    }

    private void doPackages(PrintStream ps, Set<String> packages, Set<String> entryPoints) {
        for (String pack : packages) {
            ps.println("  "
                    + "\"" + pack + "\""
                    + "[shape=box,"
                    + (entryPoints.contains(pack) ? "style=bold,color=red" : "")
                    + "];");
        }
    }

    private void doConnections(PrintStream ps, Set<String> packages, List<ClassResult> outputs) {
        for (String pack : packages) {
            Set<String> genDeps = new HashSet<>();
            Set<String> staticDeps = new HashSet<>();
            Set<String> execDeps = new HashSet<>();

            for (ClassResult output : outputs) {


                String thisone = output.getJavaClass().getPackageName();
                if (!thisone.equals(pack))
                    continue;

                /*if (donePackages.contains(pack))
                 continue;
                 else
                 donePackages.add(pack);*/

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
            }

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
}
