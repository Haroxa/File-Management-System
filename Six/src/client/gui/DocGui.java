package client.gui;

import client.DealClient;
import share.console.Doc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Vector;

/**
 * TODO 档案管理界面
 *
 * @author Haroxa
 * @date 2023/11/20
 */
public class DocGui {
    static final String[] TEXTS = {
            "查    询", "上    传", "下    载"
    };
    static final Vector<String> COLUMN_DATA = new Vector<>(Arrays.asList(
            "档案号", "创建者", "时    间", "文件名", "描    述"
    ));
    static final Dimension BUTTON_DIMENSION = new Dimension(150, 25);


    public static void main(String[] args) {

    }

    /**
     * TODO 创建docManage主窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newDocManageGui() {
        MyFrame jFrame = new MyFrame("档案管理", 550, 400);
        int be = 0, panelNum = 3;
        // 获取所有档案数据，以此创建表格
        Vector<Vector<String>> rowsData = DealClient.listDocToVector();
        DefaultTableModel tableModel = MyFrame.getNonEditableTableModel(rowsData, COLUMN_DATA);
        JTable jTable = new JTable(tableModel);
        // 设置表格在滚动窗口中的首选大小
        Dimension tableSize = new Dimension(550, 260);
        jTable.setPreferredScrollableViewportSize(tableSize);
        JScrollPane scrollPane = new JScrollPane(jTable);
        jFrame.add(scrollPane);

        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JButton queryButton = MyFrame.addButton(jPanels[be + 1], TEXTS[0]);
        queryButton.setPreferredSize(BUTTON_DIMENSION);
        JButton uploadButton = MyFrame.addButton(jPanels[be + 1], TEXTS[1]);
        uploadButton.setPreferredSize(BUTTON_DIMENSION);
        JButton downloadButton = MyFrame.addButton(jPanels[be + 1], TEXTS[2]);
        downloadButton.setPreferredSize(BUTTON_DIMENSION);

        jFrame.addJPanels(jPanels);
        // 设置 垂直 盒式布局
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        // 设置指定位置
        jFrame.setLocation(950, 200);

        MyFrame queryDocGui = newQueryDocGui(jTable);
        MyFrame uploadDocGui = newUploadDocGui(tableModel);
        MyFrame downloadDocGui = newDownloadDocGui(jTable);
        queryDocGui.parentGui = uploadDocGui.parentGui = downloadDocGui.parentGui = jFrame;

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.printf("{ gui-msg:docGui/newDocManageGui/activated, nowUser:%s }\n", jFrame.user);
                // 激活窗口时，检查当前用户合法性， 不合法，返回主界面
                if (!jFrame.checkUserValid()) {
                    jFrame.showParentGui();
                    return;
                }
                // 设置非录入员无法上传文件
                uploadButton.setEnabled(jFrame.user.getRole().equals(DealClient.Role.OPERATOR.getName()));
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("{ gui-msg:docGui/newDocManageGui/closed, nowUser:%s }\n", jFrame.user);
                // 关闭窗口时，检查子窗口是否已关闭
                jFrame.closeCurrentGui();
            }
        });
        // 设置各个子窗口
        queryButton.addActionListener(e -> jFrame.setCurrentGui(queryDocGui));
        uploadButton.addActionListener(e -> jFrame.setCurrentGui(uploadDocGui));
        downloadButton.addActionListener(e -> jFrame.setCurrentGui(downloadDocGui));
        return jFrame;
    }


    /**
     * TODO 创建queryDoc子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newQueryDocGui(JTable jTable) {
        MyFrame jFrame = new MyFrame("查询档案", 300, 200);
        int be = 1, panelNum = 3;
        JPanel[] jPanels = MyFrame.getJPanels(panelNum);

        JTextField idField = MyFrame.addNameLabelAndTextField(jPanels[be], MyFrame.DOC_TEXTS[0]);
        JButton confirmButton = jFrame.addConfirmAndCancelButton(jPanels[be + 1], MyFrame.CONFIRM_CANCEL_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 1, 0, 0));

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 关闭时，清空数据
                idField.setText("");
            }
        });
        confirmButton.addActionListener(e -> {
            String id = idField.getText();
            Doc doc = DealClient.searchDoc(id);
            System.out.printf("{ gui-msg:docGui/newQueryDocGui/confirmButton, nowUser:%s, id:%s, searchDoc:%s }\n", jFrame.user, id, doc);
            if (doc != null && MyFrame.searchAndScrollToUser(jTable, id) != -1) {
                jFrame.showTip("查询成功");
                jFrame.dispose();
            } else {
                jFrame.showTip("档案编号错误或不存在");
            }
        });
        return jFrame;
    }

    /**
     * TODO 创建uploadDoc子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newUploadDocGui(DefaultTableModel tableModel) {
        MyFrame jFrame = new MyFrame("上传档案", 400, 250);
        int be = 1, panelNum = 5;

        JPanel[] jPanels = MyFrame.getJPanels(panelNum, 70);

        JTextField idField = MyFrame.addNameLabelAndTextField(jPanels[be], MyFrame.DOC_TEXTS[0]);
        JTextField descriptionField = MyFrame.addNameLabelAndTextField(jPanels[be + 1], MyFrame.DOC_TEXTS[1]);
        JTextField pathField = MyFrame.addNameLabelAndTextField(jPanels[be + 2], MyFrame.DOC_TEXTS[2]);
        JButton pathButton = MyFrame.addButton(jPanels[be + 2], MyFrame.DOC_TEXTS[3]);
        JButton confirmButton = jFrame.addConfirmAndCancelButton(jPanels[be + 3], MyFrame.CONFIRM_CANCEL_TEXTS);

        jFrame.addJPanels(jPanels);
        // 设置 网格布局（行数，列数）
        jFrame.setLayout(new GridLayout(panelNum, 3, 0, 0));

        JFileChooser uploadChoose = new JFileChooser(".");
        uploadChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 关闭时，清空数据
                idField.setText("");
                descriptionField.setText("");
                pathField.setText("");
            }
        });
        pathButton.addActionListener(e -> {
            int result = uploadChoose.showOpenDialog(jFrame);
            File file = uploadChoose.getSelectedFile();
            // 判断选项与文件状态
            if (result == JFileChooser.APPROVE_OPTION && file != null) {
                pathField.setText(file.getPath());
            }
            System.out.printf("{ gui-msg:docGui/newUploadDocGui/pathButton, nowUser:%s, result:%d, file:%s }\n", jFrame.user, result, file);
        });
        confirmButton.addActionListener(e -> {
            String id = idField.getText();
            String description = descriptionField.getText();
            String path = pathField.getText();
            System.out.printf("{ gui-msg:docGui/newUploadDocGui/confirmButton, nowUser:%s, id:%s, description:%s, path:%s }\n", jFrame.user, id, description, path);
            File file = new File(path);
//            System.out.println(Doc.checkId(id));
            if (!Doc.checkId(id)) {
                jFrame.showTip("档案编号格式错误");
            } else if (!Doc.checkDescription(description)) {
                jFrame.showTip("档案描述格式错误");
            } else if (!file.exists()) {
                jFrame.showTip("档案路径错误或不存在");
            } else if (!DealClient.uploadDoc(file.getParent(), id, DealClient.getFilename(path))) {
                jFrame.showTip("档案上传失败");
            } else if (!DealClient.insertDoc(id, jFrame.user.getName(),
                    new Timestamp(System.currentTimeMillis()), description, file.getName())) {
                jFrame.showTip("档案编号已存在");
            } else {
                Doc doc = DealClient.searchDoc(id);
                if (doc != null) {
                    jFrame.showTip("上传成功");
                    tableModel.insertRow(tableModel.getRowCount(), doc.toVector());
                    jFrame.dispose();
                } else {
                    jFrame.showWarn("文件不存在或上传失败");
                }
            }
        });
        return jFrame;
    }

    /**
     * TODO 创建downloadDoc子窗口
     *
     * @param
     * @return MyFrame
     * @throws
     */
    public static MyFrame newDownloadDocGui(JTable jTable) {
        MyFrame jFrame = new MyFrame("下载档案");
        // 设置窗口为无边框，完全透明
        jFrame.setUndecorated(true);
        jFrame.setOpacity(0);
        final int[] row = {-1};

        JFileChooser downloadChoose = new JFileChooser(".");
        downloadChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // 防止在调用确认框之后进行重复调用
                if (row[0] != -1) {
                    return;
                }
                row[0] = jTable.getSelectedRow();
                System.out.printf("{ gui-msg:docGui/newDownloadDocGui/activated, nowUser:%s, row:%d }\n", jFrame.user, row[0]);
                if (row[0] == -1) {
                    jFrame.showTip("请先选中指定行，再进行操作");
                    jFrame.dispose();
                    return;
                }
                String id = (String) jTable.getValueAt(row[0], 0);
                String filename = (String) jTable.getValueAt(row[0], 3);
                System.out.printf("{ gui-msg:docGui/newDownloadDocGui/activated/getValue, id:%s, filename:%s }\n", id, filename);
                int option = jFrame.showConfirm("是否确认下载该文档？");
                System.out.printf("{ gui-msg:docGui/newDownloadDocGui/activated/confirm, option:%d }\n", option);
                if (option == JOptionPane.OK_OPTION) {
                    int result = downloadChoose.showOpenDialog(jFrame);
                    File file = downloadChoose.getSelectedFile();
                    if (result == JFileChooser.APPROVE_OPTION && file != null) {
                        if (DealClient.downloadDoc( file.getPath(),id,filename )) {
                            jFrame.showTip("下载成功");
                            jFrame.dispose();
                        } else {
                            jFrame.showWarn("文件不存在或下载失败");
                        }
                    }
                    System.out.printf("{ gui-msg:docGui/newDownloadDocGui/activated/confirm, result:%d, file:%s }\n", result, file);
                }
                row[0] = -1;
                jFrame.dispose();
            }
        });
        return jFrame;
    }

}
