/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.dot;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.lachlanap.dependencyanalyser.Dependency;
import me.lachlanap.dependencyanalyser.Output;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class PackageDiagram {

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

    public void convert(List<Output> outputs) {
        convert(System.out, outputs);
    }

    public void convert(PrintStream ps, List<Output> outputs) {
        ps.println("digraph {");

        Set<String> packages = new HashSet<>();
        Set<String> entryPoints = new HashSet<>();
        for (Output output : outputs) {
            packages.add(output.getJavaClass().getPackageName());
            if (output.isMain())
                entryPoints.add(output.getJavaClass().getPackageName());
        }

        doPackages(ps, packages, entryPoints);

        ps.println();
        ps.println();

        doConnections(ps, packages, outputs);

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

    private void doConnections(PrintStream ps, Set<String> packages, List<Output> outputs) {
        Set<String> donePackages = new HashSet<>();

        for (String pack : packages) {
            Set<String> genDeps = new HashSet<>();
            Set<String> staticDeps = new HashSet<>();
            Set<String> execDeps = new HashSet<>();

            for (Output output : outputs) {


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
                            genDeps.add(dep.getJavaClass().getPackageName());
                            break;
                        case Static:
                            staticDeps.add(dep.getJavaClass().getPackageName());
                            break;
                        case Executable:
                            execDeps.add(dep.getJavaClass().getPackageName());
                            break;
                    }
                }
            }

            for (String dep : genDeps) {
                ps.println("  \"" + pack + "\" -> " + "\"" + dep + "\""
                        + " [style=bold,color=grey];");
            }
            for (String dep : staticDeps) {
                ps.println("  \"" + pack + "\" -> " + "\"" + dep + "\""
                        + " [color=green];");
            }
            for (String dep : execDeps) {
                ps.println("  \"" + pack + "\" -> " + "\"" + dep + "\""
                        + " [color=red];");
            }

            ps.println();
        }

    }

    private String getStyle(Dependency dep) {
        switch (dep.getType()) {
            case Genealogical:
                return "style=bold,color=grey";
            case Static:
                return "color=green";
            case Executable:
            default:
                return "color=red";
        }
    }
}
