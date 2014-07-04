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
import me.lachlanap.dependencyanalyser.graph.Matrix;

/**
 *
 * @author lachlan
 */
public class ClassDiagram implements Diagram {

    private static final String[] COLOURS = {
        "red", "green", "blue", "orange", "blueviolet",
        "darkgoldenrod1", "darkgreen", "cyan", "deeppink2", "grey14", "firebrick"
    };

    @Override
    public void generate(PrintStream ps, Matrix matrix) {
        ps.println("digraph {");
        ps.println("  rankdir=LR;");
        ps.println();

        doClasses(ps, matrix.getClasses());

        ps.println();
        ps.println();

        doConnections(ps, matrix);

        ps.println("}");
    }

    private void doClasses(PrintStream ps, List<String> classes) {
        Set<String> packages = new HashSet<>();

        for (String klass : classes) {
            packages.add(klass.substring(0, klass.lastIndexOf('.')));
        }

        for (String pack : packages) {
            ps.println("  subgraph \"cluster_" + pack + "\" {");
            ps.println("    label = \"" + pack + "\";\n");

            for (String klass : classes) {
                String packageName = klass.substring(0, klass.lastIndexOf('.'));
                if (packageName.equals(pack)) {

                    ps.println("    "
                            + "\"" + klass + "\""
                            + "["
                            + "label=\"" + klass.substring(klass.lastIndexOf('.') + 1) + "\""
                            // + ",shape=" + ((klass.isClass()) ? "ellipse" : "box")
                            + "];");
                }
            }

            ps.println("  }");
        }
    }

    private void doConnections(PrintStream ps, Matrix matrix) {
        for (String klass : matrix.getClasses()) {
            int i = 0;
            for (String dependency : matrix.getFor(klass)) {
                if (matrix.get(klass, dependency)) {
                    ps.println("  \"" + klass + "\" -> " + "\"" + dependency + "\""
                            + " [color=" + COLOURS[i % COLOURS.length] + ",weight=1];");
                    i++;
                }
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
