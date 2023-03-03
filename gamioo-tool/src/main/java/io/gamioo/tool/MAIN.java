package io.gamioo.tool;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class MAIN {
    public static void main(String[] args) throws Exception {
//
//        FlatDarkLaf.setup();
//        FlatDraculaIJTheme.setup();
//        //  BeautyEyeLNFHelper.launchBeautyEyeLNF();
//        MAIN2 frame = new MAIN2();
//        frame.setSize(400, 300);
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        //   frame.pack();
//        frame.setVisible(true);
        FlatDarkLaf.setup();
        MAIN2 frame = new MAIN2();


        frame.setSize(600, 400);
        frame.setLocationRelativeTo((Component) null);
        //  frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);


    }
}
