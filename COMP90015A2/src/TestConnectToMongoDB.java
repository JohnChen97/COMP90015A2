import org.bson.Document;

public class TestConnectToMongoDB {



    public static void main(String[] args) {
        try {
            RemoteWhiteBoardMongoDB remoteWhiteBoardMongoDB = new RemoteWhiteBoardMongoDB("jionghao", "Guange1997", "comp90015a2.2pcja08.mongodb.net", "COMP90015A2");
            String jsonString = "{\"shape\":\"Oval\",\"color\":\"java.awt.Color[r=0,g=0,b=0]\",\"size\":12,\"x\":417,\"width\":12,\"y\":176,\"action\":\"new_shape\",\"height\":12}";
            Document document = Document.parse(jsonString);

            remoteWhiteBoardMongoDB.insertDocument("COMP90015A2", "testShape", document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
