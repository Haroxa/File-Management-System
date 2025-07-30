import java.util.Scanner;

public abstract class AbstractUser {
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
    public abstract void showMenu();
    public boolean changeSelfInfo(String password){
        if (DataProcessing.updateUser(name,password,role)){
            this.password = password;
            return true;
        }
        return false;
    }
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

    public void showFileList(){
        System.out.println("文件列表...");
    }
    public boolean downloadFile(){
        System.out.println("下载文件...");
        return true;
    }
    public void changeSelfInfo(){
        System.out.println("修改信息...");
    }
    public void exitSystem(){
        System.out.println("系统退出，谢谢使用！");
        System.exit(0);
    }
    public static AbstractUser login(){
        Scanner in = new Scanner(System.in);
        String name = inputName(in);
        String password = inputPassword(in,"");
        return DataProcessing.verify(name,password);
    }
    public static String inputName(Scanner in){
        System.out.print("请输入用户名：");
        return DataProcessing.inputStr(in,"[a-zA-Z0-9_\\-@]+",2,10);
    }
    public static String inputPassword(Scanner in,String msg){
        System.out.printf("请输入%s密码：",msg);
        return DataProcessing.inputStr(in,"[a-zA-Z0-9_\\-@]+",2,10);
    }
    public static String inputRole(Scanner in){
        System.out.print("请选择角色：\n0-Browser\t1-Operator\t2-Administrator\n请输入：");
        int option = DataProcessing.inputInt(in);
        return  DataProcessing.rangeInt(option,0,2) ? DataProcessing.Role.values()[option].getName() : "" ;
    }
    public static void listShowColumns(){
        System.out.printf("%10s %10s %20s\n","Name","Password","Role");
    }
    public void listShow(){
        System.out.printf("%10s %10s %20s\n",name,password,role);
    }
}
