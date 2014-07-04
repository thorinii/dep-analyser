package me.lachlanap.lct.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import me.lachlanap.lct.data.DoubleConstantField;

/**
 *
 * @author lachlan
 */
public class DoubleEditor extends ConstantEditor {

    private static final int RESOLUTION = 10000;
    private final DoubleConstantField constant;
    private final JSlider slider;
    private final JTextField value;
    private final double shift, scale;
    private final DecimalFormat df = new DecimalFormat("0.####");

    public DoubleEditor(DoubleConstantField constant) {
        super(constant);
        this.constant = constant;

        df.setMinimumFractionDigits(2);

        setLayout(new GridBagLayout());

        double min = (constant.min == Double.MIN_VALUE) ? -100 : constant.min;
        double max = (constant.max == Double.MAX_VALUE) ? 100 : constant.max;
        double constantValue = constant.get();

        shift = min;
        scale = 1.0 / ((max - shift) / RESOLUTION);

        add(new JLabel(constant.name), new GBC(0, 0, 5));

        slider = new JSlider(0, RESOLUTION);
        slider.setMajorTickSpacing(RESOLUTION / 10);
        slider.setSnapToTicks(false);
        slider.setLabelTable(makeLabelTable());
        slider.setPaintLabels(true);
        slider.setValue(d2i(constantValue));
        slider.addChangeListener(new SliderChangeListener());
        add(slider, new GBC(1, 0, 5).weight(100, 0).fillX());

        value = new JTextField(String.valueOf(constantValue), 7);
        value.addKeyListener(new TextFieldKeyListener());
        add(value, new GBC(2, 0, 5));

        setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                     getLayout().preferredLayoutSize(this).height));
    }

    private int d2i(double d) {
        return (int) ((d - shift) * scale);
    }

    private double i2d(int i) {
        return (i / scale) + shift;
    }

    private Hashtable<Integer, JLabel> makeLabelTable() {
        Hashtable<Integer, JLabel> table = new Hashtable<>();

        for (int i = 0; i <= 10; i++) {
            int val = RESOLUTION / 10 * i;
            table.put(val, new JLabel(df.format(i2d(val))));
        }

        return table;
    }

    private class SliderChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            double val = i2d(slider.getValue());

            try {
                double prev = Double.parseDouble(value.getText());
                if (val == prev)
                    return;
            } catch (NumberFormatException nfe) {
            }

            value.setText(df.format(val));
            constant.set(val);
        }
    }

    private class TextFieldKeyListener implements KeyListener {

        @Override
        public void keyReleased(KeyEvent e) {
            try {
                double val = Double.parseDouble(value.getText());

                slider.setValue(d2i(val));
                constant.set(val);
            } catch (NumberFormatException nfe) {
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
    }
}
