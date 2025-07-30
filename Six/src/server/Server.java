package server;

import share.Info;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * TODO 服务端
 *
 * @author Haroxa
 * @date 2023/12/22
 */
public class Server {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    public int counter = 1;
    private final int port;
    public Server(int port){
        this.port = port;
    }
    public void initServer(){
        try {
            server = new ServerSocket(port, 100);
        }catch (Exception e) {
            System.out.printf("{ server-msg: failed to init server, err:%s }\n",e.getMessage());
        }
    }
    public void run() {
        initServer();
        while (true) {
            waitForConnection();
            initStreams();
            processConnection();
            closeConnection();
            counter++;
        }
    }

    public void waitForConnection(){
        System.out.printf("{ server-msg: waiting for connection }\n");
        try{
            connection = server.accept();
        }catch (Exception e){
            System.out.printf("{ server-msg: failed to wait for connection, err:%s }\n",e.getMessage());
        }
        System.out.printf("{ server-msg: connection %d received from %s }\n",
                counter, connection.getInetAddress().getHostName());
    }

    public void initStreams(){
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
        } catch (Exception e){
            System.out.printf("{ server-msg: failed to init streams, err:%s }\n",e.getMessage());
        }
        System.out.printf("{ server-msg: init streams successfully }\n");
    }

    public void processConnection(){
        Info info;
        String msg;
        Hashtable<String,Object> data;
        do {
            try {
                info = (Info) input.readObject();
                msg = info.getMsg();
                data = (Hashtable<String, Object>) info.getData();
                System.out.printf("CLIENT %d>>> server-msg: %s, data: %s\n",counter,msg,data);
            } catch (Exception e){
                System.out.printf("{ server-msg: failed to process data, err: %s }\n",e.getMessage());
                return;
            }
        } while (!msg.equals("CLIENT>>> TERMINATE"));
    }

    public void closeConnection() {
        System.out.println("Closing connection");
        try {
            output.close();
            input.close();
            connection.close();
        } catch (Exception e) {
            System.out.printf("{ server-msg: failed to close connection, err:%s }\n",e.getMessage());
        }
    }
    public Object readData(){
        Object data = null;
        try {
            data = input.readObject();
            System.out.printf("CLIENT %d>>> data: %s\n",counter,data);
        } catch (Exception e) {
            System.out.printf("{ server-msg: failed to read data, err: %s }\n",e.getMessage());
        }
        return data;
    }
    public void sendData(Object data) {
        try {
            output.reset();
            output.writeObject(data);
            output.flush();
            System.out.println("SERVER>>> " + data);
        } catch (IOException ioException) {
            System.out.println("Error writing object");
        }
    }

    public static void main(String[] args) {
        Server server = new Server(12345);
        server.run();
    }
}