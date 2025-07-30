package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import console.*;
import serializable.*;

/**
 * TODO 用户主界面
 *
 * @author Haroxa
 * @date 2023/11/16
 */
public class MainGui {
    static final String[] TEXTS = {"用户管理", "档案管理", "个人中心", "退出登录"};
    static final Dimension MANAGE_DIMENSION = new Dimension(200, 30);
    static AbstractUser user;
    static MyFrame mainGui;
    static MyFrame userManageGui;
    static MyFrame personManageGui;
    static MyFrame docManageGui;

    static {
        // 初始化
        mainGui = newMainGui();
        userManageGui = UserGui.userManageGui;
        personManageGui = PersonGui.personManageGui;
        docManageGui = DocGui.docManageGui;
    }

    public static void main(String[] args) {
        AbstractUser user = new Administrator("kate", "123");
        start(user);
    }

    public static void start(AbstractUser u) {
        setAllUser(u);
        mainGui.setVisible(true);
    }

    /**
     * TODO 设置当前用户，保证各界面用户的一致性
     *
     * @param u 指定用户
     * @return void
     * @throws
     */
    public static void setAllUser(AbstractUser u) {
        user = u;
        UserGui.setUser(user);
        PersonGui.setUser(user);
        DocGui.setUser(user);
    }

    /**
     * TODO 创建主窗口
     *
     * @param
     * @return gui.MyFrame
     * @throws
     */
    public static MyFrame newMainGui() {
        MyFrame jFrame = new MyFrame("主窗口", 350, 300);
        int be = 1, panelNum = 6;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JButton userManageButton = MyFrame.addButton(jPanels[be], TEXTS[0]);
        userManageButton.setPreferredSize(MANAGE_DIMENSION);
        JButton docManageButton = MyFrame.addButton(jPanels[be + 1], TEXTS[1]);
        docManageButton.setPreferredSize(MANAGE_DIMENSION);
        JButton personManageButton = MyFrame.addButton(jPanels[be + 2], TEXTS[2]);
        personManageButton.setPreferredSize(MANAGE_DIMENSION);
        JButton logOutButton = MyFrame.addButton(jPanels[be + 3], TEXTS[3]);
        logOutButton.setPreferredSize(MANAGE_DIMENSION);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1, 0, 0));

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.printf("{ msg:userGui/newMainGui/activated, nowUser:%s }\n", user);
                // 激活窗口时，检查当前用户合法性， 不合法，返回登录界面
                if ( !jFrame.checkUserValid(user) ) {
                    LoginGui.start();
                    return;
                }
                // 设置非管理员不可管理用户信息
                userManageButton.setEnabled(user.getRole().equals(DataProcessing.Role.ADMINISTRATOR.getName()));
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("{ msg:userGui/newMainGui/closing, nowUser:%s }\n", user);
                // 关闭窗口时，检查子窗口是否已关闭
                jFrame.closeCurrentGui();
            }
        });
        // 设置各个子窗口
        userManageButton.addActionListener(e -> jFrame.setCurrentGui(userManageGui));
        docManageButton.addActionListener(e -> jFrame.setCurrentGui(docManageGui));
        personManageButton.addActionListener(e -> jFrame.setCurrentGui(personManageGui));
        logOutButton.addActionListener(e -> {
            jFrame.dispose();
            LoginGui.start();
        });
        return jFrame;
    }
}
