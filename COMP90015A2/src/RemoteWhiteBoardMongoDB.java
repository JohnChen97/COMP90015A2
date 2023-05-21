import com.mongodb.client.*;
import org.bson.Document;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class RemoteWhiteBoardMongoDB {

    private String username;
    private String password;
    private String hostname;

    private MongoClient mongoClient;
    private String databaseName;

    public RemoteWhiteBoardMongoDB(String username, String password, String hostname, String databaseName) {
        try {
            this.username = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            this.password = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
            this.hostname = hostname;
            this.databaseName = databaseName;
            this.mongoClient = MongoClients.create("mongodb+srv://" + this.username + ":" + this.password + "@" + this.hostname);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding is not supported", e);
        }
    }

    public MongoDatabase getDatabase(String databaseName){
        try {
            return this.mongoClient.getDatabase(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MongoCollection<Document> getCollection(String collectionName){
        try {
            return getDatabase(this.databaseName).getCollection(collectionName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getDocumentField(String collectionName, String key){
        try {
            return getCollection(collectionName).find().first().get(key);
        } catch (Exception e) {
            e.printStackTrace();
            Document document = new Document();
            document.append("error", "Get document field failed");
            return document;
        }
    }

    public String insertDocument(String collectionName, String key, Document value){
        try{
            Document document = new Document(key, value).append("time", new Date());
            getCollection(collectionName).insertOne(document);
            return "success";
        } catch (Exception e){
            e.printStackTrace();
            return "failed";
        }

    }

    public String updateDocument(String collectionName, String key, Document value) {
        try {
            getCollection(collectionName).updateOne(new Document(key, value), new Document("$set", new Document(key, value)));
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    public String deleteDocument(String collectionName, String key, Document value){
        try {
            getCollection(collectionName).deleteOne(new Document(key, value));
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    public void saveJsonFileAsDoc(String collectionName, JSONObject jsonObject){
        try {
            Document document = Document.parse(jsonObject.toString());
            getCollection(collectionName).insertOne(document);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadDocAsJsonFile(String collectionName){
        try {
            Document document = getCollection(collectionName).find().first();
            JSONObject jsonObject = new JSONObject(document.toJson());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean processUsername(String username, String inputPassword){
        try {
            MongoCollection<Document> collection = getCollection("UsernamePassword");
            Document filter = new Document("document_name", "UsernameAndPassword");
            Document userInfoDoc = collection.find(filter).first();
            JSONObject userInfoJson = new JSONObject(userInfoDoc.toJson());
            if (userInfoJson.has(username)){
                String password = userInfoJson.getString(username);
                Boolean verificationResult =  this.verifyPassword(password, inputPassword);
                return verificationResult;
            } else{
                Document update = new Document("$set", new Document(username, inputPassword));
                collection.updateOne(filter, update);
            }
           return true;
        } catch (Exception e) {
            throw new RuntimeException("UTF-8 encoding is not supported", e);

        }
    }

    public boolean verifyPassword(String password, String inputPassword){
        if (password.equals(inputPassword)){
            return true;
        } else {
            return false;
        }
    }
}

