package serializable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Base64;

public class SerializableDemo {
    public static void main(String[] args) {
        demo3();
    }

    public static void demo1() {
        try {
            File file = new File("Three/doc.txt");
            ObjectOutputStream out  = new ObjectOutputStream( new FileOutputStream(file) );

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Doc doc = new Doc("0001","jack",timestamp,"Doc Source Java","Doc.java");
            out.writeObject(doc);
            doc = new Doc("0002","jack",timestamp,"Doc Source Java","Doc.java");
            out.writeObject(doc);
            out.close();

            ObjectInputStream in = new ObjectInputStream( new FileInputStream(file) );
            System.out.println(in.available());
            while(in.available()==0){
                Doc newDoc = (Doc)in.readObject();
                System.out.println(newDoc);
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void demo3() {
        File file = new File("P:\\CODE\\Java\\IdeaProjects\\Whut\\File Management System\\_data\\users.txt");
        try (ObjectInputStream in = new ObjectInputStream( new FileInputStream(file) ) ){
            while(true){
                AbstractUser newDoc = (AbstractUser) in.readObject();
                System.out.println(newDoc);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void demo2(){
        try {
            // Writing the object to a file
            Doc doc = new Doc("0001", "jack", new Timestamp(System.currentTimeMillis()), "Doc Source Java", "Doc.java");
            byte[] bytes = serialize(doc);
            Path path = Paths.get("Three/doc.txt");
            Files.write(path, Base64.getEncoder().encode(bytes));

            // Reading the object from a file
            byte[] fileBytes = Files.readAllBytes(path);
            Doc newDoc = deserialize(Base64.getDecoder().decode(fileBytes));
            System.out.println(newDoc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Serialize an object to bytes
    private static byte[] serialize(Doc doc) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(doc);
            return bos.toByteArray();
        }
    }

    // Deserialize an object from bytes
    private static Doc deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return (Doc) in.readObject();
        }
    }
}
