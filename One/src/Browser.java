import java.util.Scanner;
public class Browser extends AbstractUser {
    Browser(String name, String password) {
        super(name, password, DataProcessing.Role.BROWSER.getName());
    }

    static String asterisk="****************************************";
    static String tip_main = String.format("""
                    %1$.5s欢迎进入档案浏览员菜单%1$.5s
                    %2$10s1.下载文件
                    %2$10s2.文件列表
                    %2$10s3.修改密码
                    %2$10s4.退出
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
                    System.out.println("下载文件");
                    break;
                case 2:
                    System.out.println("文件列表");
                    break;
                case 3:
                    System.out.println("修改密码");
                    changePassword();
                    break;
                case 4:
                    System.out.println("退出登录");
                    return;
                default:
                    System.out.println("未知的选项，请重新输入");
                    break;
            }
        }
    }
    public static void main(String[] args){
        AbstractUser user= DataProcessing.search("brow1");
        user.showMenu();
    }
}
