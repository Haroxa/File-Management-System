package console;

import java.sql.*;
import java.util.*;
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

    public static String asterisk="****************************************";

    static String driverName = "com.mysql.cj.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/java实验?serverTimezone=GMT%2B8&useSSL=false"; // 声明数据库的URL
    static String user = "root";
    static String password = "mysqladmin";

    static Connection connection;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;
    static int resultInt;
    static boolean connectDB = false;
    static final String DEFAULT_PASSWORD = "123";

    static {
        initDB();
    }

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }

    /**
     * TODO 初始化，连接数据库
     *
     * @param
     * @return void
     * @throws
     */
    public static void initDB() {
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, user, password);
            connectDB = true;
        } catch (Exception e) {
            System.out.printf("{ msg:database init error, error:%s }\n", e.getMessage());
        }
        System.out.printf("{ msg:database init success, connectDB:%b }\n", connectDB);
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
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                System.out.printf("{ msg:database disconnect error, error:%s }\n", e.getMessage());
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
     * TODO 按档案编号搜索文档，返回null表明未找到
     *
     * @param sid 文档编号
     * @return console.Doc
     * @throws
     */
    public static Doc searchDoc(String sid) {
        try {
            preparedStatement = connection.prepareStatement("select * from doc_info where sid = ?");
            preparedStatement.setString(1, sid);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Doc(resultSet);
            }
        } catch (Exception e) {
            System.out.printf("{ msg:search doc error, sid:%s, error:%s }\n", sid, e.getMessage());
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
        Vector<Doc> vector = new Vector<>();
        try {
            preparedStatement = connection.prepareStatement("select * from doc_info");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                vector.add(new Doc(resultSet));
            }
        } catch (Exception e) {
            System.out.printf("{ msg:list doc error, error:%s }\n", e.getMessage());
        }
        return vector.elements();
    }

    static void setPreparedStatementArgs(Object... args) throws SQLException {
        int i = 1;
        for (Object arg : args) {
            preparedStatement.setObject(i, arg);
            i += 1;
        }
    }

    /**
     * TODO 插入新的档案
     *
     * @param sid          档案编号
     * @param creator     创建者
     * @param timestamp   时间戳
     * @param description 描述
     * @param filename    文件名
     * @return boolean
     * @throws
     */
    public static boolean insertDoc(String sid, String creator, Timestamp timestamp, String description, String filename) {
        try {
            preparedStatement = connection.prepareStatement("insert into doc_info (sid, creator, timestamp, description, filename) values (?,?,?,?,?)");
            setPreparedStatementArgs(sid,creator,timestamp,description,filename);
            resultInt = preparedStatement.executeUpdate();
            return resultInt != 0;
        } catch (Exception e) {
            System.out.printf("{ msg:insert doc error, doc:%s, error:%s }\n", new Doc(sid,creator,timestamp,description,filename), e.getMessage());
        }
        return false;
    }

    public static Vector<Vector<String>> listDocToVector() {
        Enumeration<Doc> e = listDoc();
        Vector<Vector<String>> vector = new Vector<>();
        if (e != null) {
            while (e.hasMoreElements()) {
                Doc doc = e.nextElement();
                vector.add(doc.toVector());
            }
        }
        return vector;
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
            preparedStatement = connection.prepareStatement("select * from user_info where username = ? and password = ?");
            setPreparedStatementArgs(name,password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                return AbstractUser.newUser(name,password,role);
            }
        } catch (Exception e) {
            System.out.printf("{ msg:verify user error, name:%s, password:%s, error:%s }\n", name, password, e.getMessage());
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
            preparedStatement = connection.prepareStatement("select * from user_info where username = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                return AbstractUser.newUser(name,password,role);
            }
        } catch (Exception e) {
            System.out.printf("{ msg:search user error, name:%s, error:%s }\n", name, e.getMessage());
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
        Vector<AbstractUser> users = new Vector<>();
        try {
            preparedStatement = connection.prepareStatement("select * from user_info");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(AbstractUser.newUser(resultSet));
            }
        } catch (Exception e) {
            System.out.printf("{ msg:list user error, error:%s }\n", e.getMessage());
        }
        return users.elements();
    }

    public static Vector<Vector<String>> listUserToVector() {
        Enumeration<AbstractUser> e = listUser();
        Vector<Vector<String>> vector = new Vector<>();
        if (e != null) {
            while (e.hasMoreElements()) {
                AbstractUser u = e.nextElement();
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
        try {
            preparedStatement = connection.prepareStatement("update user_info set password = ? , role = ? where username = ?");
            setPreparedStatementArgs(password,role,name);
            resultInt = preparedStatement.executeUpdate();
            return resultInt != 0;
        } catch (Exception e) {
            System.out.printf("{ msg:update user error, user:%s, error:%s }\n", AbstractUser.newUser(name, password, role), e.getMessage());
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
        try {
            preparedStatement = connection.prepareStatement("insert into user_info values (?,?,?)");
            setPreparedStatementArgs(name,password,role);
            resultInt = preparedStatement.executeUpdate();
            return resultInt != 0;
        } catch (Exception e) {
            System.out.printf("{ msg:insert user error, user:%s, error:%s }\n", AbstractUser.newUser(name,password,role), e.getMessage());
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
            preparedStatement = connection.prepareStatement("delete from user_info where username = ?");
            preparedStatement.setString(1,name);
            resultInt = preparedStatement.executeUpdate();
            return resultInt != 0;
        } catch (Exception e) {
            System.out.printf("{ msg:insert doc error, name:%s, error:%s }\n", name, e.getMessage());
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
     * @param str     待检测字符串
     * @param pattern 正则匹配串
     * @param min     最小长度
     * @param max     最大长度
     * @return boolean
     * @throws
     */
    public static boolean checkStrPatternAndLen(String str, String pattern, int min, int max) {
        try {
            return str.matches(pattern) && rangeInt(str.length(), min, max);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

    }
}
