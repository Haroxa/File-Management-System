package gui;

//import console.AbstractUser;
//import console.Administrator;
//import console.DataProcessing;
import serializable.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * TODO 个人管理界面
 *
 * @author Haroxa
 * @date 2023/11/20
 */
public class PersonGui {
    static final String[] UPDATE_PASSWORD_TEXTS = {"修改密码", "关闭窗口"};
    static final String[] PASSWORD_TEXTS = {"原  密  码", "新  密  码", "确认密码"};
    static AbstractUser user;
    static MyFrame personManageGui;
    static MyFrame updatePasswordGui;

    static {
        // 初始化
        DataProcessing.init();
        personManageGui = newPersonManageGui();
        updatePasswordGui = newUpdatePasswordGui();
    }

    public static void main(String[] args) {
        AbstractUser user = new Administrator("kate", "123");
        start(user);
    }

    /**
     * TODO 设置当前用户
     *
     * @param u 指定用户
     * @return void
     * @throws
     */
    public static void setUser(AbstractUser u) {
        user = u;
    }

    public static void start(AbstractUser u) {
        user = u;
        personManageGui.setVisible(true);
    }

    /**
     * TODO 创建personManage主窗口
     *
     * @param
     * @return gui.MyFrame
     * @throws
     */
    public static MyFrame newPersonManageGui() {
        MyFrame jFrame = new MyFrame("个人中心", 300, 300);
        int be = 1, panelNum = 5;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);
        // 添加各个组件，并设置为不可编辑
        JTextField nameField = MyFrame.addNameLabelAndTextField(jPanels[be], MyFrame.USER_TEXTS[0]);
        nameField.setEnabled(false);
        JPasswordField passwordField = MyFrame.addPasswordLabelAndTextField(jPanels[be + 1], MyFrame.USER_TEXTS[1]);
        passwordField.setEnabled(false);
        JComboBox<String> comboBox = MyFrame.addRoleLabelAndComboBox(jPanels[be + 2], MyFrame.ROLE_OPTIONS);
        comboBox.setEnabled(false);
        JButton updatePasswordButton = jFrame.addConfirmAndCancelButton(jPanels[be + 3], UPDATE_PASSWORD_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1, 0, 0));
        // 设置指定位置
        jFrame.setLocation(950, 200);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.printf("{ msg:userGui/newPersonManageGui/activated, nowUser:%s }\n", user);
                // 激活窗口时，检查当前用户合法性， 不合法，返回主界面
                if ( !jFrame.checkUserValid(user) ) {
                    MainGui.start(user);
                    return;
                }
                // 显示用户信息
                nameField.setText(user.getName());
                passwordField.setText(user.getPassword());
                comboBox.setSelectedItem(user.getRole());
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("{ msg:userGui/newPersonManageGui/closed, nowUser:%s }\n", user);
                // 关闭窗口时，检查子窗口是否已关闭
                jFrame.closeCurrentGui();
            }
        });
        // 设置修改密码窗口
        updatePasswordButton.addActionListener(e -> jFrame.setCurrentGui(updatePasswordGui));
        return jFrame;
    }

    /**
     * TODO 创建updatePassword子窗口
     *
     * @param
     * @return gui.MyFrame
     * @throws
     */
    public static MyFrame newUpdatePasswordGui() {
        MyFrame jFrame = new MyFrame("修改密码", 300, 200);
        int be = 0, panelNum = 4;
        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JPasswordField oldPasswordField = MyFrame.addPasswordLabelAndTextField(jPanels[be], PASSWORD_TEXTS[0]);
        JPasswordField newPasswordField = MyFrame.addPasswordLabelAndTextField(jPanels[be + 1], PASSWORD_TEXTS[1]);
        JPasswordField confirmPasswordField = MyFrame.addPasswordLabelAndTextField(jPanels[be + 2], PASSWORD_TEXTS[2]);
        JButton confirmButton = jFrame.addConfirmAndCancelButton(jPanels[be + 3], MyFrame.CONFIRM_CANCEL_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1, 0, 0));
        // 设置指定位置
        jFrame.setLocation(950, 500);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 关闭时，清空数据
                oldPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            }
        });

        confirmButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            System.out.printf("{ msg:personGui/newUpdatePasswordGui/confirm, nowUser:%s, oldPassword:%s, newPassword:%s ,confirmPassword:%s }\n",
                    user, oldPassword, newPassword, confirmPassword);
            if ( !oldPassword.equals( user.getPassword() ) ) {
                jFrame.showTip("原密码错误");
            } else if ( !AbstractUser.checkPassword(newPassword) ) {
                jFrame.showTip("新密码格式错误");
            } else if ( !confirmPassword.equals(newPassword) ) {
                jFrame.showTip("两次密码不匹配");
            }else {
                if (DataProcessing.updateUser(user.getName(), newPassword, user.getRole())) {
                    user.SetPassword(newPassword);
                    // 查找到表格中的数据，进行修改，并且重置用户
                    int row = MyFrame.searchAndScrollToUser(UserGui.jTable, user.getName());
                    UserGui.jTable.removeRowSelectionInterval(row, row);
                    UserGui.jTable.setValueAt(user.getPassword(), row, 1);
                    jFrame.showTip("修改成功,请重新登录");
                    user = null;
                    jFrame.dispose();
                } else {
                    jFrame.showWarn("修改失败");
                }
            }
        });
        return jFrame;
    }
}
