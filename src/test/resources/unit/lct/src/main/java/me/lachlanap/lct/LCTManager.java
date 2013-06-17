package me.lachlanap.lct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import me.lachlanap.lct.data.ClassInspector;
import me.lachlanap.lct.data.ConstantField;
import me.lachlanap.lct.data.ConstantFieldFactory;
import me.lachlanap.lct.data.IntConstantField;

/**
 *
 * @author lachlan
 */
public class LCTManager {

    private final List<ConstantField> constantList;
    private final Map<String, ConstantField> constants;
    private final ClassInspector inspector;

    public LCTManager() {
        this(new ClassInspector(new ConstantFieldFactory()));
    }

    public LCTManager(ConstantFieldFactory factory) {
        this(new ClassInspector(factory));
    }

    public LCTManager(ClassInspector inspector) {
        constantList = new ArrayList<>();
        constants = new HashMap<>();
        this.inspector = inspector;
    }

    public void addConstant(ConstantField field) {
        constantList.add(field);
        constants.put(field.name, field);
    }

    public List<ConstantField> getFields() {
        return Collections.unmodifiableList(constantList);
    }

    public void register(Class<?> aClass) {
        List<ConstantField> tmp = inspector.getConstants(aClass);

        constantList.addAll(tmp);
        for (ConstantField constant : tmp)
            constants.put(constant.name, constant);
    }

    public void loadSettings(Properties props) {
        for (ConstantField constant : constantList) {
            constant.loadFromProperties(props);
        }
    }

    public void loadSettings(String file) throws IOException {
        Path path = Paths.get(file);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(reader);
            loadSettings(props);
        }
    }

    public void saveSettings(Properties props) {
        for (ConstantField constant : constantList) {
            constant.saveToProperties(props);
        }
    }

    public void saveSettings(String file) throws IOException {
        Path path = Paths.get(file);

        try (BufferedWriter reader = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            saveSettings(props);
            props.store(reader, "Saved by LiveConstantTweaker");
        }
    }

    public void set(String name, int i) {
        ConstantField constant = constants.get(name);
        ((IntConstantField) constant).set(i);
    }
}
