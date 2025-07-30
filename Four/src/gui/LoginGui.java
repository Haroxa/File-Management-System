package gui;

//import console.AbstractUser;
//import console.DataProcessing;
import serializable.AbstractUser;
import serializable.DataProcessing;

import javax.swing.*;
import java.awt.*;

/**
 * TODO 登录界面
 *
 * @author Haroxa
 * @date 2023/11/16
 */
public class LoginGui {
    static final String[] TEXTS = {"档案管理系统", "用户名: ", "密    码: "};
    static final String[] loginTexts = {"登录", "取消"};
    static final int SIZE = 12;
    static MyFrame loginGui;
    static MyFrame mainGui;
    static {
        loginGui = newLoginGui();
        mainGui = MainGui.mainGui;
    }

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        loginGui.setVisible(true);
    }

    public static MyFrame newLoginGui() {
        MyFrame jFrame = new MyFrame("系统登录", 350, 300);
        int be = 1, panelNum = 7;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JLabel label = new JLabel(TEXTS[0]);
        Font font = label.getFont();
        label.setFont(font.deriveFont((float) (SIZE * 1.8)));
        jPanels[be].add(label);
        JTextField nameField = MyFrame.addNameLabelAndTextField(jPanels[be + 2], MyFrame.USER_TEXTS[0]);
        JPasswordField passwordField = MyFrame.addPasswordLabelAndTextField(jPanels[be + 3], MyFrame.USER_TEXTS[1]);

        JButton loginButton = jFrame.addConfirmAndCancelButton(jPanels[be + 4], loginTexts);

        jFrame.addJPanels(jPanels);
        // 设置布局方式：流式布局（行数、列数、组件水平间距、纵向间距）
        jFrame.setLayout(new GridLayout(panelNum, 1, 0, 0));

        loginButton.addActionListener(e -> {
            String name = nameField.getText();
            String password = new String(passwordField.getPassword());
            System.out.printf("{ msg:userGui/newLoginGui/login, name:%s, password:%s }\n", name, password);
            if(!AbstractUser.checkName(name)){
                jFrame.showTip("用户名格式错误");
            } else if (!AbstractUser.checkPassword(password)) {
                jFrame.showTip("密码格式错误");
            }else{
                AbstractUser user = DataProcessing.verifyUser(name, password);
                if (user != null) {
                    jFrame.showTip("登录成功");
                    jFrame.dispose();
                    MainGui.start(user);
                } else {
                    jFrame.showTip("密码错误或用户名不存在");
                }
            }
        });
        return jFrame;
    }
}
