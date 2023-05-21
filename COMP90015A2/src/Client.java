import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class Client {

    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private LoginBoard loginBoard;
    private RemoteWhiteBoard remoteWhiteBoard;
    private String username;
    private boolean manager;

    public static void main(String[] args){
        String serverAddress = "localhost";
        int serverPort = 8000;
        Client client = new Client(serverAddress, serverPort, false);
    }

    public Client(String serverAddress, int serverPort, boolean manager) {
        try {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.socket = new Socket(this.serverAddress, this.serverPort);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.loginBoard = new LoginBoard(socket, in, out, manager);
//            this.remoteWhiteBoard = new RemoteWhiteBoard(socket, in, out);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void checkUsername(String username){

    }


}
