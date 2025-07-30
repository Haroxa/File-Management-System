package console;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.util.function.Function;

/**
 * TODO 数据处理类
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public class DataProcessing {
    public enum Role {
        // 角色的枚举值
        BROWSER("Browser"), OPERATOR("Operator"), ADMINISTRATOR("Administrator");
        private final String name;

        Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static boolean connectDB = false;
    static final double EXCEPTION_CONNECT_PROBABILITY = 0;
    static final double EXCEPTION_SQL_PROBABILITY = 0.99999999;
    static Hashtable<String, AbstractUser> users;
    static Hashtable<String, Doc> docs;
    static final String DEFAULT_PASSWORD = "123";
    static {
        users = new Hashtable<>();
        users.put("jack", new Operator("jack", DEFAULT_PASSWORD));
        users.put("rose", new Browser("rose", DEFAULT_PASSWORD));
        users.put("kate", new Administrator("kate", DEFAULT_PASSWORD));
        users.put("super", new Administrator("super", DEFAULT_PASSWORD));
        init();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        docs = new Hashtable<>();
        docs.put("0001", new Doc("0001", "jack", timestamp, "console.Doc Source Java", "Doc.java"));
    }
    public static String getDefaultPassword(){
        return DEFAULT_PASSWORD;
    }


    /**
     * TODO 初始化，连接数据库
     *
     * @param
     * @return void
     * @throws
     */
    public static void init() {
        connectDB = true;
    }

    /**
     * TODO 关闭数据库连接
     *
     * @param
     * @return void
     * @throws
     */
    public static void disconnectFromDB() {
        if (connectDB) {
            try {
                ranSQLException("Error in disconnecting DB");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
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
    public static void checkDB() throws SQLException {
        if (!connectDB) {
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
    public static void ranSQLException(String msg) throws SQLException {
        double ranValue = Math.random();
        if (ranValue > EXCEPTION_SQL_PROBABILITY) {
            throw new SQLException(msg);
        }
    }

    /**
     * TODO 按档案编号搜索文档，返回null表明未找到
     *
     * @param id 文档编号
     * @return console.Doc
     * @throws
     */
    public static Doc searchDoc(String id) {
        try {
            checkDB();
            ranSQLException("Error in executing Query doc");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        if (docs.containsKey(id)) {
            return docs.get(id);
        }
        return null;
    }

    /**
     * TODO 取出所有的文档
     *
     * @param
     * @return java.util.Enumeration<console.Doc>
     * @throws
     */
    public static Enumeration<Doc> listDoc() {
        try {
            checkDB();
            ranSQLException("Error in executing Query listDoc");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Enumeration<Doc> e = docs.elements();
        return e;
    }

    /**
     * TODO 修改用户信息
     *
     * @param id          档案编号
     * @param description 描述
     * @param filename    文件名
     * @return boolean
     * @throws
     */
    public static boolean updateDoc(String id, String description, String filename) {
        Doc doc;
        try {
            checkDB();
            ranSQLException("Error in executing Update");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (docs.containsKey(id)) {
            doc = docs.get(id);
            doc.setDescription(description);
            doc.setFilename(filename);
            docs.put(id, doc);
            return true;
        }
        return false;
    }

    /**
     * TODO 插入新的档案
     *
     * @param id          档案编号
     * @param creator     创建者
     * @param timestamp   时间戳
     * @param description 描述
     * @param filename    文件名
     * @return boolean
     * @throws
     */
    public static boolean insertDoc(String id, String creator, Timestamp timestamp, String description, String filename) {
        Doc doc;
        try {
            checkDB();
            ranSQLException("Error in executing Insert doc");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (!docs.containsKey(id)) {
            doc = new Doc(id, creator, timestamp, description, filename);
            docs.put(id, doc);
            return true;
        }
        return false;
    }
    public static Vector<Vector<String>> listDocToVector(){
        Enumeration<Doc> e = listDoc();
        Vector<Vector<String>> vector = new Vector<>();
        if(e!=null){
            while(e.hasMoreElements()){
                Doc doc=e.nextElement();
                vector.add(doc.toVector());
            }
        }
        return vector;
    }
    /**
     * TODO 删除指定文档
     *
     * @param id
     * @return boolean
     * @throws
     */
    public static boolean deleteDoc(String id) {
        try {
            checkDB();
            ranSQLException("Error in executing Delete doc");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (docs.containsKey(id)) {
            docs.remove(id);
            return true;
        }
        return false;
    }

    /**
     * TODO 验证用户密码
     *
     * @param name     用户名
     * @param password 密码
     * @return console.AbstractUser
     * @throws
     */
    public static AbstractUser verifyUser(String name, String password) {
        try {
            checkDB();
            ranSQLException("Error in executing Verify");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        if (users.containsKey(name)) {
            AbstractUser temp = users.get(name);
            if (temp.getPassword().equals(password)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * TODO 按用户名搜索用户，返回null表明未找到符合条件的用户
     *
     * @param name 用户名
     * @return console.AbstractUser
     * @throws
     */
    public static AbstractUser searchUser(String name) {
        try {
            checkDB();
            ranSQLException("Error in executing Query");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        if (users.containsKey(name)) {
            return users.get(name);
        }
        return null;
    }

    /**
     * TODO 取出所有的用户
     *
     * @param
     * @return java.util.Enumeration<console.AbstractUser>
     * @throws
     */
    public static Enumeration<AbstractUser> listUser() {
        try {
            checkDB();
            ranSQLException("Error in executing Query listUser");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users.elements();
    }
    public static Vector<Vector<String>> listUserToVector(){
        Enumeration<AbstractUser> e = listUser();
        Vector<Vector<String>> vector = new Vector<>();
        if(e!=null){
            while(e.hasMoreElements()){
                AbstractUser u=e.nextElement();
                vector.add(u.toVector());
            }
        }
        return vector;
    }
    /**
     * TODO 修改用户信息
     *
     * @param name     用户名
     * @param password 密码
     * @param role     角色
     * @return boolean
     * @throws
     */
    public static boolean updateUser(String name, String password, String role) {
        AbstractUser user;
        try {
            checkDB();
            ranSQLException("Error in executing Update");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (users.containsKey(name)) {
            switch (Role.valueOf(role.toUpperCase())) {
                case ADMINISTRATOR:
                    user = new Administrator(name, password);
                    break;
                case OPERATOR:
                    user = new Operator(name, password);
                    break;
                case BROWSER:
                    user = new Browser(name, password);
                    break;
                default:
                    return false;
            }
            users.put(name, user);
            return true;
        }
        return false;
    }

    /**
     * TODO 插入新用户
     *
     * @param name     用户名
     * @param password 密码
     * @param role     角色
     * @return boolean
     * @throws
     */
    public static boolean insertUser(String name, String password, String role) {
        AbstractUser user;
        try {
            checkDB();
            ranSQLException("Error in executing insert");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (!users.containsKey(name)) {
            switch (Role.valueOf(role.toUpperCase())) {
                case ADMINISTRATOR:
                    user = new Administrator(name, password);
                    break;
                case OPERATOR:
                    user = new Operator(name, password);
                    break;
                case BROWSER:
                    user = new Browser(name, password);
                    break;
                default:
                    return false;
            }
            users.put(name, user);
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
    public static boolean deleteUser(String name) {
        try {
            checkDB();
            ranSQLException("Error in executing Delete");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (users.containsKey(name)) {
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
    public static int inputInt(Scanner in) {
        try {
            String sin = in.nextLine();
            return Integer.parseInt(sin);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * TODO 读取下一行数据，使用传入的检查函数进行检验
     *
     * @param in    输入流
     * @param check 检查函数
     * @return java.lang.String
     * @throws
     */
    public static String inputStr(Scanner in, Function<String, Boolean> check) {
        try {
            String sin = in.nextLine();
            if (check.apply(sin)) {
                return sin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * TODO 判断整型范围
     *
     * @param x   待判断执行
     * @param min 最小值
     * @param max 最大值
     * @return boolean
     * @throws
     */
    public static boolean rangeInt(int x, int min, int max) {
        return min <= x && x <= max;
    }

    /**
     * TODO 从文件路径中获取文件名
     *
     * @param filepath 文件路径
     * @return java.lang.String
     * @throws
     */
    public static String getFilename(String filepath){
        try {
            return filepath.substring(filepath.lastIndexOf("\\")+1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * TODO 检查字符串中是否包含指定内容，并且长度是否在指定范围
     *
     * @param str 待检测字符串
     * @param pattern 正则匹配串
     * @param min 最小长度
     * @param max 最大长度
     * @return boolean
     * @throws
     */
    public static boolean checkStrPatternAndLen(String str, String pattern, int min, int max) {
        try {
            return str.matches(pattern) && rangeInt(str.length(), min, max);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

    }
}
