package console;

import java.util.Scanner;
/**
 * TODO 浏览者类
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public class Browser extends AbstractUser {
    public Browser(String name, String password) {
        super(name, password, DataProcessing.Role.BROWSER.getName());
    }

    static String tip_main = String.format("""
                    %1$.5s欢迎进入档案浏览员菜单%1$.5s
                    %2$10s1.下载文件
                    %2$10s2.文件列表
                    %2$10s3.修改密码
                    %2$10s4.退出
                    %1$.29s""", DataProcessing.asterisk,"");
    @Override
    public void showMenu() {
        Scanner in =new Scanner(System.in);

        while(true){
            System.out.println(tip_main);
            System.out.printf(Main.tip_menu);

            int option = DataProcessing.inputInt(in);
            System.out.println(option);
            switch(option){
                case 1:
                    System.out.println("下载文件");
                    fileDownload();
                    break;
                case 2:
                    System.out.println("文件列表");
                    showFileList();
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

    public static void main(String[] args) {
        AbstractUser user= DataProcessing.searchUser("brow1");
        assert user != null;
        user.showMenu();
    }
}
