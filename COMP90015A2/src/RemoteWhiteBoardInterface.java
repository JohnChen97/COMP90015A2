import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteWhiteBoardInterface extends Remote {

    public void sendJsonString(String jsonString) throws RemoteException;
    public String receiveJsonString(String jsonString) throws RemoteException;
}
