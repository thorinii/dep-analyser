package me.lachlanap.lct;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import me.lachlanap.lct.gui.LCTFrame;

/**
 *
 * @author lachlan
 */
public class MainTesting {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException |
                IllegalAccessException |
                InstantiationException |
                UnsupportedLookAndFeelException e) {
            // We don't particularly care if the LaF cannot be set
            e.printStackTrace();
        }

        LCTManager lct = new LCTManager();
        lct.register(ClassOfConstantTesting.class);
        lct.register(SecondSetOfConstants.class);

        LCTFrame frame = new LCTFrame(lct);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static class ClassOfConstantTesting {

        @Constant(name = "Number", constraints = "1,13")
        public static int NUMBER = 10;
        @Constant(name = "Integer", constraints = ",8")
        public static int INTEGER = 6;
        @Constant(name = "Long", constraints = ",1000")
        public static long LONG = 6;
    }

    public static class SecondSetOfConstants {

        @Constant(name = "Cycles", constraints = "0,50")
        public static int CYCLES = 7;
        @Constant(name = "Gravity", constraints = "0,10")
        public static double GRAVITY = 9.81;
        @Constant(name = "Drag", constraints = "0,10")
        public static float DRAG = 5;
    }
}
