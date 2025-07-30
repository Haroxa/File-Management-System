import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
* TODO 抽象用户类，为各用户子类提供模板
*
* @author Haroxa
* @date 2023/11/15
*/
public abstract class AbstractUser {
    static final int NAME_MIN_LEN = 2,NAME_MAX_LEN = 10;
    static final int PWD_MIN_LEN = 2,PWD_MAX_LEN = 10;
    static final int ROLE_MIN = 0 , ROLE_MAX = 2;
    static final double EXCEPTION_PROBABILITY = 0.9;
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
     * TODO 下载档案文件
     *
     * @param id 文件编号
     * @return boolean
     * @throws
     */
    public boolean downloadFile(String id){
        double ranValue = Math.random();
        try{
            if(ranValue > EXCEPTION_PROBABILITY){
                throw new IOException("Error in accessing file");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("下载文件...");
        return true;
    }

    /**
     * TODO 展示档案文件列表
     *
     * @param
     * @return void
     * @throws
     */
    public void showFileList(){
        try{
            DataProcessing.ranSQLException("Error in accessing DB");
        }catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("文件列表...");
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
     * @return AbstractUser
     * @throws
     */
    public static AbstractUser login(){
        Scanner in = new Scanner(System.in);
        String name = inputName(in);
        String password = inputPassword(in,"");
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
        Scanner in = new Scanner(System.in);
        String oldPassword = inputPassword(in,"原");
        String password = inputPassword(in,"新");
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
     * TODO 输入姓名，满足指定字符和长度
     *
     * @param in 输入流
     * @return java.lang.String
     * @throws 
     */
    public static String inputName(Scanner in){
        System.out.print("请输入用户名：");
        return DataProcessing.inputStr(in,"[a-zA-Z0-9_\\-@]+",NAME_MIN_LEN,NAME_MAX_LEN);
    }
    /**
     * TODO 输入密码，满足指定字符和长度
     *
     * @param in 输入流
     * @param msg 提示信息
     * @return java.lang.String
     * @throws 
     */
    public static String inputPassword(Scanner in,String msg){
        System.out.printf("请输入%s密码：",msg);
        return DataProcessing.inputStr(in,"[a-zA-Z0-9_\\-@]+",PWD_MIN_LEN,PWD_MAX_LEN);
    }
    /**
     * TODO 输入角色对应数字，判断范围后转换为角色字符串
     *
     * @param in
     * @return java.lang.String
     * @throws
     */
    public static String inputRole(Scanner in){
        System.out.print("请选择角色：\n0-Browser\t1-Operator\t2-Administrator\n请输入：");
        int option = DataProcessing.inputInt(in);
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
