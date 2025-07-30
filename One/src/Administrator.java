import java.util.Enumeration;
import java.util.Scanner;

public class Administrator extends AbstractUser {

    Administrator(String name, String password) {
        super(name, password, DataProcessing.Role.ADMINISTRATOR.getName());
    }

    static String asterisk="****************************************";
    static String tip_main = String.format("""
                    %1$.5s欢迎进入系统管理员菜单%1$.5s
                    %2$10s1.新增用户
                    %2$10s2.删除用户
                    %2$10s3.修改用户
                    %2$10s4.用户列表
                    %2$10s5.下载文件
                    %2$10s6.文件列表
                    %2$10s7.修改密码
                    %2$10s8.退出
                    %1$.29s""", asterisk,"");
    @Override
    public void showMenu() {
        Scanner in =new Scanner(System.in);

        while(true){
            System.out.println(tip_main);
            System.out.printf(DataProcessing.tip_menu);
            int option = DataProcessing.inputInt(in);
            System.out.println(option);
            switch(option){
                case 1:
                    System.out.println("新增用户");
                    userInsert();
                    break;
                case 2:
                    System.out.println("删除用户");
                    userDelete();
                    if(DataProcessing.search(getName()) == null){
                        System.out.println("用户已注销");
                        return;
                    }
                    break;
                case 3:
                    System.out.println("修改用户");
                    userUpdate();
                    break;
                case 4:
                    System.out.println("用户列表");
                    listUserShow();
                    break;
                case 5:
                    System.out.println("下载文件");
                    break;
                case 6:
                    System.out.println("文件列表");
                    break;
                case 7:
                    System.out.println("修改密码");
                    changePassword();
                    break;
                case 8:
                    System.out.println("退出登录");
                    return;
                default:
                    System.out.println("未知的选项，请重新输入");
                    break;
            }
        }
    }

    public static void userInsert(){
        Scanner in = new Scanner(System.in);
        String name = inputName(in);
        String password = inputPassword(in,"");
        String role = inputRole(in);
        if( name.isEmpty() || password.isEmpty() || role.isEmpty() ){
            System.out.println("输入格式错误，请重试");
            return;
        }
        if(DataProcessing.insertUser(name,password,role)){
            System.out.println("添加成功");
        }else{
            System.out.println("添加失败");
        }
    }
    public void userDelete(){
        Scanner in = new Scanner(System.in);
        String name = inputName(in);
        if(name.isEmpty()){
            System.out.println("输入格式错误，请重试");
            return;
        }
        if (name.equals("super")){
            System.out.println("无法删除超级管理员");
        }else if(DataProcessing.deleteUser(name)){
            System.out.println("删除成功");
        }else{
            System.out.println("删除失败");
        }
    }
    public void userUpdate(){
        Scanner in = new Scanner(System.in);
        String name = inputName(in);
        String password = inputPassword(in,"新");
        String role = inputRole(in);
        if( name.isEmpty() || password.isEmpty() || role.isEmpty() ){
            System.out.println("输入格式错误，请重试");
            return;
        }
        boolean status;
        if( name.equals(this.getName()) ){
            // 对自身只会修改密码，不会修改角色
            status = this.changeSelfInfo(password);
        }else{
            status = DataProcessing.updateUser(name,password,role);
        }
        if(status){
            System.out.println("更新成功");
        }else{
            System.out.println("更新失败");
        }
    }
    public static void listUserShow(){
        Enumeration<AbstractUser> e= DataProcessing.listUser();
        if(e==null){
            System.out.println("用户列表获取失败");
        }else if(!e.hasMoreElements()){
            System.out.println("用户列表为空");
        } else {
            listShowColumns();
            while(e.hasMoreElements()){
                //调用nextElement方法获取元素
                AbstractUser u=e.nextElement();
                u.listShow();
            }
        }
    }

    public static void main(String[] args){
        AbstractUser user= DataProcessing.search("admin1");
        user.showMenu();
    }
}
