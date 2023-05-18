public interface ServerCallback {
    void addMessage(String message);

    String retrieveMessage() throws InterruptedException;
}
