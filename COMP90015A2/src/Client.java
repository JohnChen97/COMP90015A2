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
    private RemoteWhiteBoard remoteWhiteBoard;

    public static void main(String[] args){
        String serverAddress = "localhost";
        int serverPort = 8000;
        Client client = new Client(serverAddress, serverPort);
    }

    public Client(String serverAddress, int serverPort) {
        try {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.socket = new Socket(this.serverAddress, this.serverPort);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.remoteWhiteBoard = new RemoteWhiteBoard(socket, in, out);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
