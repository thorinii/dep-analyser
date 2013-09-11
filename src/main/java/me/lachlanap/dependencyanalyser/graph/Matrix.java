package me.lachlanap.dependencyanalyser.graph;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import me.lachlanap.dependencyanalyser.analysis.DependencyType;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class Matrix {

    private final Class2StringMapper classMapper;
    private final TObjectIntMap<String> class2IndexMap;
    private final TIntObjectMap<String> index2ClassMap;
    private final List<TIntList> matrix;
    private final List<String> nodes;
    private volatile int nextClassID = 0;

    public Matrix() {
        classMapper = new SyntheticClass2StringMapper();
        class2IndexMap = new TObjectIntHashMap<>(128, 0.75f, -1);
        index2ClassMap = new TIntObjectHashMap<>();
        matrix = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    public Matrix(Matrix copy) {
        classMapper = new SyntheticClass2StringMapper();
        class2IndexMap = new TObjectIntHashMap<>(copy.class2IndexMap);
        index2ClassMap = new TIntObjectHashMap<>(copy.index2ClassMap);
        matrix = new ArrayList<>();
        nodes = new ArrayList<>(copy.nodes);

        for (TIntList list : copy.matrix) {
            matrix.add(new TIntArrayList(list));
        }
    }

    public List<String> getFor(String node) {
        List<String> connected = new ArrayList<>();

        int id = class2IndexMap.get(node);
        TIntList list = matrix.get(id);

        for (int i = 0; i < size(); i++)
            if (list.get(i) > 0)
                connected.add(nodes.get(i));

        return connected;
    }

    public DependencyType get(String from, String to) {
        int fromID = class2IndexMap.get(from);
        int toID = class2IndexMap.get(to);

        int value = matrix.get(fromID).get(toID);

        if (value < 0)
            return null;
        else
            return DependencyType.values()[value];
    }

    public void set(int x, int y, int value) {
        if (value >= 0 && matrix.get(x).get(y) > value)
            return;
        matrix.get(x).set(y, value);
    }

    public void unset(int x, int y) {
        matrix.get(x).set(y, -1);
    }

    public int get(int x, int y) {
        return matrix.get(x).get(y);
    }

    public List<String> getClasses() {
        return nodes;
    }

    public int size() {
        return matrix.size();
    }

    @Deprecated
    public void put(JavaClass from, JavaClass to, DependencyType dependencyType) {
        put(classMapper.map(from),
            classMapper.map(to),
            dependencyType);
    }

    public void put(String from, String to, DependencyType dependencyType) {
        if (!doing(from) || !doing(to))
            return;

        ensureNode(from);
        ensureNode(to);

        int fromI = map(from);
        int toI = map(to);

        if (fromI == toI)
            return;
        matrix.get(fromI).set(toI,
                              Math.max(matrix.get(fromI).get(toI), dependencyType.ordinal()));
    }

    private void ensureNode(String node) {
        if (map(node) >= 0)
            return;

        int index = nextID();
        class2IndexMap.put(node, index);
        index2ClassMap.put(index, node);
        nodes.add(node);

        while (size() <= index)
            matrix.add(null);

        matrix.set(index, new TIntArrayList());

        for (TIntList subList : matrix) {
            while (subList.size() < size()) {
                subList.add(-1);
            }
        }
    }

    private boolean doing(String node) {
        if (node.startsWith("java"))
            return false;
        return true;
    }

    private int map(String node) {
        return class2IndexMap.get(node);
    }

    public String map(int id) {
        return index2ClassMap.get(id);
    }

    private synchronized int nextID() {
        return nextClassID++;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Matrix: ").append(size()).append(" classes\n");

        for (int i = 0; i < size(); i++) {
            builder.append(index2ClassMap.get(i)).append(" -> \n");

            TIntList adjacency = matrix.get(i);
            for (int j = 0; j < size(); j++) {
                if (adjacency.get(j) > 0)
                    builder.append("  ").append(index2ClassMap.get(j)).append("\n");
            }
        }

        return builder.toString();
    }
}
