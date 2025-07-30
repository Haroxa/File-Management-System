package console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;

/**
 * TODO 抽象用户类，为各用户子类提供模板
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public abstract class AbstractUser {
    static final int NAME_MIN_LEN = 2, NAME_MAX_LEN = 10;
    static final int PWD_MIN_LEN = 2, PWD_MAX_LEN = 10;
    static final int ROLE_MIN = 0, ROLE_MAX = 2;
    public static String uploadPath = "\\_upload";
    static String downloadPath = "\\_download";

    static {
        String currentFilePath = new File("").getAbsolutePath();
        uploadPath = currentFilePath + uploadPath;
        downloadPath = currentFilePath + downloadPath;
    }

    private String name, password, role;

    AbstractUser(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static AbstractUser newUser(ResultSet resultSet)throws Exception{
        String name = resultSet.getString("username");
        String password = resultSet.getString("password");
        String role = resultSet.getString("role");
        return switch (DataProcessing.Role.valueOf(role.toUpperCase())) {
            case ADMINISTRATOR -> new Administrator(name, password);
            case OPERATOR -> new Operator(name, password);
            case BROWSER -> new Browser(name, password);
        };
    }
    public static AbstractUser newUser(String name, String password, String role)throws IllegalArgumentException{
        return switch (DataProcessing.Role.valueOf(role.toUpperCase())) {
            case ADMINISTRATOR -> new Administrator(name, password);
            case OPERATOR -> new Operator(name, password);
            case BROWSER -> new Browser(name, password);
        };
    }

    public void SetName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void SetPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void SetRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    /**
     * TODO 展示菜单，需子类重载
     *
     * @param
     * @return void
     * @throws
     */
    public abstract void showMenu();

    /**
     * TODO 修改用户信息
     *
     * @param password 密码
     * @return boolean
     * @throws
     */
    public boolean changeSelfInfo(String password) {
        if (DataProcessing.updateUser(name, password, role)) {
            this.password = password;
            return true;
        }
        return false;
    }

    /**
     * TODO 复制文件内容
     *
     * @param source 源文件
     * @param target 目标文件
     * @return boolean
     * @throws
     */
    public static boolean copyFile(String source,String target){
        try {
            File sourceFile = new File( source );
            File targetFile = new File( target );
            // 源文件不存在
            if (!sourceFile.exists()){
                return false;
            }
            // 目标文件不存在
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            FileInputStream in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * TODO 展示档案文件列表
     *
     * @param
     * @return void
     * @throws
     */
    public void showFileList() {
        Enumeration<Doc> e = DataProcessing.listDoc();
        if (e == null) {
            System.out.println("文件列表获取失败");
        } else if (!e.hasMoreElements()) {
            System.out.println("文件列表为空");
        } else {
            Doc.listShowColumns();
            while (e.hasMoreElements()) {
                Doc d = e.nextElement();
                d.listShow();
            }
        }
    }

    /**
     * TODO 退出系统
     *
     * @param
     * @return void
     * @throws
     */
    public static void exitSystem() {
        System.out.println("系统退出，谢谢使用！");
        System.exit(0);
    }


    /**
     * TODO 输入用户名与密码，经过格式检验后，进行用户登录
     *
     * @param
     * @return console.AbstractUser
     * @throws
     */
    public static AbstractUser login() {
        Scanner in = new Scanner(System.in);
        String name = inputName(in);
        String password = inputPassword(in, "");
        return DataProcessing.verifyUser(name, password);
    }

    /**
     * TODO 输入新旧密码，进行验证修改
     *
     * @param
     * @return void
     * @throws
     */
    public void changePassword() {
        Scanner in = new Scanner(System.in);
        String oldPassword = inputPassword(in, "原");
        String password = inputPassword(in, "新");
        if (!oldPassword.equals(this.getPassword()) || password.isEmpty()) {
            System.out.println("密码错误或输入格式错误");
            return;
        }
        if (changeSelfInfo(password)) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }
    }

    /**
     * TODO 输入文档编号，查找后进行下载
     *
     * @param
     * @return void
     * @throws
     */
    public void fileDownload(){
        Scanner in = new Scanner(System.in);
        String id = Doc.inputId(in);
        Doc doc= DataProcessing.searchDoc(id);
        if (doc==null){
            System.out.println("文档不存在");
        }else if( copyFile( uploadPath + "%s-%s".formatted(id,doc.getFilename() ), downloadPath+doc.getFilename() ) ){
            System.out.println("下载成功");
        }else{
            System.out.println("下载失败");
        }

    }

    /**
     * TODO 检查用户名格式
     *
     * @param name 用户名
     * @return boolean
     * @throws
     */
    public static boolean checkName(String name) {
        return DataProcessing.checkStrPatternAndLen(name, "[a-zA-Z0-9_\\-@]+", NAME_MIN_LEN, NAME_MAX_LEN);
    }

    /**
     * TODO 检查密码格式
     *
     * @param password 密码
     * @return boolean
     * @throws
     */
    public static boolean checkPassword(String password) {
        return DataProcessing.checkStrPatternAndLen(password, "[a-zA-Z0-9_\\-@]+", PWD_MIN_LEN, PWD_MAX_LEN);
    }

    /**
     * TODO 输入姓名，满足指定字符和长度
     *
     * @param in 输入流
     * @return java.lang.String
     * @throws
     */
    public static String inputName(Scanner in) {
        System.out.print("请输入用户名：");
        return DataProcessing.inputStr(in, AbstractUser::checkName);
    }

    /**
     * TODO 输入密码，满足指定字符和长度
     *
     * @param in  输入流
     * @param msg 提示信息
     * @return java.lang.String
     * @throws
     */
    public static String inputPassword(Scanner in, String msg) {
        System.out.printf("请输入%s密码：", msg);
        return DataProcessing.inputStr(in, AbstractUser::checkPassword);
    }

    /**
     * TODO 输入角色对应数字，判断范围后转换为角色字符串
     *
     * @param in
     * @return java.lang.String
     * @throws
     */
    public static String inputRole(Scanner in) {
        System.out.print("请选择角色：\n0-Browser\t1-Operator\t2-Administrator\n请输入：");
        int option = DataProcessing.inputInt(in);
        return DataProcessing.rangeInt(option, ROLE_MIN, ROLE_MAX) ? DataProcessing.Role.values()[option].getName() : "";
    }

    /**
     * TODO 列表展示时，显示格式化列名
     *
     * @param
     * @return void
     * @throws
     */
    public static void listShowColumns() {
        System.out.printf("%10s %10s %20s\n", "Name", "Password", "Role");
    }

    /**
     * TODO 列表展示时，显示格式化信息
     *
     * @param
     * @return void
     * @throws
     */
    public void listShow() {
        System.out.printf("%10s %10s %20s\n", name, password, role);
    }

    public Vector<String> toVector() {
        return new Vector<>(Arrays.asList(
                name, password, role
        ));
    }

    @Override
    public String toString() {
        return "{ name:%s ,password:%s ,role:%s }".formatted(name,password,role);
    }
}
