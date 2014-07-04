package me.lachlanap.lct.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import me.lachlanap.lct.LCTManager;

/**
 *
 * @author lachlan
 */
public class LCTFrame extends JFrame {

    private final LCTEditor editor;

    public LCTFrame(LCTManager manager) {
        this(manager, "Live Constant Tweaker");
    }

    public LCTFrame(LCTManager manager, String title) {
        super(title);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        editor = new LCTEditor(manager);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        c.add(BorderLayout.CENTER, editor);

        pack();
        setSize(Math.max(500, getWidth()), getHeight());
    }
}
