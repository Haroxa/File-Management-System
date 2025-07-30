import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
/**
* TODO 数据处理类
*
* @author Haroxa
* @date 2023/11/15
*/
public class DataProcessing {
    enum Role{
        // 角色的枚举值
        BROWSER("Browser"), OPERATOR("Operator"), ADMINISTRATOR("Administrator");
        private final String name;
        Role(String name) {
            this.name=name;
        }
        public String getName(){
            return name;
        }
    }
    private static boolean connectDB = false;
    static  final double EXCEPTION_CONNECT_PROBABILITY = 0.1;
    static  final double EXCEPTION_SQL_PROBABILITY = 0.9;
    static Hashtable<String, AbstractUser> users;
    static final String DEFAULT_PASSWORD = "123";
    static {
        users = new Hashtable<>();
        users.put("jack", new Operator("jack", DEFAULT_PASSWORD));
        users.put("rose", new Browser("rose", DEFAULT_PASSWORD));
        users.put("kate", new Administrator("kate", DEFAULT_PASSWORD));
        users.put("super", new Administrator("super", DEFAULT_PASSWORD));
        init();
    }

    /**
     * TODO 初始化，连接数据库
     *
     * @param
     * @return void
     * @throws
     */
    public static void init(){
        double ranValue = Math.random();
        connectDB = ranValue > EXCEPTION_CONNECT_PROBABILITY;
    }

    /**
     * TODO 关闭数据库连接
     *
     * @param
     * @return void
     * @throws
     */
    public static void disconnectFromDB(){
        if (connectDB){
            try{
                ranSQLException("Error in disconnecting DB");
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                connectDB = false;
            }
        }
    }

    /**
     * TODO 检测数据库连接状态
     *
     * @param
     * @return void
     * @throws SQLException
     */
    public static void checkDB() throws SQLException{
        if(!connectDB){
            throw new SQLException("Not Connected to Database");
        }
    }


    /**
     * TODO 产生随机的SQLException
     *
     * @param msg 错误信息
     * @return void
     * @throws SQLException
     */
    public static void ranSQLException(String msg) throws SQLException{
        double ranValue = Math.random();
        if(ranValue > EXCEPTION_SQL_PROBABILITY){
            throw new SQLException(msg);
        }
    }

    /**
     * TODO 验证用户密码
     *
     * @param name 用户名
     * @param password 密码
     * @return AbstractUser
     * @throws
     */
    public static AbstractUser verifyUser(String name, String password){
        try {
            checkDB();
            ranSQLException("Error in executing Verify");
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        if (users.containsKey(name)){
            AbstractUser temp = users.get(name);
            if (temp.getPassword().equals(password)){
                return temp;
            }
        }
        return null;
    }

    /**
     * TODO 按用户名搜索用户，返回null表明未找到符合条件的用户
     *
     * @param name 用户名
     * @return AbstractUser
     * @throws
     */
    public static AbstractUser searchUser(String name){
        try {
            checkDB();
            ranSQLException("Error in executing Query");
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        if (users.containsKey(name)){
            return users.get(name);
        }
        return null;
    }

    /**
     * TODO 取出所有的用户
     *
     * @param
     * @return java.util.Enumeration<AbstractUser>
     * @throws
     */
    public static Enumeration<AbstractUser> listUser(){
        try{
            checkDB();
            ranSQLException("Error in executing Query listUser");
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return users.elements();
    }

    /**
     * TODO 修改用户信息
     *
     * @param name 用户名
     * @param password 密码
     * @param role 角色
     * @return boolean
     * @throws
     */
    public static boolean updateUser(String name,String password,String role){
        AbstractUser user;
        try{
            checkDB();
            ranSQLException("Error in executing Update");
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
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

    /**
     * TODO 插入新用户
     *
     * @param name 用户名
     * @param password 密码
     * @param role 角色
     * @return boolean
     * @throws
     */
    public static boolean insertUser(String name,String password,String role){
        AbstractUser user;
        try{
            checkDB();
            ranSQLException("Error in executing insert");
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
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

    /**
     * TODO 删除指定用户
     *
     * @param name 用户名
     * @return boolean
     * @throws
     */
    public static boolean deleteUser(String name){
        try{
            checkDB();
            ranSQLException("Error in executing Delete");
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        if (users.containsKey(name)){
            users.remove(name);
            return true;
        }
        return false;
    }

    /**
     * TODO 读取下一行数据，转换为整型
     *
     * @param in 输入流
     * @return int
     * @throws
     */
    public static int inputInt(Scanner in){
        try{
            String sin = in.nextLine();
            return Integer.parseInt(sin);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    /**
     * TODO 读取下一行数据，判断其是否与模式串匹配，并比较其长度
     *
     * @param in 输入流
     * @param pattern 模式串
     * @return java.lang.String
     * @throws
     */
    public static String inputStr(Scanner in,String pattern,int min,int max){
        try{
            String sin = in.nextLine();
            if( sin.matches(pattern) && rangeInt(sin.length() , min , max) ){
                return sin;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    /**
     * TODO 判断整型范围
     *
     * @param x 待判断执行
     * @param min 最小值
     * @param max 最大值
     * @return boolean
     * @throws
     */
    public static boolean rangeInt(int x, int min, int max){
        return min<=x && x<=max;
    }

    public static void main(String[] args){

    }
}
