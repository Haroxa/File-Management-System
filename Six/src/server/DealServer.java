package server;

import share.Common;
import share.Info;
import share.console.AbstractUser;
import share.console.DataProcessing;
import share.console.Doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Vector;

/**
 * TODO 处理服务端接受的数据
 *
 * @author Haroxa
 * @date 2023/12/23
 */
public class DealServer {
    static int port = Common.SERVER_PORT;
    static Server server;
    static Hashtable<String, Object> data;

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        server = new Server(port);
        server.initServer();
        while (true) {
            server.waitForConnection();
            server.initStreams();
            deal();
            server.closeConnection();
            server.counter++;
        }
    }

    public static void deal() {
        Info info = null;
        while (true) {
            // 接受信息
            try {
                info = (Info) server.readData();
                String msg = info.getMsg();
                data = (Hashtable<String, Object>) info.getData();
                // 处理信息
                Object result = dealMsg(msg);
                // 返回结果
                server.sendData(result);
            } catch (Exception e) {
                System.out.printf("{ server-msg: found invalid info, err: %s }\n", e.getMessage());
            }
            if (info == null) {
                break;
            }
        }
    }

    public static Object dealMsg(String msg) {
        try {
            switch (Common.valueOf(msg.toUpperCase())) {
                case VERIFY_USER:
                    return verifyUser();
                case SEARCH_USER:
                    return searchUser();
                case INSERT_USER:
                    return insertUser();
                case UPDATE_USER:
                    return updateUser();
                case DELETE_USER:
                    return deleteUser();
                case LIST_USER:
                    return listUser();
                case INSERT_DOC:
                    return insertDoc();
                case SEARCH_DOC:
                    return searchDoc();
                case LIST_DOC:
                    return listDoc();
                case UPLOAD_DOC:
                    return uploadDoc();
                case DOWNLOAD_DOC:
                    return downloadDoc();
            }
        } catch (IllegalArgumentException e) {
            return "not found %s ".formatted(msg);
        }
        return msg;
    }

    /**
     * TODO 按档案编号搜索文档，返回null表明未找到
     *
     * @param
     * @return console.Doc
     * @throws
     */
    public static Doc searchDoc() {
        try {
            String id = (String) data.get("id");
            return DataProcessing.searchDoc(id);
        } catch (Exception e) {
            System.out.printf("{ server-msg:search doc error, error:%s }\n", e.getMessage());
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
            return DataProcessing.listDoc();
        } catch (Exception e) {
            System.out.printf("{ server-msg:list doc error, error:%s }\n", e.getMessage());
        }
        return vector;
    }

    /**
     * TODO 插入新的档案
     *
     * @param
     * @return boolean
     * @throws
     */
    public static boolean insertDoc() {
        try {
            String id = (String) data.get("id");
            String creator = (String) data.get("creator");
            Timestamp timestamp = (Timestamp) data.get("timestamp");
            String description = (String) data.get("description");
            String filename = (String) data.get("filename");
            return DataProcessing.insertDoc(id, creator, timestamp, description, filename);
        } catch (Exception e) {
            System.out.printf("{ server-msg:insert doc error, error:%s }\n", e.getMessage());
        }
        return false;
    }

    public static boolean uploadDoc() {
        File targetFile = null;
        try {
            String id = (String) data.get("id");
            String filename = (String) data.get("filename");
            String target = "%s\\%s-%s".formatted(AbstractUser.uploadPath, id, filename);
            targetFile = new File(target);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buffer;
            int len;
            Info info;
            String msg;
            Hashtable<String, Object> data;
            while (true) {
                info = (Info) server.readData();
                msg = info.getMsg();
                data = (Hashtable<String, Object>) info.getData();
                if (!msg.equals(Common.UPLOAD_DOC.getName())) {
                    throw new Exception("received unknown msg %s, should be %s"
                            .formatted(msg,Common.UPLOAD_DOC.getName()));
                }
                buffer = (byte[]) data.get("buffer");
                len = (int) data.get("len");
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }
            out.close();
            return true;
        } catch (Exception e) {
            try{
                targetFile.deleteOnExit();
            }catch (Exception ee){
                System.out.printf("{ server-msg:delete doc error, error:%s }\n",ee.getMessage());
            }
            System.out.printf("{ server-msg:upload doc error, error:%s }\n", e.getMessage());
        }
        return false;
    }

    public static boolean downloadDoc() {
        try {
            String id = (String) data.get("id");
            String filename = (String) data.get("filename");
            String source = "%s\\%s-%s".formatted(AbstractUser.uploadPath, id, filename);
            File sourceFile = new File(source);
            if (!sourceFile.exists()) {
                return false;
            }
            FileInputStream in = new FileInputStream(sourceFile);
            byte[] buffer = new byte[Common.MAX_BYTES];
            int len;
            Info info;
            while (true) {
                len = in.read(buffer);
                data.clear();
                data.put("buffer",buffer);
                data.put("len",len);
                info = new Info(Common.DOWNLOAD_DOC.getName(),data);
                server.sendData(info);
                if (len==-1){
                    break;
                }
            }
            in.close();
            return true;
        } catch (Exception e) {
            System.out.printf("{ server-msg:upload doc error, error:%s }\n", e.getMessage());
        }
        return false;
    }

    /**
     * TODO 验证用户密码
     *
     * @return console.AbstractUser
     */
    public static AbstractUser verifyUser() {
        try {
            String name = (String) data.get("name");
            String password = (String) data.get("password");
            return DataProcessing.verifyUser(name, password);
        } catch (Exception e) {
            System.out.printf("{ server-msg:verify user error, error:%s }\n", e.getMessage());
        }
        return null;
    }

    /**
     * TODO 按用户名搜索用户，返回null表明未找到符合条件的用户
     *
     * @param
     * @return console.AbstractUser
     * @throws
     */
    public static AbstractUser searchUser() {
        try {
            String name = (String) data.get("name");
            return DataProcessing.searchUser(name);
        } catch (Exception e) {
            System.out.printf("{ server-msg:search user error, error:%s }\n", e.getMessage());
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
            return DataProcessing.listUser();
        } catch (Exception e) {
            System.out.printf("{ server-msg:list user error, error:%s }\n", e.getMessage());
        }
        return users;
    }


    /**
     * TODO 修改用户信息
     *
     * @param
     * @return boolean
     * @throws
     */
    public static boolean updateUser() {
        try {
            String name = (String) data.get("name");
            String password = (String) data.get("password");
            String role = (String) data.get("role");
            return DataProcessing.updateUser(name, password, role);
        } catch (Exception e) {
            System.out.printf("{ server-msg:update user error, error:%s }\n", e.getMessage());
        }
        return false;
    }

    /**
     * TODO 插入新用户
     *
     * @param
     * @return boolean
     * @throws
     */
    public static boolean insertUser() {
        try {
            String name = (String) data.get("name");
            String password = (String) data.get("password");
            String role = (String) data.get("role");
            return DataProcessing.insertUser(name, password, role);
        } catch (Exception e) {
            System.out.printf("{ server-msg:insert user error, error:%s }\n", e.getMessage());
        }
        return false;
    }

    /**
     * TODO 删除指定用户
     *
     * @param
     * @return boolean
     * @throws
     */
    public static boolean deleteUser() {
        try {
            String name = (String) data.get("name");
            return DataProcessing.deleteUser(name);
        } catch (Exception e) {
            System.out.printf("{ server-msg:insert doc error, error:%s }\n", e.getMessage());
        }
        return false;
    }

}
