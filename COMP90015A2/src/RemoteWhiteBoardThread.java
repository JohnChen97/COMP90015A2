import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class RemoteWhiteBoardThread extends Thread {
    private String collectionName;
    private JSONObject canvasJSONObject;
    private Canvas canvas;
    private RemoteWhiteBoardMongoDB remoteWhiteBoardMongoDB;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private ArrayList<RemoteWhiteBoardThread> threads;
    private String username;


    public RemoteWhiteBoardThread(Socket socket, RemoteWhiteBoardMongoDB remoteWhiteBoardMongoDB, String collectionName, Canvas canvas, ArrayList<RemoteWhiteBoardThread> threads) throws IOException {
        super();
        this.setSocket(socket);
        this.canvas = canvas;
        this.threads = threads;
        this.in = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());
        this.setRemoteWhiteBoardMongoDB(remoteWhiteBoardMongoDB);
        this.collectionName = collectionName;

    }

    public RemoteWhiteBoardMongoDB getRemoteWhiteBoardMongoDB() {
        return remoteWhiteBoardMongoDB;
    }

    public void setRemoteWhiteBoardMongoDB(RemoteWhiteBoardMongoDB remoteWhiteBoardMongoDB) {
        this.remoteWhiteBoardMongoDB = remoteWhiteBoardMongoDB;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void handleIncomingJsonString(String bigJsonString) {
        try {
            JSONObject bigJsonObject = new JSONObject(bigJsonString);
            if (bigJsonObject.has("data")) {
                JSONArray RecordStringList = bigJsonObject.getJSONArray("data");


                for (RemoteWhiteBoardThread thread : threads) {
                    if (thread != this) {
                        System.out.println("Sending to " + thread.getSocket().getInetAddress());
                        thread.getOut().writeUTF(bigJsonString);
                    }
                }


            } else{
                if (bigJsonObject.has("type")) {
                    String type = bigJsonObject.getString("type");
                    if (type.equals("text")){
                        for (RemoteWhiteBoardThread thread : threads) {
                            if (thread != this) {
                                System.out.println("Sending to " + thread.getSocket().getInetAddress());
                                thread.getOut().writeUTF(bigJsonString);
                            }
                        }
                    } else if (type.equals("chat")){
                        for (RemoteWhiteBoardThread thread : threads) {
                            if (thread != this) {
                                System.out.println("Sending to " + thread.getSocket().getInetAddress());
                                thread.getOut().writeUTF(bigJsonString);
                            }
                        }
                    } else if (type.equals("UsernamePassword")){
                        String username = bigJsonObject.getString("username");
                        String password = bigJsonObject.getString("password");
                        Boolean result =  this.remoteWhiteBoardMongoDB.processUsername(username, password);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("status", result);
                        this.out.writeUTF(jsonObject.toString());
                        if (result){
                            this.canvas.addClient(username);
                            this.username = username;
                            for (RemoteWhiteBoardThread thread : threads) {
                                if (thread != this) {
                                    System.out.println("Sending to " + thread.getSocket().getInetAddress());
                                    JSONObject jsonObjectToManager = new JSONObject();
                                    jsonObjectToManager.put("type", "addClient");
                                    jsonObjectToManager.put("action", "add");
                                    jsonObjectToManager.put("username", username);
                                    thread.getOut().writeUTF(jsonObjectToManager.toString());
                                }
                            }
                        }


                    } else if (type.equals("kickout")){
                        String username = bigJsonObject.getString("username");
                        for (RemoteWhiteBoardThread thread : threads) {
                            if (thread.username.equals(username)) {
                                System.out.println("Sending to " + thread.getSocket().getInetAddress());
                                this.canvas.removeClient(username);
                                thread.getOut().writeUTF(bigJsonString);
                                this.socket.close();
                                this.threads.remove(this);
                            }
                        }

                    }

                }

            }
            } catch(JSONException e){
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();

            }
}



    public void eventAddShape(String jsonString) {
        try {

            ShapeRecord shapeRecord = new ShapeRecord(jsonString);
            //this.canvas.putShapeRecord(shapeRecord);

        } catch (NullPointerException e) {
            System.out.println("Something wrong in eventAddShape: " + e.toString());


        } catch (Exception e) {
            System.out.println("Something wrong in eventAddShape: " + e.toString());


        }
    }
    @Override
    public void run() {
        try {
            while(this.socket.isConnected()) {
                // Instead of reading from the client socket, retrieve message from queue

                // Handle client's message
                if (this.in.available() > 0) {
                    String userInputMessage = this.in.readUTF();
                    System.out.println("A client sent: " + userInputMessage);
                    this.handleIncomingJsonString(userInputMessage);
//                    String resultJsonString = this.handleIncomingJsonString(userInputMessage);
//                    this.out.writeUTF(resultJsonString);
                    if (!new JSONObject(userInputMessage).has("data")) {
                        if (new JSONObject(userInputMessage).getString("action").equals("exit")) {
                            System.out.println("Client " + this.socket.getInetAddress() + " has disconnected.");
                            this.socket.close();
                            break;
                        }
                    }
                } else {
                    Thread.sleep(100); // If no data is available in the stream, sleep for a bit to prevent CPU overload.
                }
            }
        } catch (UTFDataFormatException e){
            System.out.println("UTFDataFormatException: " + e.toString());
        } catch(SocketException e) {
            System.out.println("Socket closed.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            System.out.println("User input message is a null string.");
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted while waiting for a new message.");
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                System.out.println("Socket is a null string.");
            } catch (Exception e){
                System.out.println("Something wrong in finally in RemoteWhiteBoard Client thread: " + e.toString());
            }
        }
    }

}
