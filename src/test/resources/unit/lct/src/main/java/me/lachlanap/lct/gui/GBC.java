package me.lachlanap.lct.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 *
 * @author lachlan
 */
public class GBC extends GridBagConstraints {

    public GBC(int x, int y) {
        gridx = x;
        gridy = y;
    }

    public GBC(int x, int y, int i) {
        gridx = x;
        gridy = y;
        insets = new Insets(i, i, i, i);
    }

    public GBC weight(int x, int y) {
        weightx = x;
        weighty = y;
        return this;
    }

    public GBC fillX() {
        fill = GBC.HORIZONTAL;
        return this;
    }

    public GBC fillY() {
        fill = GBC.VERTICAL;
        return this;
    }

    public GBC fillBoth() {
        fill = GBC.BOTH;
        return this;
    }
}
