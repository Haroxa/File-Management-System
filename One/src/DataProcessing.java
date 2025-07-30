import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class DataProcessing {

    enum Role{
        //
        BROWSER("Browser"), OPERATOR("Operator"), ADMINISTRATOR("Administrator");
        private final String name;

        private Role(String name) {
            this.name=name;
        }
        public String getName(){
            return name;
        }
    }
    static String asterisk="****************************************";
    static String tip_menu="请选择菜单：";
    static Hashtable<String, AbstractUser> users;
    static final String DEFAULT_PASSWORD = "123";
    static {
        users = new Hashtable<>();
        users.put("jack", new Operator("jack", DEFAULT_PASSWORD));
        users.put("rose", new Browser("rose", DEFAULT_PASSWORD));
        users.put("kate", new Administrator("kate", DEFAULT_PASSWORD));
        users.put("super", new Administrator("super", DEFAULT_PASSWORD));
    }

    public static AbstractUser search(String name){
        if (users.containsKey(name)){
            return users.get(name);
        }
        return null;
    }

    public static AbstractUser verify(String name, String password){
        if (users.containsKey(name)){
            AbstractUser temp = users.get(name);
            if (temp.getPassword().equals(password)) {
                return temp;
            }
        }
        return null;
    }

    public static Enumeration<AbstractUser> listUser(){
        return users.elements();
    }
    public static boolean updateUser(String name,String password,String role){
        AbstractUser user;
        if (users.containsKey(name)){
            switch (Role.valueOf(role.toUpperCase())){
                case ADMINISTRATOR:
                    user=new Administrator(name, password);
                    break;
                case OPERATOR:
                    user=new Operator(name, password);
                    break;
                case BROWSER:
                    user=new Browser(name, password);
                    break;
                default:
                    return false;
            }
            users.put(name,user);
            return true;
        }
        return false;
    }

    public static boolean insertUser(String name,String password,String role){
        AbstractUser user;
        if (!users.containsKey(name)){
            switch (Role.valueOf(role.toUpperCase())){
                case ADMINISTRATOR:
                    user=new Administrator(name, password);
                    break;
                case OPERATOR:
                    user=new Operator(name, password);
                    break;
                case BROWSER:
                    user=new Browser(name, password);
                    break;
                default:
                    return false;
            }
            users.put(name,user);
            return true;
        }
        return false;
    }
    public static boolean deleteUser(String name){
        if (users.containsKey(name)){
            users.remove(name);
            return true;
        }
        return false;
    }
    public static int inputInt(Scanner in){
        String sin = in.nextLine();
        return Integer.parseInt(sin);
    }

    public static String inputStr(Scanner in,String pattern,int min,int max){
        String sin = in.nextLine();
        if( sin.matches(pattern) && rangeInt(sin.length() , min , max) ){
            return sin;
        }
        return "";
    }

    public static boolean rangeInt(int x, int min, int max){
        return min<=x && x<=max;
    }

    public static void main(String[] args){

    }
}
