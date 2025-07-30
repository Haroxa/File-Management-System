package client;

import share.Common;
import share.Info;
import share.console.AbstractUser;
import share.console.Doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * TODO 客户端发送信息并处理返回数据
 *
 * @author Haroxa
 * @date 2023/12/23
 */
public class DealClient {
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
    static final String DEFAULT_PASSWORD = "123";
    static Hashtable<String,Object> data = new Hashtable<>();
    static String host = Common.SERVER_HOST;
    static int port = Common.SERVER_PORT;
    static Client client;

    static {
        connectServer();
    }

    public static void connectServer(){
        client = new Client(host,port);
        client.connectToServer();
        client.initStreams();
    }

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
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
            data.clear();
            data.put("id",sid);
            Info info = new Info(Common.SEARCH_DOC.getName(), data);
            client.sendData(info);
            return (Doc) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:search doc error, sid:%s, error:%s }\n", sid, e.getMessage());
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
    public static Vector<Doc> listDoc() {
        Vector<Doc> vector = new Vector<>();
        try {
            data.clear();
            Info info = new Info(Common.LIST_DOC.getName(), data);
            client.sendData(info);
            return (Vector<Doc>) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:list doc error, error:%s }\n", e.getMessage());
        }
        return vector;
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
            data.clear();
            data.put("id",sid);
            data.put("creator",creator);
            data.put("timestamp",timestamp);
            data.put("description",description);
            data.put("filename",filename);
            Info info = new Info(Common.INSERT_DOC.getName(), data);
            client.sendData(info);
            return (boolean) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:insert doc error, doc:%s, error:%s }\n", new Doc(sid,creator,timestamp,description,filename), e.getMessage());
        }
        return false;
    }

    /**
     * TODO 上传档案
     *
     * @param sourcePath 文件路径
     * @param id 档案编号
     * @param filename 文件名
     * @return boolean
     * @throws
     */
    public static boolean uploadDoc(String sourcePath,String id,String filename){
        try{
            String source = "%s\\%s".formatted(sourcePath,filename);
            File sourceFile = new File(source);
            if (!sourceFile.exists()){
                return false;
            }

            data.clear();
            data.put("id",id);
            data.put("filename",filename);
            Info info = new Info(Common.UPLOAD_DOC.getName(), data);
            client.sendData(info);

            FileInputStream in = new FileInputStream(sourceFile);
            byte[] buffer = new byte[Common.MAX_BYTES];
            int len;
            while (true){
                len = in.read(buffer);
                data.clear();
                data.put("buffer",buffer);
                data.put("len",len);
                info = new Info(Common.UPLOAD_DOC.getName(),data);
                client.sendData(info);
                if(len==-1){
                    break;
                }
            }
            in.close();
            return (boolean) client.readData();
        }catch (Exception e){
            System.out.printf("{ client-msg:upload doc error, error:%s }\n",e.getMessage());
        }
        return false;
    }

    /**
     * TODO 下载档案
     *
     * @param targetPath 文件路径
     * @param id 档案编号
     * @param filename 文件名
     * @return boolean
     * @throws
     */
    public static boolean downloadDoc(String targetPath,String id,String filename){
        File targetFile = null;
        try {
            data.clear();
            data.put("id", id);
            data.put("filename",filename);
            Info info = new Info(Common.DOWNLOAD_DOC.getName(), data);
            client.sendData(info);

            int num = 1;
            // 防止重复命名覆盖已有文件
            while (true){
                String target = "%s\\%s".formatted(targetPath,filename);
                targetFile = new File(target);
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                    break;
                }else {
                    filename = addFilenameNum(filename,num);
                    num++;
                }
            }
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buffer;
            int len;
            String msg;
            while (true) {
                info = (Info) client.readData();
                msg = info.getMsg();
                data = (Hashtable<String, Object>) info.getData();
                if (!msg.equals(Common.DOWNLOAD_DOC.getName())) {
                    throw new Exception("received unknown msg %s, should be %s"
                            .formatted(msg,Common.DOWNLOAD_DOC.getName()));
                }
                buffer = (byte[]) data.get("buffer");
                len = (int) data.get("len");
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }
            out.close();
            return (boolean) client.readData();
        }catch (Exception e){
            try{
                targetFile.deleteOnExit();
            }catch (Exception ee){
                System.out.printf("{ client-msg:delete doc error, error:%s }\n",ee.getMessage());
            }
            System.out.printf("{ client-msg:download doc error, error:%s }\n",e.getMessage());
        }
        return false;
    }

    public static boolean deleteDoc(String id,String filename){
        try {
            data.clear();
            data.put("id", id);
            data.put("filename", filename);
            Info info = new Info(Common.DELETE_DOC.getName(), data);
            client.sendData(info);
            return (boolean) client.readData();
        }catch (Exception e){
            System.out.println("{}\n");
        }
        return false;
    }

    public static Vector<Vector<String>> listDocToVector() {
        Enumeration<Doc> e = listDoc().elements();
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
            data.clear();
            data.put("name",name);
            data.put("password",password);
            Info info = new Info(Common.VERIFY_USER.getName(), data);
            client.sendData(info);
            return (AbstractUser) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:verify user error, name:%s, password:%s, error:%s }\n", name, password, e.getMessage());
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
            data.clear();
            data.put("name",name);
            Info info = new Info(Common.SEARCH_USER.getName(), data);
            client.sendData(info);
            return (AbstractUser) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:search user error, name:%s, error:%s }\n", name, e.getMessage());
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
    public static Vector<AbstractUser> listUser() {
        Vector<AbstractUser> users = new Vector<>();
        try {
            data.clear();
            Info info = new Info(Common.LIST_USER.getName(), data);
            client.sendData(info);
            return (Vector<AbstractUser>) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:list user error, error:%s }\n", e.getMessage());
        }
        return users;
    }

    public static Vector<Vector<String>> listUserToVector() {
        Enumeration<AbstractUser> e = listUser().elements();
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
            data.clear();
            data.put("name",name);
            data.put("password",password);
            data.put("role",role);
            Info info = new Info(Common.UPDATE_USER.getName(), data);
            client.sendData(info);
            return (boolean) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:update user error, user:%s, error:%s }\n", AbstractUser.newUser(name, password, role), e.getMessage());
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
            data.clear();
            data.put("name",name);
            data.put("password",password);
            data.put("role",role);
            Info info = new Info(Common.INSERT_USER.getName(), data);
            client.sendData(info);
            return (boolean) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:insert user error, user:%s, error:%s }\n", AbstractUser.newUser(name,password,role), e.getMessage());
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
            data.clear();
            data.put("name",name);
            Info info = new Info(Common.DELETE_USER.getName(), data);
            client.sendData(info);
            return (boolean) client.readData();
        } catch (Exception e) {
            System.out.printf("{ client-msg:insert doc error, name:%s, error:%s }\n", name, e.getMessage());
        }
        return false;
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

    public static String addFilenameNum(String filename,int num){
        try {
            String[] strings = filename.split("\\.");
            String ext = strings[strings.length-1];
            return filename.replaceAll("(\\(\\d+\\))?\\."+ext+"$",
                    "(%d)\\.%s".formatted(num,ext));
        }catch (Exception e){
            e.printStackTrace();
        }
        return filename;
    }

    public static void main(String[] args) {

    }
}
