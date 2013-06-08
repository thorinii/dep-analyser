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
public class ClassDiagram {

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

        doClasses(ps, outputs);

        ps.println();
        ps.println();

        doConnections(ps, outputs);

        ps.println("}");
    }

    private void doClasses(PrintStream ps, List<Output> outputs) {
        Set<String> packages = new HashSet<>();

        for (Output output : outputs) {
            packages.add(output.getJavaClass().getPackageName());
        }

        for (String pack : packages) {
            ps.println("  subgraph \"cluster_" + pack + "\" {");
            ps.println("    label = \"" + pack + "\";\n");

            for (Output output : outputs) {
                JavaClass klass = output.getJavaClass();
                if (pack.equals(klass.getPackageName())) {
                    String name = klass.getClassName();

                    ps.println("    "
                            + "\"" + name + "\""
                            + "["
                            + "label=\"" + name.substring(name.lastIndexOf('.') + 1) + "\","
                            + "shape=" + ((klass.isClass()) ? "ellipse" : "box") + ","
                            + ((output.isMain())? "style=bold,color=red" : "")
                            + "];");
                }
            }

            ps.println("  }");
        }
    }

    private void doConnections(PrintStream ps, List<Output> outputs) {
        for (Output output : outputs) {
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
                return "style=bold,color=grey";
            case Static:
                return "color=green";
            case Executable:
            default:
                return "color=red";
        }
    }
}
