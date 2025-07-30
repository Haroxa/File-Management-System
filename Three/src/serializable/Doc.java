package serializable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * @FileName console.Doc
 * @Description
 * @Author Haroxa
 * @date 2023-09-09
 **/
public class Doc implements Base,Serializable {
    private String id,creator,description,filename;
    private Timestamp timestamp;
    static final int ID_LEN = 4;
    static final int DES_MIN_LEN = 2 , DES_MAX_LEN = 1024;
    Doc(String id,String creator,Timestamp timestamp,String description,String filename){
        this.id = id; this.creator = creator;
        this.timestamp = timestamp; this.description=description;
        this.filename = filename;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getCreator(){
        return creator;
    }
    public void setCreator(String creator){
        this.creator=creator;
    }
    public Timestamp getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp){
        this.timestamp=timestamp;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public String getFilename(){
        return filename;
    }
    public void setFilename(String filename){
        this.filename=filename;
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        // 自定义序列化，以可读的文本形式写入文件
        out.defaultWriteObject();
        out.writeUTF(description);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // 自定义反序列化，从可读的文本形式读取文件内容
        in.defaultReadObject();
        description = in.readUTF();
    }
    @Override
    public String getKey(){
        return id;
    }
    @Override
    public String toString() {
        return "Doc{" +
                "id:" + id +
                ", creator:" + creator +
                ", description:" + description +
                ", filename:" + filename +
                ", timestamp:" + timestamp +
                '}';
    }

    /**
     * TODO 检测档案编号
     *
     * @param id 档案编号
     * @return boolean
     * @throws
     */
    public static boolean checkId(String id) {
        return DataProcessing.checkStrPatternAndLen(id, "[0-9]+", ID_LEN, ID_LEN);
    }
    /**
     * TODO 检测档案描述
     *
     * @param description  档案描述
     * @return boolean
     * @throws
     */
    public static boolean checkDescription(String description){
        return DataProcessing.checkStrPatternAndLen(description,".*",DES_MIN_LEN,DES_MAX_LEN);
    }

    /**
     * TODO 输入档案编号，满足指定字符和长度
     *
     * @param
     * @return java.lang.String
     * @throws
     */
    public static String inputId(){
        System.out.print("请输入档案编号：");
        return DataProcessing.inputStr(Doc::checkId);
    }

    /**
     * TODO 输入档案描述，满足指定字符和长度
     *
     * @param
     * @return java.lang.String
     * @throws
     */
    public static String inputDescription(){
        System.out.print("请输入文件描述：");
        return DataProcessing.inputStr(Doc::checkDescription);
    }
    /**
     * TODO 列表展示时，显示格式化列名
     *
     * @param
     * @return void
     * @throws
     */
    public static void listShowColumns(){
        System.out.printf("%10s %10s %30s %15s \t\t%s\n",
                "Id","Creator","Timestamp","Filename","Description");
    }
    /**
     * TODO 列表展示时，显示格式化信息
     *
     * @param
     * @return void
     * @throws
     */
    public void listShow(){
        System.out.printf("%10s %10s %30s %15s \t\t%s\n",
                id,creator,timestamp, filename,description);
    }

}
