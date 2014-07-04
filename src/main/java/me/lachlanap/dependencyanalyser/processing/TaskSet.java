/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.processing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class TaskSet {

    private final Queue<Task> todo;
    private final Set<String> todoQuickAccess;
    private final Set<String> processing;
    private final Set<String> finished;

    public TaskSet() {
        todo = new LinkedList<>();
        todoQuickAccess = new HashSet<>();
        processing = new HashSet<>();
        finished = new HashSet<>();
    }

    public boolean tryAddTask(Task task) {
        String className = task.getJavaClass().getClassName();
        if (finished.contains(className) || processing.contains(className) || todoQuickAccess.contains(className))
            return false;

        todo.add(task);
        todoQuickAccess.add(className);

        return true;
    }

    public boolean tryAddTask(JavaClass klass) {
        String className = klass.getClassName();
        if (finished.contains(className) || processing.contains(className) || todoQuickAccess.contains(className))
            return false;

        todo.add(new Task(klass));
        todoQuickAccess.add(className);

        return true;
    }

    public boolean moreTasks() {
        return !todo.isEmpty();
    }

    public Task getNextTask() {
        Task next = todo.remove();
        String className = next.getJavaClass().getClassName();

        todoQuickAccess.remove(className);
        processing.add(className);

        return next;
    }

    public void setFinished(Task task) {
        String className = task.getJavaClass().getClassName();

        processing.remove(className);
        finished.add(className);
    }
}
