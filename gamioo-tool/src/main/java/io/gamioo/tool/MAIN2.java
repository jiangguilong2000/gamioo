/*
 * Created by JFormDesigner on Sat Jul 16 03:40:19 CST 2022
 */

package io.gamioo.tool;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import javax.swing.border.*;
import com.intellij.uiDesigner.core.*;
import com.jgoodies.forms.factories.*;

/**
 * @author unknown
 */
public class MAIN2 extends JFrame {
    public MAIN2() throws AWTException {
        initComponents();
    }

    private void initComponents() throws AWTException {
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("arrow.png")).getPath();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage(path);  //图片路径或
        TrayIcon trayIcon = new TrayIcon(image);
        // set icon auto resize
        trayIcon.setImageAutoSize(true);
        SystemTray.getSystemTray().add(trayIcon);
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
        file = new JMenu();
        menuItem1 = new JMenuItem();
        menuItem2 = new JMenuItem();
        menu1 = new JMenu();
        panel1 = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        panel2 = new JPanel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();

        //======== this ========
        setIconImage(new ImageIcon(getClass().getResource("/arrow.png")).getImage());
        setTitle("gamioo");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== file ========
            {
                file.setText("File");
                file.setToolTipText("file is here");

                //---- menuItem1 ----
                menuItem1.setText("new");
                file.add(menuItem1);

                //---- menuItem2 ----
                menuItem2.setText("open");
                file.add(menuItem2);
            }
            menuBar1.add(file);

            //======== menu1 ========
            {
                menu1.setText("Edit");
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //======== panel1 ========
        {
            panel1.setBorder(new CompoundBorder(
                new TitledBorder("\u73a9\u5bb6\u8bbe\u7f6e"),
                Borders.DLU2));
            panel1.setToolTipText("\u73a9\u5bb6\u8bbe\u7f6e");
            panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));

            //---- label1 ----
            label1.setText("\u62bc\u6ce8\u7b56\u7565");
            panel1.add(label1, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

            //---- textField1 ----
            textField1.setText("\u667a\u80fd\u62bc\u6ce8");
            panel1.add(textField1, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        }
        contentPane.add(panel1, BorderLayout.NORTH);

        //======== panel2 ========
        {
            panel2.setBorder(new CompoundBorder(
                new TitledBorder("\u6e38\u620f\u65e5\u5fd7"),
                Borders.DLU2));
            panel2.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));

            //======== scrollPane1 ========
            {

                //---- textArea1 ----
                textArea1.setBackground(new Color(0, 31, 40));
                textArea1.setForeground(new Color(131, 148, 150));
                scrollPane1.setViewportView(textArea1);
            }
            panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        }
        contentPane.add(panel2, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu file;
    private JMenuItem menuItem1;
    private JMenuItem menuItem2;
    private JMenu menu1;
    private JPanel panel1;
    private JLabel label1;
    private JTextField textField1;
    private JPanel panel2;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
