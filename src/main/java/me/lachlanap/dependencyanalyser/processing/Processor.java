/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.lachlanap.dependencyanalyser.analysis.Analysis;
import me.lachlanap.dependencyanalyser.analysis.ClassResult;
import me.lachlanap.dependencyanalyser.analysis.Dependency;
import me.lachlanap.dependencyanalyser.analysis.DependencyType;
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
    private final Analysis analysis;
    private final Map<String, JavaClass> cache = new HashMap<>();
    private final Set<String> bad = new HashSet<>();
    private ProcessingFilter processingFilter;

    public Processor(Repository repo, TaskSet taskSet, Analysis analysis) {
        this.repo = repo;
        this.taskSet = taskSet;
        this.analysis = analysis;
        this.processingFilter = new NullProcessingFilter();
    }

    public void setProcessingFilter(ProcessingFilter processingFilter) {
        this.processingFilter = processingFilter;
    }

    public void run() {
        while (taskSet.moreTasks()) {
            process(taskSet.getNextTask());
        }
    }

    private void process(Task task) {
        ClassResult output = new ClassResult(task.getJavaClass());
        processInheritance(task, output);
        processFields(task, output);
        processMethods(task, output);

        taskSet.setFinished(task);
        analysis.add(output);
    }

    private void processInheritance(Task task, ClassResult result) {
        JavaClass c = task.getJavaClass();

        try {
            JavaClass sup = c.getSuperClass();
            if (sup != null) {
                result.addDependency(new Dependency(DependencyType.Genealogical, sup));

                if (processingFilter.shouldProcess(sup)) {
                    addTask(sup);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        try {
            for (JavaClass i : c.getInterfaces()) {
                result.addDependency(new Dependency(DependencyType.Genealogical, i));

                if (processingFilter.shouldProcess(i)) {
                    addTask(i);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void processFields(Task task, ClassResult result) {
        JavaClass c = task.getJavaClass();

        for (Field f : c.getFields()) {
            try {
                Type t = f.getType();
                processType(t, result, DependencyType.Static);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processMethods(Task task, ClassResult result) {
        JavaClass c = task.getJavaClass();

        for (Method m : c.getMethods()) {
            try {
                processType(m.getReturnType(), result, DependencyType.Static);

                for (Type t : m.getArgumentTypes()) {
                    processType(t, result, DependencyType.Static);
                }

                if (c.isClass() && !m.isAbstract())
                    processCode(c, m, result);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void processCode(JavaClass c, Method m, ClassResult result) {
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
                            result.addDependency(new Dependency(DependencyType.Executable, field));

                            if (processingFilter.shouldProcess(field)) {
                                addTask(field);
                            }
                        }
                    }
                }
                if (i instanceof TypedInstruction) {
                    TypedInstruction ti = (TypedInstruction) i;
                    try {
                        processType(ti.getType(gen), result, DependencyType.Executable);
                    } catch (NullPointerException npe) {
                        System.out.println(i);
                    }
                } else if (i instanceof NEWARRAY) {
                    NEWARRAY na = (NEWARRAY) i;
                    processType(na.getType(), result, DependencyType.Executable);
                }
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }

    private void addTask(JavaClass klass) {
        taskSet.tryAddTask(klass);
    }

    private void processType(Type t, ClassResult result, DependencyType kind) throws ClassNotFoundException {
        if (t instanceof ReferenceType) {
            if (t instanceof ObjectType) {
                ObjectType ot = (ObjectType) t;

                JavaClass field = loadClass(ot.getClassName());
                result.addDependency(new Dependency(kind, field));

                if (processingFilter.shouldProcess(field)) {
                    addTask(field);
                }
            } else if (t instanceof ArrayType) {
                ArrayType at = (ArrayType) t;
                t = at.getBasicType();

                if (t instanceof ObjectType) {
                    ObjectType ot = (ObjectType) t;

                    JavaClass field = loadClass(ot.getClassName());
                    result.addDependency(new Dependency(kind, field));

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
