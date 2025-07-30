package client.gui;

import client.DealClient;
import share.console.AbstractUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Vector;

/**
 * TODO 用户管理界面
 *
 * @author Haroxa
 * @date 2023/11/20
 */
public class UserGui {
    static final String[] TEXTS = {
            "查    询", "新    增", "修    改", "删    除"
    };
    static final Vector<String> COLUMN_DATA = new Vector<>(Arrays.asList(
            "用户名", "密    码", "角    色"
    ));

    public static void main(String[] args) {

    }

    /**
     * TODO 创建userManage主窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newUserManageGui() {
        MyFrame jFrame = new MyFrame("用户管理", 400, 550);
        int be = -1, panelNum = 1;
        // 获取所有用户数据，以此创建表格
        Vector<Vector<String>> rowsData = DealClient.listUserToVector();
        DefaultTableModel tableModel = MyFrame.getNonEditableTableModel(rowsData, COLUMN_DATA);
        JTable jTable = new JTable(tableModel);
        // 设置表格在滑动窗口中的首选大小
        Dimension tableSize = new Dimension(400, 400);
        jTable.setPreferredScrollableViewportSize(tableSize);
        JScrollPane scrollPane = new JScrollPane(jTable);
        jFrame.add(scrollPane);

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JButton queryButton = MyFrame.addButton(jPanels[be + 1], TEXTS[0]);
        JButton insertButton = MyFrame.addButton(jPanels[be + 1], TEXTS[1]);
        JButton updateButton = MyFrame.addButton(jPanels[be + 1], TEXTS[2]);
        JButton deleteButton = MyFrame.addButton(jPanels[be + 1], TEXTS[3]);

        jFrame.addJPanels(jPanels);
        // 设置 盒式布局（垂直方向）
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        // 设置指定位置
        jFrame.setLocation(950, 120);


        MyFrame queryUserGui = newQueryUserGui(jTable);
        MyFrame insertUserGui = newInsertUserGui();
        MyFrame updateUserGui = newUpdateUserGui(jTable);
        MyFrame deleteUserGui = newDeleteUserGui(jTable);
        queryUserGui.parentGui = insertUserGui.parentGui = updateUserGui.parentGui = deleteUserGui.parentGui = jFrame;

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.printf("{ gui-msg:userGui/newUserManageGui/activated, nowUser:%s }\n", jFrame.user);
                // 激活窗口时，检查当前用户合法性， 不合法，返回主界面
                if ( !jFrame.checkUserValid() ) {
                    jFrame.showParentGui();
                }
                Vector<Vector<String>> rowsData = DealClient.listUserToVector();
                tableModel.setDataVector(rowsData,COLUMN_DATA);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("{ gui-msg:userGui/newUserManageGui/closed, nowUser:%s }\n", jFrame.user);
                // 关闭窗口时，检查子窗口是否已关闭
                jFrame.closeCurrentGui();
            }
        });
        // 设置各个子窗口
        queryButton.addActionListener(e -> jFrame.setCurrentGui(queryUserGui));
        insertButton.addActionListener(e -> jFrame.setCurrentGui(insertUserGui));
        updateButton.addActionListener(e -> jFrame.setCurrentGui(updateUserGui));
        deleteButton.addActionListener(e -> jFrame.setCurrentGui(deleteUserGui));
        return jFrame;
    }

    /**
     * TODO 创建queryUser子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newQueryUserGui(JTable jTable) {
        MyFrame jFrame = new MyFrame("查询用户", 300, 200);
        int be = 1, panelNum = 3;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JTextField nameField = MyFrame.addNameLabelAndTextField(jPanels[be], MyFrame.USER_TEXTS[0]);
        JButton confirmButton = jFrame.addConfirmAndCancelButton(jPanels[be + 1], MyFrame.CONFIRM_CANCEL_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1));

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 关闭时，清空数据
                nameField.setText("");
            }
        });
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            AbstractUser u = DealClient.searchUser(name);
            System.out.printf("{ gui-msg:userGui/newQueryUserGui/confirmButton, nowUser:%s, name:%s, searchUser:%s }\n", jFrame.user, name, u);
            if (u != null && MyFrame.searchAndScrollToUser(jTable, name) != -1) {
                jFrame.showTip("查询成功");
                jFrame.dispose();
            } else {
                jFrame.showTip("用户名错误或用户名不存在");
            }
        });
        return jFrame;
    }

    /**
     * TODO 创建insertUser子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newInsertUserGui() {
        MyFrame jFrame = new MyFrame("新增用户", 300, 250);
        int be = 1, panelNum = 4;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JTextField nameField = MyFrame.addNameLabelAndTextField(jPanels[be], MyFrame.USER_TEXTS[0]);
        JComboBox<String> comboBox = MyFrame.addRoleLabelAndComboBox(jPanels[be + 1], MyFrame.ROLE_OPTIONS);
        JButton confirmButton = jFrame.addConfirmAndCancelButton(jPanels[be + 2], MyFrame.CONFIRM_CANCEL_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1));

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 关闭时，清空数据
                nameField.setText("");
                comboBox.setSelectedItem(MyFrame.ROLE_OPTIONS[0]);
            }
        });
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            String role = (String) comboBox.getSelectedItem();
            System.out.printf("{ gui-msg:userGui/newInsertUserGui/confirmButton, nowUser:%s, name:%s, role:%s }\n", jFrame.user, name, role);
            if (!AbstractUser.checkName(name)) {
                jFrame.showTip("用户名格式错误");
            } else if (!DealClient.insertUser(name, DealClient.getDefaultPassword(), role)) {
                jFrame.showTip("用户名已存在");
            } else {
                AbstractUser u = DealClient.searchUser(name);
                if (u != null) {
                    jFrame.showTip("添加成功");
                    // 在表格中同步添加数据
                    // tableModel.insertRow(tableModel.getRowCount(), u.toVector());
                    jFrame.dispose();
                } else {
                    jFrame.showWarn("查询失败");
                }
            }
        });
        return jFrame;
    }

    /**
     * TODO 创建updateUser子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newUpdateUserGui(JTable jTable) {
        MyFrame jFrame = new MyFrame("修改用户", 300, 250);
        int be = 1, panelNum = 5;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JTextField nameField = MyFrame.addNameLabelAndTextField(jPanels[be], MyFrame.USER_TEXTS[0]);
        // 设置无法修改用户名
        nameField.setEnabled(false);
        JButton passwordButton = MyFrame.addPasswordLabelAndButton(jPanels[be + 1]);
        JComboBox<String> comboBox = MyFrame.addRoleLabelAndComboBox(jPanels[be + 2], MyFrame.ROLE_OPTIONS);
        JButton confirmButton = jFrame.addConfirmAndCancelButton(jPanels[be + 3], MyFrame.CONFIRM_CANCEL_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1, 0, 0));

        final String[] rowData = new String[MyFrame.USER_TEXTS.length];

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                int updateRowStore = jTable.getSelectedRow();
                System.out.printf("{ gui-msg:userGui/newUpdateUserGui/activated, nowUser:%s, updateRowStore:%d }\n", jFrame.user, updateRowStore);
                if (updateRowStore == -1) {
                    jFrame.showTip("请先选中指定行，再进行操作");
                    jFrame.dispose();
                    return;
                }
                // 获取到指定行数据，并填充到对应位置
                for (int col = 0; col < jTable.getColumnCount(); col++) {
                    rowData[col] = (String) jTable.getValueAt(updateRowStore, col);
                }
                // 无法修改super信息
                if (!jFrame.user.getName().equals("super") && rowData[0].equals("super")){
                    jFrame.showTip("没有权限修改super信息");
                    jFrame.dispose();
                    return;
                }
                nameField.setText(rowData[0]);
                comboBox.setSelectedItem(rowData[2]);
                System.out.printf("{ gui-msg:userGui/newUpdateUserGui/activated, nowUser:%s, row:%d, rowData:%s }\n", jFrame.user, updateRowStore, Arrays.toString(rowData));
                // 设置无法修改自己的角色
                comboBox.setEnabled(!rowData[0].equals(jFrame.user.getName()));
            }
        });

        passwordButton.addActionListener(e -> {
            String name = nameField.getText();
            int option = jFrame.showConfirm("是否确认重置用户 %s 的密码？".formatted(name));
            if (option == JOptionPane.OK_OPTION) {
                rowData[1] = DealClient.getDefaultPassword();
                confirmButton.doClick();
            }
        });
        comboBox.addActionListener(e -> rowData[2] = (String) comboBox.getSelectedItem());

        confirmButton.addActionListener(e -> {
            String name = rowData[0];
            String password = rowData[1];
            String role = rowData[2];
            System.out.printf("{ gui-msg:userGui/newUpdateUserGui/confirmButton, nowUser:%s, rowData:%s }\n", jFrame.user, Arrays.toString(rowData));
            if (DealClient.updateUser(name, password, role)) {
                // 更新表格数据
//                int row = jTable.getSelectedRow();
//                for (int col = 0; col < jTable.getColumnCount(); col++) {
//                    jTable.setValueAt(rowData[col], row, col);
//                }

                // 修改当前用户密码时，需要重新登录
                if (name.equals(jFrame.user.getName()) && !password.equals(jFrame.user.getPassword())) {
                    jFrame.showTip("当前密码已修改，请重新登录");
                    jFrame.user = null;
                    jFrame.showParentGui();
                }else{
                    jFrame.showTip("修改成功");
                    jFrame.dispose();
                }
            } else {
                jFrame.showWarn("修改失败");
            }
        });
        return jFrame;
    }

    /**
     * TODO 创建deleteUser子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newDeleteUserGui(JTable jTable) {
        MyFrame jFrame = new MyFrame("删除用户");
        // 设置窗口为无边框，完全透明
        jFrame.setUndecorated(true);
        jFrame.setOpacity(0);
        final int[] row = {-1};
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // 防止在调用确认框之后进行重复调用
                if (row[0] != -1) {
                    return;
                }
                row[0] = jTable.getSelectedRow();
                System.out.printf("{ gui-msg:userGui/newDeleteUserGui/activated, nowUser:%s, row:%s }\n", jFrame.user, Arrays.toString(row));
                if (row[0] == -1) {
                    jFrame.showTip("请先选中指定行，再进行操作");
                    jFrame.dispose();
                    return;
                }
                String name = (String) jTable.getValueAt(row[0], 0);
                System.out.printf("{ gui-msg:userGui/newDeleteUserGui/activated/getValue, name:%s }\n", name);
                if (name.equals("super")) {
                    jFrame.showTip("无法删除超级管理员");
                    row[0] = -1;
                    jFrame.dispose();
                    return;
                }
                int option = jFrame.showConfirm("是否确认%s用户？".formatted(name.equals(jFrame.user.getName()) ? "注销当前" : "删除该" ));
                System.out.printf("{ gui-msg:userGui/newDeleteUserGui/activated/confirm, option:%d }\n", option);
                if (option == JOptionPane.OK_OPTION) {
                    if (DealClient.deleteUser(name)) {
                        // 更新表格数据
//                        tableModel.removeRow(row[0]);
                        // 判断是否为当前用户
                        if (name.equals(jFrame.user.getName())) {
                            jFrame.user = null;
                            jFrame.showTip("注销成功");
                        }else{
                            jFrame.showTip("删除成功");
                        }
                    } else {
                        jFrame.showWarn("删除失败");
                    }
                }
                row[0] = -1;
                jFrame.dispose();
            }
        });
        return jFrame;
    }
}
