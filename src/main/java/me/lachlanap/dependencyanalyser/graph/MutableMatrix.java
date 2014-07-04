package me.lachlanap.dependencyanalyser.graph;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class MutableMatrix implements Matrix {

    private final Class2StringMapper classMapper;
    private final TObjectIntMap<String> class2IndexMap;
    private final TIntObjectMap<String> index2ClassMap;
    private final List<BitSet> matrix;
    private final List<String> nodes;
    private volatile int nextClassID = 0;

    public MutableMatrix() {
        classMapper = new SyntheticClass2StringMapper();
        class2IndexMap = new TObjectIntHashMap<>(128, 0.75f, -1);
        index2ClassMap = new TIntObjectHashMap<>();
        matrix = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    public MutableMatrix(Matrix copy) {
        this();

        for (String node : copy.getClasses()) {
            ensureNode(node);
            for (String connected : copy.getFor(node))
                this.put(node, connected);
        }

        if (size() != copy.size())
            throw new IllegalStateException("Somehow managed to not have all the nodes");
    }

    public MutableMatrix(MutableMatrix copy) {
        classMapper = new SyntheticClass2StringMapper();
        class2IndexMap = new TObjectIntHashMap<>(copy.class2IndexMap);
        index2ClassMap = new TIntObjectHashMap<>(copy.index2ClassMap);
        matrix = new ArrayList<>();
        nodes = new ArrayList<>(copy.nodes);

        for (BitSet list : copy.matrix) {
            BitSet newbs = new BitSet(list.length());
            for (int i = 0; i < list.length(); i++)
                newbs.set(i, list.get(i));
            matrix.add(newbs);
        }
    }

    @Override
    public List<String> getFor(String node) {
        List<String> connected = new ArrayList<>();

        int id = class2IndexMap.get(node);
        BitSet list = matrix.get(id);

        for (int i = 0; i < size(); i++)
            if (list.get(i))
                connected.add(nodes.get(i));

        return connected;
    }

    public void set(int x, int y, boolean value) {
        if (x == y)
            return;
        matrix.get(x).set(y, value);
    }

    public void set(int x, int y) {
        if (x == y)
            return;
        matrix.get(x).set(y, true);
    }

    public void unset(int x, int y) {
        if (x == y)
            return;
        matrix.get(x).set(y, false);
    }

    @Override
    public boolean get(String from, String to) {
        int fromID = class2IndexMap.get(from);
        int toID = class2IndexMap.get(to);

        return get(fromID, toID);
    }

    @Override
    public boolean get(int x, int y) {
        return matrix.get(x).get(y);
    }

    @Override
    public List<String> getClasses() {
        return nodes;
    }

    @Override
    public int size() {
        return nodes.size();
    }

    public int edges() {
        int e = 0;
        for (BitSet bs : matrix) {
            e += bs.cardinality();
        }
        return e;
    }

    @Deprecated
    public void put(JavaClass from, JavaClass to) {
        put(classMapper.map(from),
            classMapper.map(to));
    }

    public void put(String from, String to) {
        ensureNode(from);
        ensureNode(to);

        int fromI = map(from);
        int toI = map(to);

        if (fromI == toI)
            return;
        set(fromI, toI);
    }

    private void ensureNode(String node) {
        if (map(node) >= 0)
            return;

        int index = nextID();
        class2IndexMap.put(node, index);
        index2ClassMap.put(index, node);
        nodes.add(node);

        //while (matrix.size() <= index)
        //    matrix.add(null);

        matrix.add(new BitSet(size()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MutableMatrix other = (MutableMatrix) obj;

        if (size() != nodes.size())
            return false;
        if (!nodes.containsAll(other.nodes) || !other.nodes.containsAll(nodes))
            return false;

        for (String node : nodes) {
            BitSet ours = matrix.get(map(node));
            BitSet theirs = other.matrix.get(other.map(node));

            for (String to : nodes) {
                if (ours.get(map(to)) != theirs.get(other.map(to)))
                    return false;
            }
        }

        return true;
    }

    private int map(String node) {
        return class2IndexMap.get(node);
    }

    @Override
    public String map(int id) {
        return index2ClassMap.get(id);
    }

    private synchronized int nextID() {
        return nextClassID++;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Matrix: ")
                .append(size()).append(" nodes ")
                .append(edges()).append(" edges\n");

        for (int i = 0; i < size(); i++) {
            builder.append(index2ClassMap.get(i)).append(" -> \n");

            BitSet adjacency = matrix.get(i);
            for (int j = 0; j < size(); j++) {
                if (adjacency.get(j))
                    builder.append("  ").append(index2ClassMap.get(j)).append("\n");
            }
        }

        return builder.toString();
    }
}
