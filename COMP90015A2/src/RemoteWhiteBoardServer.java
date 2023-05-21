import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the server class.
 */
public class RemoteWhiteBoardServer {


    private int port;

    private ArrayList<RemoteWhiteBoardThread> threads = new ArrayList<>();

    private Canvas canvas;
    private String collectionName;
    private RemoteWhiteBoardMongoDB remoteWhiteBoardMongoDB;

    /**
     * This is the main function to start the server.
     * @param args
     */
    public static void main(String[] args) {
        try {
//        int port = Integer.parseInt(args[0]);
//        String collectionName = args[1];
            int port = 8000;
            String collectionName = "WhiteBoard1";
            RemoteWhiteBoardServer server = new RemoteWhiteBoardServer(port, collectionName);
            server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Constructor to setup the server.
     * @param port
     */
    public RemoteWhiteBoardServer(int port, String collectionName) throws IOException {
        this.port = port;
        this.collectionName = collectionName;
        this.remoteWhiteBoardMongoDB = new RemoteWhiteBoardMongoDB("jionghao", "Guange1997", "comp90015a2.2pcja08.mongodb.net", "COMP90015A2");
        this.canvas = new Canvas();


    }

    public void runServer() {

        ServerSocket serverSocket = null;
        try {

            serverSocket = new ServerSocket(this.port);

            while (true) {
                try {

                    Socket socket = serverSocket.accept();
                    RemoteWhiteBoardThread clientThread = new RemoteWhiteBoardThread(socket, this.remoteWhiteBoardMongoDB, this.collectionName, this.canvas,  this.threads);
                    this.threads.add(clientThread);
                    System.out.println("A new client is connected : " + socket);
                    clientThread.start();



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
