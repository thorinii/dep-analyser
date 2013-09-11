package me.lachlanap.dependencyanalyser.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.lachlanap.dependencyanalyser.analysis.DependencyType;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class Graph {

    private final Set<Node> nodes = new HashSet<>();
    //private final Set<Edge> edges = new HashSet<>();

    public Graph() {
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public class Node {

        public final JavaClass klass;
        public final Set<Edge> connections = new HashSet<>();

        public Node(JavaClass klass) {
            this.klass = klass;
        }

        public void addEdge(Node target, DependencyType type) {
            Edge e = new Edge(target, type);
            connections.add(e);
        }

        @Override
        public int hashCode() {
            return klass.getClassName().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Node other = (Node) obj;
            return klass.getClassName().equals(other.klass.getClassName());
        }
    }

    public class Edge {

        public final Node target;
        public final DependencyType type;

        Edge(Node target, DependencyType type) {
            this.target = target;
            this.type = type;
        }
    }
}
