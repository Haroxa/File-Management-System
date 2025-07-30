package serializable;

import java.util.Enumeration;
/**
 * TODO 管理员类
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public class Administrator extends AbstractUser {

    Administrator(String name, String password) {
        super(name, password, DataProcessing.Role.ADMINISTRATOR.getName());
    }
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
                    %1$.29s""", DataProcessing.asterisk,"");
    @Override
    public void showMenu() {
        while(true){
            System.out.println(tip_main);
            System.out.printf(Main.tip_menu);

            int option = DataProcessing.inputInt();
            System.out.println(option);
            switch(option){
                case 1:
                    System.out.println("新增用户");
                    userInsert();
                    break;
                case 2:
                    System.out.println("删除用户");
                    userDelete();
                    if(DataProcessing.searchUser(getName()) == null){
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
                    fileDownload();
                    break;
                case 6:
                    System.out.println("文件列表");
                    showFileList();
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
    /**
     * TODO 输入用户信息，验证后进行添加
     *
     * @param
     * @return void
     * @throws
     */
    public static void userInsert(){
        String name = inputName();
        String password = inputPassword("");
        String role = inputRole();
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
    /**
     * TODO 输入指定用户名，进行删除
     *
     * @param
     * @return void
     * @throws
     */
    public static void userDelete(){
        String name = inputName();
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
    /**
     * TODO 输入指定用户名及其相关信息，进行更新(不会更新自身的角色)
     *
     * @param
     * @return void
     * @throws
     */
    public void userUpdate() {
        String name = inputName();
        String password = inputPassword("新");
        String role = inputRole();

        if (name.isEmpty() || password.isEmpty() || role.isEmpty()) {
            System.out.println("输入格式错误，请重试");
        } else if (name.equals("super") && !getName().equals("super")) {
            System.out.println("无法修改超级管理员信息");
        } else if (name.equals(this.getName())) {
            // 对自身只会修改密码，不会修改角色
            System.out.println("无法修改自身角色");
            if (changeSelfInfo(password)) {
                System.out.println("密码修改成功");
            } else {
                System.out.println("密码修改失败");
            }
        } else if (DataProcessing.updateUser(name, password, role)) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }
    }
    /**
     * TODO 获取所有用户信息，进行展示
     *
     * @param
     * @return void
     * @throws
     */
    public void listUserShow(){
        Enumeration<AbstractUser> e = DataProcessing.listUser();
        if(e==null){
            System.out.println("用户列表获取失败");
        }else if(!e.hasMoreElements()){
            System.out.println("用户列表为空");
        } else {
            listShowColumns();
            while(e.hasMoreElements()){
                AbstractUser u=e.nextElement();
                u.listShow();
            }
        }
    }

    public static void main(String[] args) {
        AbstractUser user= DataProcessing.searchUser("admin1");
        assert user != null;
        user.showMenu();
    }
}
