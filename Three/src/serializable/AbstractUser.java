package serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * TODO 抽象用户类，为各用户子类提供模板
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public abstract class AbstractUser implements Base,Serializable {
    static final int NAME_MIN_LEN = 2,NAME_MAX_LEN = 10;
    static final int PWD_MIN_LEN = 2,PWD_MAX_LEN = 10;
    static final int ROLE_MIN = 0 , ROLE_MAX = 2;
    static String uploadPath = "\\_upload\\", downloadPath = "\\_download\\";
    static {
        String currentFilePath = new File("").getAbsolutePath();
        uploadPath = currentFilePath + uploadPath;
        downloadPath = currentFilePath + downloadPath;
    }
    private String name,password,role;
    AbstractUser(String name, String password, String role){
        this.name=name; this.password=password; this.role=role;
    }
    public void SetName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public void SetPassword(String password){
        this.password=password;
    }
    public String getPassword(){
        return password;
    }
    public void SetRole(String role){
        this.role=role;
    }
    public String getRole(){
        return role;
    }

    @Override
    public String getKey(){
        return name;
    }
    @Override
    public String toString() {
        return "{"+
                "name:" + name +
                ", password:" + password +
                ", role:" + role +
                '}';
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
    public boolean changeSelfInfo(String password){
        if (DataProcessing.updateUser(name,password,role)){
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
    public boolean copyFile(String source,String target){
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
    public void showFileList(){
        Enumeration<Doc> e= DataProcessing.listDoc();
        if(e==null){
            System.out.println("文件列表获取失败");
        }else if(!e.hasMoreElements()){
            System.out.println("文件列表为空");
        }else{
            Doc.listShowColumns();
            while(e.hasMoreElements()){
                Doc d=e.nextElement();
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
    public static void exitSystem(){
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
    public static AbstractUser login(){
        String name = inputName();
        String password = inputPassword("");
        return DataProcessing.verifyUser(name,password);
    }
    /**
     * TODO 输入新旧密码，进行验证修改
     *
     * @param
     * @return void
     * @throws
     */
    public void changePassword(){
        String oldPassword = inputPassword("原");
        String password = inputPassword("新");
        if (!oldPassword.equals(this.getPassword())|| password.isEmpty()){
            System.out.println("密码错误或输入格式错误");
            return;
        }
        if(changeSelfInfo(password)){
            System.out.println("修改成功");
        }else{
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
        String id = Doc.inputId();
        Doc doc = DataProcessing.searchDoc(id);
        if (doc==null){
            System.out.println("文档不存在");
        }else if( copyFile( uploadPath + "%s-%s".formatted(id,doc.getFilename() ), downloadPath+doc.getFilename() ) ){
            System.out.println("下载成功");
        }else{
            System.out.println("下载失败");
        }
    }
    public static boolean checkName(String name) {
        return DataProcessing.checkStrPatternAndLen(name, "[a-zA-Z0-9_\\-@]+", NAME_MIN_LEN, NAME_MAX_LEN);
    }

    public static boolean checkPassword(String password) {
        return DataProcessing.checkStrPatternAndLen(password, "[a-zA-Z0-9_\\-@]+", PWD_MIN_LEN, PWD_MAX_LEN);
    }
    /**
     * TODO 输入姓名，满足指定字符和长度
     *
     * @param
     * @return java.lang.String
     * @throws
     */
    public static String inputName(){
        System.out.print("请输入用户名：");
        return DataProcessing.inputStr(AbstractUser::checkName);
    }
    /**
     * TODO 输入密码，满足指定字符和长度
     *
     * @param msg 提示信息
     * @return java.lang.String
     * @throws
     */
    public static String inputPassword(String msg){
        System.out.printf("请输入%s密码：",msg);
        return DataProcessing.inputStr(AbstractUser::checkPassword);
    }
    /**
     * TODO 输入角色对应数字，判断范围后转换为角色字符串
     *
     * @param
     * @return java.lang.String
     * @throws
     */
    public static String inputRole(){
        System.out.print("请选择角色：\n0-Browser\t1-Operator\t2-Administrator\n请输入：");
        int option = DataProcessing.inputInt();
        return  DataProcessing.rangeInt(option,ROLE_MIN,ROLE_MAX) ? DataProcessing.Role.values()[option].getName() : "" ;
    }
    /**
     * TODO 列表展示时，显示格式化列名
     *
     * @param
     * @return void
     * @throws
     */
    public static void listShowColumns(){
        System.out.printf("%10s %10s %20s\n","Name","Password","Role");
    }
    /**
     * TODO 列表展示时，显示格式化信息
     *
     * @param
     * @return void
     * @throws
     */
    public void listShow(){
        System.out.printf("%10s %10s %20s\n",name,password,role);
    }
}
