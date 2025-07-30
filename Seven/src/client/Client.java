package client;

import share.Info;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * TODO 客户端
 *
 * @author Haroxa
 * @date 2023/12/22
 */
public class Client {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Scanner scanner;
    private final int serverPort;
    private final String chatServer;
    private Socket client;
    private boolean retry = true;

    public Client(String host,int port) {
        chatServer = host;
        serverPort = port;
    }

    public void run() {
        connect();
        processConnection();
        closeConnection();
    }
    public void connect(){
        if( client!=null ){
            closeConnection();
        }
        connectToServer();
        initStreams();
    }
    public void connectToServer(){
        System.out.print("{ msg: attempt to connect server }\n");
        try{
            client = new Socket(InetAddress.getByName(chatServer), serverPort);
        }catch (Exception e){
            System.out.printf("{ msg: failed to connect to server, err:%s }\n",e.getMessage());
        }
        System.out.printf("{ msg: connected to %s }\n", client.getInetAddress().getHostName() );
    }

    public void initStreams(){
        try{
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush();
            input = new ObjectInputStream(client.getInputStream());
            scanner = new Scanner(System.in);
        } catch (Exception e){
            System.out.printf("{ msg: failed to init streams, err:%s }\n",e.getMessage());
        }
        System.out.print("{ msg: init streams successfully }\n");
    }

    public void processConnection(){
        String message = "";
        do {
            try {
                message = scanner.nextLine();
                Info info = new Info(message);
                sendData(info);
            } catch (Exception e){
                System.out.printf("{ msg: failed to process data, err: %s }\n",e.getMessage());
                return;
            }
        } while (!message.equals("SERVER>>> TERMINATE"));
    }

    public void closeConnection() {
        System.out.println("Closing connection");
        try {
            output.close();
            input.close();
            scanner.close();
            client.close();
        } catch (Exception e) {
            System.out.printf("{ msg: failed to close connection, err:%s }\n",e.getMessage());
        }
    }

    public Object readData(){
        Object data = null;
        try {
            data = input.readObject();
            System.out.printf("SERVER>>> data: %s\n",data);
        } catch (Exception e) {
            System.out.printf("{ msg: failed to read data, err: %s }\n",e.getMessage());
        }
        return data;
    }

    public void sendData(Object info) {
        try {
            // 重置输出流，防止读取重复内容
            output.reset();
            output.writeObject(info);
            output.flush();
            System.out.println("CLIENT>>> " + info);
        } catch (IOException e) {
            System.out.printf("{ msg:start to reconnect server, err:%s }\n",e.getMessage());
            if(retry){
                retry = false;
                connect();
                sendData(info);
                retry = true;
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1",12345);
        client.run();
    }
}