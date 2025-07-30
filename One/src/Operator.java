import java.util.Scanner;

public class Operator extends AbstractUser {

    Operator(String name, String password) {
        super(name, password, DataProcessing.Role.OPERATOR.getName());
    }

    static String asterisk="****************************************";
    static String tip_main = String.format("""
                    %1$.5s欢迎进入档案录入员菜单%1$.5s
                    %2$10s1.上传文件
                    %2$10s2.下载文件
                    %2$10s3.文件列表
                    %2$10s4.修改密码
                    %2$10s5.退出
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
                    System.out.println("上传文件");
                    break;
                case 2:
                    System.out.println("下载文件");
                    break;
                case 3:
                    System.out.println("文件列表");
                    break;
                case 4:
                    System.out.println("修改密码");
                    changePassword();
                    break;
                case 5:
                    System.out.println("退出登录");
                    return;
                default:
                    System.out.println("未知的选项，请重新输入");
                    break;
            }
        }
    }
    public static void main(String[] args){
        AbstractUser user= DataProcessing.search("oper1");
        user.showMenu();
    }
}
