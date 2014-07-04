package me.lachlanap.lct.gui;

import javax.swing.JPanel;
import me.lachlanap.lct.data.ConstantField;

/**
 *
 * @author lachlan
 */
public class ConstantEditor extends JPanel {

    private final ConstantField constant;

    public ConstantEditor(ConstantField constant) {
        this.constant = constant;
    }

    public ConstantField getConstant() {
        return constant;
    }
}
