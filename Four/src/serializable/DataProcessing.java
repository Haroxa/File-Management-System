package serializable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    static String asterisk = "****************************************";
    private static boolean connectDB = false;
    static Hashtable<String, AbstractUser> users;
    static Hashtable<String, Doc> docs;
    static final String DEFAULT_PASSWORD = "123";
    static String dataPath = "\\_data\\", docsDataPath, usersDataPath;
    static Scanner in = new Scanner(System.in);
    static ObjectInputStream objIn;
    static ObjectOutputStream objOut;

    static {
        String currentFilePath = new File("").getAbsolutePath();
        dataPath = currentFilePath + dataPath;
        usersDataPath = createFilePath(dataPath + "users.txt");
        docsDataPath = createFilePath(dataPath + "docs.txt");
        System.out.printf("{ dataPath:%s, usersDataPath:%s, docsDataPath:%s }\n", dataPath, usersDataPath, docsDataPath);
        init();
        // 设置关闭时保存数据
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("即将关闭...");
            disconnectFromDB();
        }));
    }

    /**
     * TODO 初始化，连接数据库
     *
     * @param
     * @return void
     * @throws
     */
    public static void init() {
        // 防止重复操作
        if(connectDB == true){
            return;
        }
        // 初始化用户数据
        users = new Hashtable<>();
        readData(usersDataPath, users);
        // 读取失败使用默认数据
        if (users.isEmpty()) {
            System.out.printf("{ msg:use default values to init users, users:%s }\n", users);
            users.put("jack", new Operator("jack", DEFAULT_PASSWORD));
            users.put("rose", new Browser("rose", DEFAULT_PASSWORD));
            users.put("kate", new Administrator("kate", DEFAULT_PASSWORD));
            users.put("super", new Administrator("super", DEFAULT_PASSWORD));
        }
        // 初始化档案数据
        docs = new Hashtable<>();
        readData(docsDataPath, docs);
        // 读取失败使用默认数据
        if (docs.isEmpty()) {
            System.out.printf("{ msg:use default values to init docs, docs:%s }\n", docs.toString());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            docs.put("0001", new Doc("0001", "jack", timestamp, "Doc Source Java", "Doc.java"));
        }
        connectDB = true;
        System.out.printf("{ msg:init finish, connectDB:%b, \nusers:%s, \ndocs:%s }\n", connectDB, users.toString(), docs.toString());
    }
    /**
     * TODO 从指定文件反序列化读取至指定 Hashtable 数据
     *
     * @param filename 指定文件
     * @param table    指定数据
     * @return void
     * @throws
     */
    public static <T extends Base> void readData(String filename, Hashtable<String, T> table) {
        try {
            objIn = new ObjectInputStream(new FileInputStream(filename));
            while (true) {
                try {
                    T data = (T) objIn.readObject();
                    table.put(data.getKey(), data);
                } catch (EOFException e) {
                    break;
                }
            }
            objIn.close();
        } catch (Exception e) {
            System.out.printf("{ msg:read data error, error:%s, table:%s, objIn%s, objOut:%s }\n", e.getMessage(), table.toString(), objIn, objOut);
        }
    }

    /**
     * TODO 将指定 Hashtable 数据序列化写入指定文件
     *
     * @param filename 指定文件
     * @param table    指定数据
     * @return void
     * @throws
     */
    public static <T extends Base> void writeData(String filename, Hashtable<String, T> table) {
        try {
            objOut = new ObjectOutputStream(new FileOutputStream(filename));
            Enumeration<T> e = table.elements();
            while (e.hasMoreElements()) {
                T data = e.nextElement();
                objOut.writeObject(data);
            }
            objOut.close();
        } catch (Exception e) {
            System.out.printf("{ msg:write data error, error:%s, table:%s, objIn%s, objOut:%s }\n", e.getMessage(), table.toString(), objIn, objOut);
        }
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
                writeData(usersDataPath, users);
            } catch (Exception e) {
                System.out.printf("{ msg:write data error, error:%s, users:%s }\n", e.getMessage(), users.toString());
            }
            try {
                writeData(docsDataPath, docs);
            } catch (Exception e) {
                System.out.printf("{ msg:write data error, error:%s, docs:%s }\n", e.getMessage(), docs.toString());
            }
            connectDB = false;
        }
        try {
            in.close();
        } catch (Exception e) {
            System.out.printf("{ msg:release resource error, error:%s }\n", e.getMessage());
        }
        System.out.printf("{ msg:disconnect finish, connectDB:%b, \nusers:%s, \ndocs:%s }\n", connectDB, users.toString(), docs.toString());
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
     * @param id 文档编号
     * @return console.Doc
     * @throws
     */
    public static Doc searchDoc(String id) {
        try {
            checkDB();
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
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return docs.elements();
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

    public static String getDefaultPassword(){
        return DEFAULT_PASSWORD;
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
     * @param
     * @return int
     * @throws
     */
    public static int inputInt() {
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
     * @param check 检查函数
     * @return java.lang.String
     * @throws
     */
    public static String inputStr(Function<String, Boolean> check) {
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
    public static String getFilename(String filepath) {
        try {
            return filepath.substring(filepath.lastIndexOf("\\") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String createFilePath(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                Path filePath = Paths.get(filename);
                Path parentDirectory = filePath.getParent();
                Files.createDirectories(parentDirectory);
                Files.createFile(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }


    public static void main(String[] args) {

    }
}
