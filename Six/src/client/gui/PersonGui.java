package client.gui;

import share.console.AbstractUser;
import client.DealClient;

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

    public static void main(String[] args) {

    }


    /**
     * TODO 创建personManage主窗口
     *
     * @param
     * @return MyFrame
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

        MyFrame updatePasswordGui = newUpdatePasswordGui();
        updatePasswordGui.parentGui = jFrame;

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.printf("{ gui-msg:userGui/newPersonManageGui/activated, nowUser:%s }\n", jFrame.user);
                // 激活窗口时，检查当前用户合法性， 不合法，返回主界面
                if ( !jFrame.checkUserValid() ) {
                    jFrame.showParentGui();
                    return;
                }
                // 显示用户信息
                nameField.setText(jFrame.user.getName());
                passwordField.setText(jFrame.user.getPassword());
                comboBox.setSelectedItem(jFrame.user.getRole());
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("{ gui-msg:userGui/newPersonManageGui/closed, nowUser:%s }\n", jFrame.user);
                // 关闭窗口时，检查子窗口是否已关闭
                jFrame.closeCurrentGui();
            }
        });
        // 设置修改密码窗口
        updatePasswordButton.addActionListener(e -> {
            jFrame.setCurrentGui(updatePasswordGui);
        });
        return jFrame;
    }

    /**
     * TODO 创建updatePassword子窗口
     *
     * @param
     * @return MyFrame
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
            System.out.printf("{ gui-msg:personGui/newUpdatePasswordGui/confirm, nowUser:%s, oldPassword:%s, newPassword:%s ,confirmPassword:%s }\n",
                    jFrame.user, oldPassword, newPassword, confirmPassword);
            if ( !oldPassword.equals( jFrame.user.getPassword() ) ) {
                jFrame.showTip("原密码错误");
            } else if ( !AbstractUser.checkPassword(newPassword) ) {
                jFrame.showTip("新密码格式错误");
            } else if ( !confirmPassword.equals(newPassword) ) {
                jFrame.showTip("两次密码不匹配");
            }else {
                if (DealClient.updateUser(jFrame.user.getName(), newPassword, jFrame.user.getRole())) {
                    jFrame.user.SetPassword(newPassword);
//                    // 查找到表格中的数据，进行修改，并且重置用户
//                    int row = MyFrame.searchAndScrollToUser(UserGui.jTable, jFrame.user.getName());
//                    UserGui.jTable.removeRowSelectionInterval(row, row);
//                    UserGui.jTable.setValueAt(jFrame.user.getPassword(), row, 1);
                    jFrame.showTip("修改成功,请重新登录");
                    jFrame.user = null;
                    jFrame.showParentGui();
                } else {
                    jFrame.showWarn("修改失败");
                }
            }
        });
        return jFrame;
    }
}
