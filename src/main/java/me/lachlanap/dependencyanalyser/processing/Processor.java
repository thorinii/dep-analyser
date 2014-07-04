/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.lachlanap.dependencyanalyser.graph.MutableMatrix;
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
    private final TaskSet taskSet;
    private final Map<String, JavaClass> cache = new HashMap<>();
    private final Set<String> bad = new HashSet<>();
    private ProcessingFilter processingFilter;

    public Processor(Repository repo, TaskSet taskSet) {
        this.repo = repo;
        this.taskSet = taskSet;
        this.processingFilter = new NullProcessingFilter();
    }

    public void setProcessingFilter(ProcessingFilter processingFilter) {
        this.processingFilter = processingFilter;
    }

    private void addTask(JavaClass klass) {
        taskSet.tryAddTask(klass);
    }

    public Result run() {
        MutableMatrix gen = new MutableMatrix();
        MutableMatrix stat = new MutableMatrix();
        MutableMatrix exec = new MutableMatrix();

        while (taskSet.moreTasks())
            process(taskSet.getNextTask(), gen, stat, exec);

        return new Result(gen, stat, exec);
    }

    private void process(Task task, MutableMatrix gen, MutableMatrix stat, MutableMatrix exec) {
        processInheritance(task, gen);
        processFields(task, stat, exec);
        processMethods(task, stat, exec);

        taskSet.setFinished(task);
    }

    private void processInheritance(Task task, MutableMatrix gen) {
        JavaClass c = task.getJavaClass();

        try {
            JavaClass sup = c.getSuperClass();
            if (sup != null) {
                put(gen, c, sup);

                if (processingFilter.shouldProcess(sup)) {
                    addTask(sup);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        try {
            for (JavaClass i : c.getInterfaces()) {
                put(gen, c, i);

                if (processingFilter.shouldProcess(i)) {
                    addTask(i);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void processFields(Task task, MutableMatrix stat, MutableMatrix exec) {
        JavaClass c = task.getJavaClass();

        for (Field f : c.getFields()) {
            try {
                Type t = f.getType();
                processType(c, t, stat);
                processType(c, t, exec);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processMethods(Task task, MutableMatrix stat, MutableMatrix exec) {
        JavaClass c = task.getJavaClass();

        for (Method m : c.getMethods()) {
            try {
                processType(c, m.getReturnType(), stat);
                processType(c, m.getReturnType(), exec);

                for (Type t : m.getArgumentTypes()) {
                    processType(c, t, stat);
                }

                if (c.isClass() && !m.isAbstract())
                    processCode(c, m, stat, exec);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processCode(JavaClass c, Method m, MutableMatrix stat, MutableMatrix exec) {
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

                            JavaClass field = loadClass(klassName);
                            put(stat, c, field);
                            put(exec, c, field);

                            if (processingFilter.shouldProcess(field)) {
                                addTask(field);
                            }
                        }
                    }
                }
                if (i instanceof TypedInstruction) {
                    TypedInstruction ti = (TypedInstruction) i;
                    try {
                        processType(c, ti.getType(gen), exec);
                    } catch (NullPointerException npe) {
                        System.out.println(i);
                    }
                } else if (i instanceof NEWARRAY) {
                    NEWARRAY na = (NEWARRAY) i;
                    processType(c, na.getType(), exec);
                }
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processType(JavaClass c, Type t, MutableMatrix matrix) throws ClassNotFoundException {
        if (t instanceof ReferenceType) {
            if (t instanceof ObjectType) {
                ObjectType ot = (ObjectType) t;

                JavaClass field = loadClass(ot.getClassName());
                put(matrix, c, field);

                if (processingFilter.shouldProcess(field)) {
                    addTask(field);
                }
            } else if (t instanceof ArrayType) {
                ArrayType at = (ArrayType) t;
                t = at.getBasicType();

                if (t instanceof ObjectType) {
                    ObjectType ot = (ObjectType) t;

                    JavaClass field = loadClass(ot.getClassName());
                    put(matrix, c, field);

                    if (processingFilter.shouldProcess(field)) {
                        addTask(field);
                    }
                }
            }
        }
    }

    private JavaClass loadClass(String name) throws ClassNotFoundException {
        if (cache.containsKey(name))
            return cache.get(name);
        else if (bad.contains(name))
            throw new ClassNotFoundException(name + " does not exist");
        else {
            try {
                JavaClass klass = repo.loadClass(name);
                cache.put(name, klass);
                return klass;
            } catch (ClassNotFoundException cnfe) {
                bad.add(name);
                throw cnfe;
            }
        }
    }

    private void put(MutableMatrix m, JavaClass from, JavaClass to) {
        String fromName = from.getClassName();
        String toName = to.getClassName();

        //if (!fromName.startsWith("java") && !toName.startsWith("java"))
            m.put(from, to);
    }

    public static class Result {

        public final MutableMatrix inheritance;
        public final MutableMatrix staticDependencies;
        public final MutableMatrix executableDependencies;

        public Result(MutableMatrix inheritance, MutableMatrix staticDependencies, MutableMatrix executableDependencies) {
            this.inheritance = inheritance;
            this.staticDependencies = staticDependencies;
            this.executableDependencies = executableDependencies;
        }
    }
}
