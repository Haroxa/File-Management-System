package gui;

import console.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * TODO 用户主界面
 *
 * @author Haroxa
 * @date 2023/11/16
 */
public class MainGui {
    static final String[] TEXTS = {"用户管理", "档案管理", "个人中心", "退出登录"};
    static final Dimension MANAGE_DIMENSION = new Dimension(200, 30);

    public static void main(String[] args) {

    }
    public static void start(MyFrame parentGui) {
        SwingUtilities.invokeLater(() -> {
            MyFrame mainGui = newMainGui();
            mainGui.setParentGui(parentGui);
            mainGui.setVisible(true);
        });
    }

    /**
     * TODO 创建主窗口
     *
     * @param
     * @return MyFrame
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

        // 初始化子窗口
        MyFrame userManageGui = UserGui.newUserManageGui();
        MyFrame personManageGui = PersonGui.newPersonManageGui();
        MyFrame docManageGui = DocGui.newDocManageGui();
        userManageGui.parentGui = personManageGui.parentGui = docManageGui.parentGui = jFrame;

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.printf("{ msg:userGui/newMainGui/activated, nowUser:%s }\n", jFrame.user);
                // 激活窗口时，检查当前用户合法性， 不合法，返回登录界面
                if ( !jFrame.checkUserValid() ) {
                    jFrame.showParentGui();
                    return;
                }
                // 设置非管理员不可管理用户信息
                userManageButton.setEnabled(jFrame.user.getRole().equals(DataProcessing.Role.ADMINISTRATOR.getName()));
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("{ msg:userGui/newMainGui/closing, nowUser:%s }\n", jFrame.user);
                // 关闭窗口时，检查子窗口是否已关闭
                jFrame.closeCurrentGui();
            }
        });
        // 设置各个子窗口
        userManageButton.addActionListener(e -> jFrame.setCurrentGui(userManageGui));
        docManageButton.addActionListener(e -> jFrame.setCurrentGui(docManageGui));
        personManageButton.addActionListener(e -> jFrame.setCurrentGui(personManageGui));
        logOutButton.addActionListener(e -> jFrame.showParentGui() );
        return jFrame;
    }
}
