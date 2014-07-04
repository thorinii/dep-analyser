package me.lachlanap.dependencyanalyser.graph;

import java.util.List;

/**
 *
 * @author lachlan
 */
public interface Matrix {

    public boolean get(String from, String to);

    public boolean get(int x, int y);

    public List<String> getClasses();

    public List<String> getFor(String node);

    public String map(int id);

    public int size();
}
