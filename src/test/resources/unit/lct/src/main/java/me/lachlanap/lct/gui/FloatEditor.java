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
import me.lachlanap.lct.data.FloatConstantField;

/**
 *
 * @author lachlan
 */
public class FloatEditor extends ConstantEditor {

    private static final int RESOLUTION = 10000;
    private final FloatConstantField constant;
    private final JSlider slider;
    private final JTextField value;
    private final float shift, scale;
    private final DecimalFormat df = new DecimalFormat("0.####");

    public FloatEditor(FloatConstantField constant) {
        super(constant);
        this.constant = constant;

        df.setMinimumFractionDigits(2);

        setLayout(new GridBagLayout());

        float min = (constant.min == Float.MIN_VALUE) ? -100 : constant.min;
        float max = (constant.max == Float.MAX_VALUE) ? 100 : constant.max;
        float constantValue = constant.get();

        shift = min;
        scale = 1f / ((max - shift) / RESOLUTION);

        add(new JLabel(constant.name), new GBC(0, 0, 5));

        slider = new JSlider(0, RESOLUTION);
        slider.setMajorTickSpacing(RESOLUTION / 10);
        slider.setSnapToTicks(false);
        slider.setLabelTable(makeLabelTable());
        slider.setPaintLabels(true);
        slider.setValue(f2i(constantValue));
        slider.addChangeListener(new SliderChangeListener());
        add(slider, new GBC(1, 0, 5).weight(100, 0).fillX());

        value = new JTextField(String.valueOf(constantValue), 7);
        value.addKeyListener(new TextFieldKeyListener());
        add(value, new GBC(2, 0, 5));

        setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                     getLayout().preferredLayoutSize(this).height));
    }

    private int f2i(float d) {
        return (int) ((d - shift) * scale);
    }

    private float i2f(int i) {
        return (i / scale) + shift;
    }

    private Hashtable<Integer, JLabel> makeLabelTable() {
        Hashtable<Integer, JLabel> table = new Hashtable<>();

        for (int i = 0; i <= 10; i++) {
            int val = RESOLUTION / 10 * i;
            table.put(val, new JLabel(df.format(i2f(val))));
        }

        return table;
    }

    private class SliderChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            float val = i2f(slider.getValue());

            try {
                float prev = Float.parseFloat(value.getText());
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
                float val = Float.parseFloat(value.getText());

                slider.setValue(f2i(val));
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
