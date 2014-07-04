/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.processing;

import me.lachlanap.dependencyanalyser.analysis.*;
import me.lachlanap.dependencyanalyser.analysis.ClassResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class ExclusionProcessingFilter implements ProcessingFilter {

    private boolean excludePlatform;
    private List<String[]> exclusions;

    public ExclusionProcessingFilter() {
        this(true, new String[]{});
    }

    public ExclusionProcessingFilter(boolean excludePlatform, String[] patterns) {
        this.excludePlatform = excludePlatform;
        exclusions = new ArrayList<>();

        Pattern match = Pattern.compile("\\.");
        for (String pattern : patterns) {
            if (pattern.isEmpty())
                continue;
            exclusions.add(match.split(pattern));
        }
    }

    @Override
    public boolean shouldProcess(JavaClass jc) {
        String name = jc.getClassName();

        if (excludePlatform && (name.startsWith("java") || name.startsWith("sun")))
            return false;

        for (String[] pattern : exclusions) {
            String[] klass = name.split("\\.");

            boolean matches = true;
            for (int i = 0; i < pattern.length && i < klass.length; i++) {
                if (!pattern[i].equals(klass[i]) && !pattern[i].equals("*")) {
                    matches = false;
                }
            }

            if (matches)
                return false;
        }

        return true;
    }
}
