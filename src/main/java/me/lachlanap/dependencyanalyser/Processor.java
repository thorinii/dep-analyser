/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.util.Repository;

/**
 *
 * @author lachlan
 */
public class Processor {

    private final Repository repo;
    private final Queue<Task> todo;
    private final Set<String> todoQuickAccess;
    private final Map<String, Output> finished;
    private String doNotInclude = "^$";

    public Processor(
            Repository repo,
            Queue<Task> todo,
            Set<String> todoQuickAccess,
            Map<String, Output> finished) {
        this.repo = repo;
        this.todo = todo;
        this.todoQuickAccess = todoQuickAccess;
        this.finished = finished;
    }

    public void setDoNotInclude(String doNotInclude) {
        this.doNotInclude = doNotInclude;
    }

    public void run() {
        while (!todo.isEmpty()) {
            process(todo.poll());
        }
    }

    private void process(Task task) {
        todoQuickAccess.remove(task.getID());
        System.out.println("Processing " + task);

        Output output = new Output(task.getJavaClass());
        processInheritance(task, output);
        processFields(task, output);
        processMethods(task, output);

        finished.put(task.getID(), output);
    }

    private void processInheritance(Task task, Output output) {
        JavaClass c = task.getJavaClass();

        try {
            JavaClass sup = c.getSuperClass();
            if (sup != null) {
                if (!sup.getClassName().matches(doNotInclude)) {
                    output.addDependency(new Dependency(Dependency.Type.Genealogical, sup));
                    addTask(new Task(sup));
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        try {
            for (JavaClass i : c.getInterfaces()) {
                if (!i.getClassName().matches(doNotInclude)) {
                    output.addDependency(new Dependency(Dependency.Type.Genealogical, i));
                    addTask(new Task(i));
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void processFields(Task task, Output output) {
        JavaClass c = task.getJavaClass();

        for (Field f : c.getFields()) {
            try {
                Type t = f.getType();
                processType(t, output, Dependency.Type.Static);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processMethods(Task task, Output output) {
        JavaClass c = task.getJavaClass();

        for (Method m : c.getMethods()) {
            try {
                processType(m.getReturnType(), output, Dependency.Type.Static);

                for (Type t : m.getArgumentTypes()) {
                    processType(t, output, Dependency.Type.Static);
                }

                if (c.isClass() && !m.isAbstract())
                    processCode(c, m, output);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processCode(JavaClass c, Method m, Output output) {
        if (m.getCode() == null)
            return;
        InstructionList instructions = new InstructionList(m.getCode().getCode());
        ConstantPoolGen gen = new ConstantPoolGen(c.getConstantPool());

        for (Instruction i : instructions.getInstructions()) {
            try {
                if (i instanceof LDC) {
                    LDC ci = (LDC) i;

                    if (ci.getType(gen) instanceof ObjectType) {
                        ObjectType ot = (ObjectType) ci.getType(gen);
                        if (ot.getClassName().equals("java.lang.Class")) {
                            ConstantClass klassValue = (ConstantClass) ci.getValue(gen);

                            String klassName = (String) klassValue.getConstantValue(gen.getConstantPool());
                            klassName = klassName.replace('/', '.');

                            JavaClass field = repo.loadClass(klassName);
                            if (!field.getClassName().matches(doNotInclude)) {
                                output.addDependency(new Dependency(Dependency.Type.Executable, field));
                                addTask(new Task(field));
                            }
                        }
                    }
                }
                if (i instanceof TypedInstruction) {
                    TypedInstruction ti = (TypedInstruction) i;

                    processType(ti.getType(gen), output, Dependency.Type.Executable);
                } else if (i instanceof NEWARRAY) {
                    NEWARRAY na = (NEWARRAY) i;
                    processType(na.getType(), output, Dependency.Type.Executable);
                }
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void addTask(Task task) {
        if (finished.containsKey(task.getID()) || todoQuickAccess.contains(task.getID()))
            return;

        todo.add(task);
        todoQuickAccess.add(task.getID());
    }

    private void processType(Type t, Output output, Dependency.Type kind) throws ClassNotFoundException {
        if (t instanceof ReferenceType) {
            if (t instanceof ObjectType) {
                ObjectType ot = (ObjectType) t;

                JavaClass field = repo.loadClass(ot.getClassName());
                if (!field.getClassName().matches(doNotInclude)) {
                    output.addDependency(new Dependency(kind, field));
                    addTask(new Task(field));
                }
            } else if (t instanceof ArrayType) {
                ArrayType at = (ArrayType) t;
                t = at.getBasicType();

                if (t instanceof ObjectType) {
                    ObjectType ot = (ObjectType) t;

                    JavaClass field = repo.loadClass(ot.getClassName());
                    if (!field.getClassName().matches(doNotInclude)) {
                        output.addDependency(new Dependency(kind, field));
                        addTask(new Task(field));
                    }
                }
            }
        }
    }
}
