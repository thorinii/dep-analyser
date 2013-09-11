/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.lachlanap.dependencyanalyser.analysis.DependencyType;
import me.lachlanap.dependencyanalyser.graph.Matrix;
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
    private final Matrix matrix;
    private final Map<String, JavaClass> cache = new HashMap<>();
    private final Set<String> bad = new HashSet<>();
    private ProcessingFilter processingFilter;

    public Processor(Repository repo, TaskSet taskSet, Matrix matrix) {
        this.repo = repo;
        this.taskSet = taskSet;
        this.processingFilter = new NullProcessingFilter();
        this.matrix = matrix;
    }

    public void setProcessingFilter(ProcessingFilter processingFilter) {
        this.processingFilter = processingFilter;
    }

    private void addTask(JavaClass klass) {
        taskSet.tryAddTask(klass);
    }

    public void run() {
        while (taskSet.moreTasks())
            process(taskSet.getNextTask());
    }

    private void process(Task task) {
        processInheritance(task);
        processFields(task);
        processMethods(task);

        taskSet.setFinished(task);
    }

    private void processInheritance(Task task) {
        JavaClass c = task.getJavaClass();

        try {
            JavaClass sup = c.getSuperClass();
            if (sup != null) {
                matrix.put(c, sup, DependencyType.Genealogical);

                if (processingFilter.shouldProcess(sup)) {
                    addTask(sup);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        try {
            for (JavaClass i : c.getInterfaces()) {
                matrix.put(c, i, DependencyType.Genealogical);

                if (processingFilter.shouldProcess(i)) {
                    addTask(i);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void processFields(Task task) {
        JavaClass c = task.getJavaClass();

        for (Field f : c.getFields()) {
            try {
                Type t = f.getType();
                processType(c, t, DependencyType.Static);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processMethods(Task task) {
        JavaClass c = task.getJavaClass();

        for (Method m : c.getMethods()) {
            try {
                processType(c, m.getReturnType(), DependencyType.Static);

                for (Type t : m.getArgumentTypes()) {
                    processType(c, t, DependencyType.Static);
                }

                if (c.isClass() && !m.isAbstract())
                    processCode(c, m);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processCode(JavaClass c, Method m) {
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
                            matrix.put(c, field, DependencyType.Static);

                            if (processingFilter.shouldProcess(field)) {
                                addTask(field);
                            }
                        }
                    }
                }
                if (i instanceof TypedInstruction) {
                    TypedInstruction ti = (TypedInstruction) i;
                    try {
                        processType(c, ti.getType(gen), DependencyType.Executable);
                    } catch (NullPointerException npe) {
                        System.out.println(i);
                    }
                } else if (i instanceof NEWARRAY) {
                    NEWARRAY na = (NEWARRAY) i;
                    processType(c, na.getType(), DependencyType.Executable);
                }
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processType(JavaClass c, Type t, DependencyType kind) throws ClassNotFoundException {
        if (t instanceof ReferenceType) {
            if (t instanceof ObjectType) {
                ObjectType ot = (ObjectType) t;

                JavaClass field = loadClass(ot.getClassName());
                matrix.put(c, field, kind);

                if (processingFilter.shouldProcess(field)) {
                    addTask(field);
                }
            } else if (t instanceof ArrayType) {
                ArrayType at = (ArrayType) t;
                t = at.getBasicType();

                if (t instanceof ObjectType) {
                    ObjectType ot = (ObjectType) t;

                    JavaClass field = loadClass(ot.getClassName());
                    matrix.put(c, field, kind);

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
}
