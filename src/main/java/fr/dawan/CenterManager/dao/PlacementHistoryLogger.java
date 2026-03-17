package fr.dawan.CenterManager.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Date;

public class PlacementHistoryLogger {
    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public PlacementHistoryLogger() {
        client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = client.getDatabase("centerManager");
        collection = db.getCollection("placement_history");
    }

    public void logPlacement(String elementName, String zoneName) {
        Document doc = new Document()
                .append("element", elementName)
                .append("zone", zoneName)
                .append("timestamp", new Date());
        collection.insertOne(doc);
    }
}