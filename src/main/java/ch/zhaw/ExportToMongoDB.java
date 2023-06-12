package ch.zhaw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class ExportToMongoDB {
    
	public void run(String mongoUriProperty, String databaseName) throws Exception {
       
        String mongoUri = mongoUriProperty;
            
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            
            exportData(database, "association", "/productions"); // <-------------- anpassen

        }
    }


    ///////////////////////////////////////////////////////////////////
    // Method to get the Data from REST API and upload them to MongoDB
    ///////////////////////////////////////////////////////////////////
    private static void exportData(MongoDatabase database, String collectionname, String endpoint) throws Exception {      
       
        MongoCollection<Document> collection = database.getCollection(collectionname);
        Gson gson = new Gson();

        int count = 0;

        JsonArray results = getRestCall(endpoint).getAsJsonArray();
        
        for (JsonElement result : results) {
            String resultJson = gson.toJson(result);
            Document resultDoc = Document.parse(resultJson);
            collection.insertOne(resultDoc);
            count++;
        }
        System.out.println(count +" " +collectionname +" exported to MongoDB successfully!");       
    }


    ////////////////////////////////////
    // Method to make the REST-API-Call
    ////////////////////////////////////
	private static JsonElement getRestCall(String url) throws Exception {
		URL obj = new URL("http://localhost:8080" + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		
        if (responseCode == HttpURLConnection.HTTP_OK) {            
            String result = new BufferedReader(new InputStreamReader(con.getInputStream()))
            .lines().collect(Collectors.joining("\n"));
            final JsonElement doc = JsonParser.parseString(result);
			return doc;
		} else {
			throw new RuntimeException("Connection failed");
		}
	}
}
