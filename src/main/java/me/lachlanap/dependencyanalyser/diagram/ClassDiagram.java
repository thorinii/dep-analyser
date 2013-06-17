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
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class ClassDiagram implements Diagram {

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
        ps.println();

        doClasses(ps, results);

        ps.println();
        ps.println();

        doConnections(ps, results);

        ps.println("}");
    }

    private void doClasses(PrintStream ps, List<ClassResult> outputs) {
        Set<String> packages = new HashSet<>();

        for (ClassResult output : outputs) {
            packages.add(output.getJavaClass().getPackageName());
        }

        for (String pack : packages) {
            ps.println("  subgraph \"cluster_" + pack + "\" {");
            ps.println("    label = \"" + pack + "\";\n");

            for (ClassResult output : outputs) {
                JavaClass klass = output.getJavaClass();
                if (pack.equals(klass.getPackageName())) {
                    String name = klass.getClassName();

                    ps.println("    "
                            + "\"" + name + "\""
                            + "["
                            + "label=\"" + name.substring(name.lastIndexOf('.') + 1) + "\","
                            + "shape=" + ((klass.isClass()) ? "ellipse" : "box") + ","
                            + ((output.isMain()) ? "style=bold,color=red" : "")
                            + "];");
                }
            }

            ps.println("  }");
        }
    }

    private void doConnections(PrintStream ps, List<ClassResult> outputs) {
        for (ClassResult output : outputs) {
            String klass = output.getJavaClass().getClassName();
            for (Dependency dep : output.getDependencies()) {

                if (dep.getType() == Dependency.Type.Genealogical && !showGenealogical)
                    continue;
                if (dep.getType() == Dependency.Type.Static && !showStatic)
                    continue;
                if (dep.getType() == Dependency.Type.Executable && !showExecutable)
                    continue;

                String depKlass = dep.getJavaClass().getClassName();

                ps.println("  \"" + klass + "\" -> " + "\"" + depKlass + "\""
                        + " [" + getStyle(dep)
                        + "];");
            }

            ps.println();
        }
    }

    private String getStyle(Dependency dep) {
        switch (dep.getType()) {
            case Genealogical:
                if (dep.getJavaClass().isInterface())
                    return "style=bold,color=grey30,weight=4";
                else
                    return "style=bold,color=black,weight=4";
            case Static:
                return "color=green,weight=1";
            case Executable:
            default:
                return "color=red,weight=0,style=dashed";
        }
    }
}
