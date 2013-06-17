package me.lachlanap.lct.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import me.lachlanap.lct.LCTManager;
import me.lachlanap.lct.data.ConstantField;

/**
 *
 * @author lachlan
 */
public class LCTEditor extends JPanel {

    private final LCTManager manager;
    private final ConstantEditorFactory factory;

    public LCTEditor(LCTManager manager) {
        this(manager, new ConstantEditorFactory());
    }

    public LCTEditor(LCTManager manager, ConstantEditorFactory factory) {
        this.manager = manager;
        this.factory = factory;

        setLayout(new BorderLayout());

        JPanel pane = buildPane();

        JScrollPane scrollPane = new JScrollPane(pane,
                                                 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(BorderLayout.CENTER, scrollPane);
    }

    public LCTManager getManager() {
        return manager;
    }

    private JPanel buildPane() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        Map<String, List<ConstantField>> mappedConstants = aggregateMappedConstants(manager.getFields());

        for (Map.Entry<String, List<ConstantField>> constantClass : mappedConstants.entrySet()) {
            JPanel group = new JPanel();
            group.setBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1, true),
                    constantClass.getKey()));
            group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
            root.add(group);

            for (ConstantField constant : constantClass.getValue()) {
                ConstantEditor editor = factory.createEditor(constant);
                group.add(editor);
            }
        }

        root.add(Box.createVerticalGlue());

        return root;
    }

    private Map<String, List<ConstantField>> aggregateMappedConstants(List<ConstantField> fields) {
        Map<String, List<ConstantField>> mappedConstants = new HashMap<>();

        for (ConstantField constant : fields) {
            if (mappedConstants.containsKey(constant.container.getSimpleName())) {
                List<ConstantField> list = mappedConstants.get(constant.container.getSimpleName());
                list.add(constant);
            } else {
                List<ConstantField> list = new ArrayList<>();
                list.add(constant);
                mappedConstants.put(constant.container.getSimpleName(), list);
            }
        }

        return mappedConstants;
    }
}
