import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class CreateWhiteBoard {
    public static void main(String[] args) {
        try {
            int port = 8000;
            String collectionName = "WhiteBoard1";
            RemoteWhiteBoardServer server = new RemoteWhiteBoardServer(port, collectionName);

            Thread serverThread = new Thread(() -> {
                try {
                    server.runServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();
            TimeUnit.SECONDS.sleep(3);
            try {
                String serverAddress = "localhost";
                Client client = new Client(serverAddress, port, true);
            } catch (Exception e) {
                System.out.println("Client failed to connect. Is the server running?");
                e.printStackTrace();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

