package client.gui;

import client.DealClient;
import share.console.AbstractUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

/**
 * TODO 自定义框架
 *
 * @author Haroxa
 * @date 2023/11/16
 */
public class MyFrame extends JFrame {
    static final int WIDTH = 200;
    static final int HEIGHT = 220;
    static final String[] USER_TEXTS = {"用户名: ", "密    码: ", "角    色: "};
    static final String[] DOC_TEXTS = {"档案编号: ", "档案描述: ", "档        案: ", "打开"};
    static final String[] ROLE_OPTIONS = {"Browser", "Operator", "Administrator"};
    static final String[] CONFIRM_CANCEL_TEXTS = {"确    认", "取    消"};
    static final Dimension BUTTON_DIMENSION = new Dimension(97, 25);
    static final Dimension TEXTFIELD_DIMENSION = new Dimension(147, 20);
    AbstractUser user;
    MyFrame currentGui, parentGui;
    boolean tableFlag = false;
    MyFrame(String title) {
        super(title);
        initFrame(WIDTH, HEIGHT);
    }

    MyFrame(String title, int width, int height) {
        super(title);
        initFrame(width, height);
    }

    /**
     * TODO 初始化设置
     *
     * @param width  宽度
     * @param height 高度
     * @return void
     * @throws
     */
    void initFrame(int width, int height) {
        // 设置窗体大小
        setSize(width, height);
        // 设置窗体关闭方式，关闭当前窗口 等价于调用 this.dispose()
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 设置窗体置顶
        setAlwaysOnTop(true);
        // 设置窗体居中
        setLocationRelativeTo(null);
        // 取消默认布局
        setLayout(null);
    }

    public void setParentGui(MyFrame myFrame){
        parentGui = myFrame;
        user = parentGui.user;
    }
    public void showParentGui(){
        dispose();
        parentGui.user = user;
        parentGui.setVisible(true);
    }

    /**
     * TODO 设置当前打开的子窗口
     *
     * @param gui 指定窗口
     * @return void
     * @throws
     */
    public void setCurrentGui(MyFrame gui) {
        // 直接设置为空
        if (gui == null) {
            currentGui = null;
            return;
        }
        gui.user = user;
        // 如果当前窗口为空或未显示，则设置新的窗口
        if (currentGui == null || !currentGui.isVisible()) {
            currentGui = gui;
        }
        // 检查是否最小化，恢复至正常大小
        if (checkMinimized( currentGui )) {
            currentGui.setExtendedState(JFrame.NORMAL);
        }
        currentGui.setVisible(true);
    }

    /**
     * TODO 关闭当前子窗口
     *
     * @param
     * @return void
     * @throws
     */
    public void closeCurrentGui() {
        if (currentGui != null && currentGui.isVisible()) {
            currentGui.dispose();
        }
    }

    /**
     * TODO 创建指定个数的JPanel数组
     *
     * @param panelNum 指定个数
     * @return javax.swing.JPanel[]
     * @throws
     */
    public static JPanel[] getJPanels(int panelNum) {
        JPanel[] jPanels = new JPanel[panelNum];
        for (int i = 0; i < panelNum; i++) {
            jPanels[i] = new JPanel();
        }
        return jPanels;
    }

    /**
     * TODO 创建指定个数的JPanel数组，并设置 左对齐 流式布局 和指定宽度的 空白占位格
     *
     * @param panelNum 指定个数
     * @param width    指定宽度
     * @return javax.swing.JPanel[]
     * @throws
     */
    public static JPanel[] getJPanels(int panelNum, int width) {
        JPanel[] jPanels = new JPanel[panelNum];
        for (int i = 0; i < panelNum; i++) {
            jPanels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jPanels[i].add(Box.createHorizontalStrut(width));
        }
        return jPanels;
    }

    /**
     * TODO 创建无法编辑的表格
     *
     * @param rowsData   所有行数据
     * @param columnData 列名数据
     * @return javax.swing.table.DefaultTableModel
     * @throws
     */
    public static <T, P> DefaultTableModel getNonEditableTableModel(Vector<Vector<T>> rowsData, Vector<P> columnData) {
        return new DefaultTableModel(rowsData, columnData) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * TODO 找到表格中的指定数据，滑动至相应位置并突出显示，找到返回行数，找不到返回-1
     *
     * @param jTable     指定表格
     * @param searchTerm 指定数据
     * @return int
     * @throws
     */
    public static int searchAndScrollToUser(JTable jTable, String searchTerm) {
        for (int row = 0, col = 0; row < jTable.getRowCount(); row++) {
            Object cellValue = jTable.getValueAt(row, col);
            if (cellValue.toString().equals(searchTerm)) {
                // 滑动到指定位置
                Rectangle cellRect = jTable.getCellRect(row, col, true);
                jTable.scrollRectToVisible(cellRect);
                // 先去除已有突出显示，再突出显示指定行
                jTable.removeRowSelectionInterval(0, jTable.getRowCount() - 1);
                jTable.addRowSelectionInterval(row, row);
                return row;
            }
        }
        return -1;
    }

    /**
     * TODO 检查窗口是否最小化
     *
     * @param
     * @return boolean
     * @throws
     */
    public static boolean checkMinimized(MyFrame gui) {
        if (gui == null) {
            return false;
        }
        // extendedState 表示窗口的扩展状态，可以包含多个状态，需要通过位运算进行判断
        int extendedState = gui.getExtendedState();
        return (extendedState & ICONIFIED) != 0;
    }

    /**
     * TODO 检查用户合法性
     *
     * @param
     * @return boolean
     * @throws
     */
    public boolean checkUserValid() {
        if (user == null || DealClient.searchUser(user.getName()) == null) {
            dispose();
            return false;
        }
        return true;
    }

    /**
     * TODO 创建带有指定信息的JButton，添加到指定JPanel中，并返回JButton
     *
     * @param jPanel 指定容器
     * @param text   指定信息
     * @return javax.swing.JButton
     * @throws
     */
    public static JButton addButton(JPanel jPanel, String text) {
        JButton button = new JButton(text);
        jPanel.add(button);
        return button;
    }

    /**
     * TODO 创建带有指定信息的JLabel和textField，添加到指定JPanel中，并返回textField
     *
     * @param jPanel 指定容器
     * @param text   指定信息
     * @return javax.swing.JTextField
     * @throws
     */
    public static JTextField addNameLabelAndTextField(JPanel jPanel, String text) {
        JLabel nameLabel = new JLabel(text);
        JTextField nameField = new JTextField(13);
        // 设置首选大小，？？？ 使用 set 无效 ，但使用 get 却可以
//        nameField.setPreferredSize(TEXTFIELD_DIMENSION);
        nameField.getPreferredSize();
        jPanel.add(nameLabel);
        jPanel.add(nameField);
        return nameField;
    }

    /**
     * TODO 创建带有指定信息的JLabel和passwordField，添加到指定JPanel中，并返回passwordField
     *
     * @param jPanel 指定容器
     * @param text   指定信息
     * @return javax.swing.JPasswordField
     * @throws
     */
    public static JPasswordField addPasswordLabelAndTextField(JPanel jPanel, String text) {
        JLabel passwordLabel = new JLabel(text);
        JPasswordField passwordField = new JPasswordField(13);
//        passwordField.setPreferredSize(TEXTFIELD_DIMENSION);
        passwordField.getPreferredSize();
        jPanel.add(passwordLabel);
        jPanel.add(passwordField);
        return passwordField;
    }

    public static JButton addPasswordLabelAndButton(JPanel jPanel) {
        JLabel passwordLabel = new JLabel(USER_TEXTS[1]);
        JButton passwordButton = new JButton("重置密码");
        passwordButton.setPreferredSize(TEXTFIELD_DIMENSION);
        jPanel.add(passwordLabel);
        jPanel.add(passwordButton);
        return passwordButton;
    }

    /**
     * TODO 创建带有指定信息的JLabel和JComboBox，添加到指定JPanel中，并返回JComboBox
     *
     * @param jPanel  指定容器
     * @param options 指定选项
     * @return javax.swing.JComboBox<java.lang.String>
     * @throws
     */
    public static JComboBox<String> addRoleLabelAndComboBox(JPanel jPanel, String[] options) {
        JLabel roleLabel = new JLabel(USER_TEXTS[2]);
        JComboBox<String> comboBox = new JComboBox<>(options);
        // 设置默认选项
        comboBox.setSelectedItem(options[0]);
        comboBox.setPreferredSize(TEXTFIELD_DIMENSION);
        jPanel.add(roleLabel);
        jPanel.add(comboBox);
        return comboBox;
    }

    /**
     * TODO 创建带有指定信息的确认与取消JButton，添加到指定JPanel中，并返回确认JButton
     *
     * @param jPanel 指定容器
     * @param texts  指定信息
     * @return javax.swing.JButton
     * @throws
     */
    public JButton addConfirmAndCancelButton(JPanel jPanel, String[] texts) {
        JButton confirmButton = new JButton(texts[0]);
        confirmButton.setPreferredSize(BUTTON_DIMENSION);
        JButton cancelButton = new JButton(texts[1]);
        cancelButton.setPreferredSize(BUTTON_DIMENSION);
        jPanel.add(confirmButton);
        jPanel.add(cancelButton);
        // 设置 取消JButton 的监听，如果被按下，则关闭窗口
        cancelButton.addActionListener(e -> {
            dispose();
        });
        return confirmButton;
    }

    /**
     * TODO 将指定JPanel数组添加至窗口中
     *
     * @param jPanels 指定容器
     * @return void
     * @throws
     */
    public void addJPanels(JPanel[] jPanels) {
        for (JPanel jPanel : jPanels) {
            add(jPanel);
        }
    }

    /**
     * TODO 显示带有指定信息的确认与否提示框
     *
     * @param msg 指定信息
     * @return int
     * @throws
     */
    public int showConfirm(String msg) {
        return JOptionPane.showConfirmDialog(MyFrame.this, msg, "提示", JOptionPane.OK_CANCEL_OPTION);
    }

    /**
     * TODO 显示带有指定信息的提示框
     *
     * @param msg 指定信息
     * @return void
     * @throws
     */
    public void showTip(String msg) {
        JOptionPane.showMessageDialog(MyFrame.this, msg, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * TODO 显示带有指定信息的警告框
     *
     * @param msg 指定信息
     * @return void
     * @throws
     */
    public void showWarn(String msg) {
        JOptionPane.showMessageDialog(MyFrame.this, msg, "警告", JOptionPane.WARNING_MESSAGE);
    }
}
