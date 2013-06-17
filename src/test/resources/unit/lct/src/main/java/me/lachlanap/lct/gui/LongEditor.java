package me.lachlanap.lct.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import me.lachlanap.lct.data.LongConstantField;

/**
 *
 * @author lachlan
 */
public class LongEditor extends ConstantEditor {

    private final LongConstantField constant;
    private final JSlider slider;
    private final JTextField value;

    public LongEditor(LongConstantField constant) {
        super(constant);
        this.constant = constant;

        int min = (int) ((constant.min == Integer.MIN_VALUE) ? -100 : constant.min);
        int max = (int) ((constant.max == Integer.MAX_VALUE) ? 100 : constant.max);
        min = Math.min(min, max);
        max = Math.max(max, min);

        int constantValue = (int) constant.get();

        setLayout(new GridBagLayout());

        add(new JLabel(constant.name), new GBC(0, 0, 5));

        slider = new JSlider(min, max);
        slider.setMajorTickSpacing(Math.max(1, (max - min) / 5));
        slider.setMinorTickSpacing(1);
        slider.setSnapToTicks(true);
        slider.setPaintLabels(true);
        slider.setValue(constantValue);
        slider.addChangeListener(new SliderChangeListener());
        add(slider, new GBC(1, 0, 5).weight(100, 0).fillX());

        value = new JTextField(String.valueOf(constantValue), 7);
        value.addKeyListener(new TextFieldKeyListener());
        add(value, new GBC(2, 0, 5));

        setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                     getLayout().preferredLayoutSize(this).height));
    }

    private class SliderChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            int val = slider.getValue();

            try {
                int prev = Integer.parseInt(value.getText());
                if (val == prev)
                    return;
            } catch (NumberFormatException nfe) {
            }

            value.setText(String.valueOf(val));
            constant.set(val);
        }
    }

    private class TextFieldKeyListener implements KeyListener {

        @Override
        public void keyReleased(KeyEvent e) {
            try {
                int val = Integer.parseInt(value.getText());

                slider.setValue(val);
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
