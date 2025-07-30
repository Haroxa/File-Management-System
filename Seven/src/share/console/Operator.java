package share.console;

import java.io.File;
import java.sql.Timestamp;
import java.util.Scanner;
/**
 * TODO 操作员类
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public class Operator extends AbstractUser {

    public Operator(String name, String password) {
        super(name, password, DataProcessing.Role.OPERATOR.getName());
    }
    static String tip_main = String.format("""
                    %1$.5s欢迎进入档案录入员菜单%1$.5s
                    %2$10s1.上传文件
                    %2$10s2.下载文件
                    %2$10s3.文件列表
                    %2$10s4.修改密码
                    %2$10s5.退出
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
                    System.out.println("上传文件");
                    fileUpload();
                    break;
                case 2:
                    System.out.println("下载文件");
                    fileDownload();
                    break;
                case 3:
                    System.out.println("文件列表");
                    showFileList();
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

    /**
     * TODO 输入文件相关信息，检验后进行创建上传
     *
     * @param
     * @return void
     * @throws
     */
    public void fileUpload(){
        Scanner in = new Scanner(System.in);
        System.out.print("请输入文件路径：");
        String filepath = in.nextLine();
        String id = Doc.inputId(in);
        String description = Doc.inputDescription(in);

        String filename = DataProcessing.getFilename(filepath);
        if ( filename.isEmpty() ){
            System.out.println("文件路径格式错误");
        }else if (id.isEmpty() || DataProcessing.searchDoc(id)!=null) {
            System.out.println("档案编号格式错误或已存在");
        } else if (description.isEmpty()) {
            System.out.println("档案描述格式错误");
        } else if ( !AbstractUser.copyFile(filepath , AbstractUser.uploadPath + "%s-%s".formatted(id,filename ) ) ) {
            System.out.println("档案上传失败");
        }else if( DataProcessing.insertDoc(id,this.getName(),
                new Timestamp(System.currentTimeMillis()),description,filename ) ){
            System.out.println("上传成功");
        }else {
            System.out.println("上传失败");
        }
    }


    public static void main(String[] args) {
        // 获取当前文件的绝对路径
        File currentFile = new File("");
        String absolutePath = currentFile.getAbsolutePath();
        System.out.println(currentFile.exists());
        System.out.println("当前文件的绝对路径是：" + absolutePath);
    }
}
