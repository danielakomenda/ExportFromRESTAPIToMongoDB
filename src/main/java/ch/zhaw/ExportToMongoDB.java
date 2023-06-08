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


///////////////////////////////////////////////////////////////////////////
//  Needed Repository-Query in EntityRepository in the Spring-Boot-Project
///////////////////////////////////////////////////////////////////////////
/*
        @Query(value="SELECT something.* FROM something INNER JOIN association ON something.id=association.something_id WHERE association.supertype_id = ?1", nativeQuery = true)
        public List<Something> findSomethingsBySupertype(long id);
*/


/////////////////////////////////////////////////////////////
//  Needed RestController-Methods in the Spring-Boot-Project
/////////////////////////////////////////////////////////////
/*
	@RequestMapping(value = "/something", method = RequestMethod.GET)
	public ResponseEntity<List<Something>> getSomething() {
		List<Something> result = this.repository.findAll();

		if (!result.isEmpty()) {
			return new ResponseEntity<List<Something>>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Something>>(HttpStatus.NOT_FOUND);
		}
	}

    @RequestMapping(value = "/supertype/{id}/somethings", method = RequestMethod.GET)
	public ResponseEntity<List<Something>> getSomethingsFromSupertype(@PathVariable("id") long id) {
		List<Something> result = this.repository.findSomethingsBySupertype(id);

		if (!result.isEmpty()) {
			return new ResponseEntity<List<Something>>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Something>>(HttpStatus.NOT_FOUND);
		}
	}
 */


public class ExportToMongoDB {

	public void run(String mongoUriProperty) throws Exception {
        
        String mongoUri = mongoUriProperty;
            
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase("DATABASE"); // <----------- Change Database-Name
            
            // Get the Nodes
            getNodes(database);
            System.out.println("Nodes DONE");
            
            // Get the Edges
            getEdges(database);
            System.out.println("Edges DONE");
        }
    }


    ///////////////////////////////////////////////////////////
    // Method to get the Node and upload them to MongoDB
    //////////////////////////////////////////////////////////
    private static void getNodes(MongoDatabase database) throws Exception {      
       
        MongoCollection<Document> collection = database.getCollection("Nodes"); // <----------- Change Collection-Name
        Gson gson = new Gson(); // Gson-Instance

        JsonArray nodes = getRestCall("/something").getAsJsonArray(); // <----------- Define End-Point in the Rest-Controller
        
        // Load Node to MongoDB
        for (JsonElement node : nodes) {
            String resultJson = gson.toJson(node);
            Document resultDoc = Document.parse(resultJson);
            collection.insertOne(resultDoc);
        }
        System.out.println("Data export to MongoDB successful!");       
    }


    ///////////////////////////////////////////////////////////
    // Method to get the Edges and upload them to MongoDB
    //////////////////////////////////////////////////////////
    private static void getEdges(MongoDatabase database) throws Exception {        
        
        MongoCollection<Document> collection = database.getCollection("Edges");  // <----------- Change Collection-Name
        Gson gson = new Gson(); // Gson-Instance

        // Get the Supertype-IDs first to make the Edge-Call
        JsonArray supertypes = getRestCall("/supertype").getAsJsonArray(); // <----------- Define End-Point in the Rest-Controller
        for (JsonElement supertype : supertypes) {

            // JsonArray-Instance
            JsonArray edges;

            // Get all the Suptertype-IDs
            int id = supertype.getAsJsonObject().get("id").getAsInt();
            
            // Make the Call for List of Nodes in Supertype
            try {
                edges = getRestCall("/supertype/" + id + "/somethings").getAsJsonArray(); // <----------- Define End-Point in the Rest-Controller
            } catch (RuntimeException e) {
                continue;
            }
        
            // Get pairs of Edges
            for (int i=0; i<edges.size(); i++) {
                for (int j=0; j<i; j++){

                    // Convert Edge-Object from JsonArray to Json to Document
                    Document edge1 = Document.parse(gson.toJson(edges.get(i)));
                    Document edge2 = Document.parse(gson.toJson(edges.get(j)));
                    Document sharedSupertype = Document.parse(gson.toJson(supertype));

                    // Create the Result-Document
                    Document resultDoc = new Document("edge1", edge1).append("edge2", edge2).append("supertype", sharedSupertype);
                    
                    // Insert the Result-Document
                    collection.insertOne(resultDoc);
                }
            }
        }
    }



    ///////////////////////////////////////////////////////////
    // Method to make the REST-API-Call
    //////////////////////////////////////////////////////////
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