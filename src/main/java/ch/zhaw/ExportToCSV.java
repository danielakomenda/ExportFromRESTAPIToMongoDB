package ch.zhaw;

import java.io.FileWriter;

import org.bson.BsonNull;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVWriter;

import java.util.ArrayList;
import java.util.Arrays;


public class ExportToCSV {

	public void run(String mongoUriProperty, String databaseName) throws Exception {

        String mongoUri = mongoUriProperty;
            
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            
            // Get the Nodes
            ArrayList<String[]> nodes = getNodes(database);
            writeCSV(nodes, "Nodes.csv");
            System.out.println("Nodes DONE");
                
            // Get the Edges
            ArrayList<String[]> edges = getEdges(database);
            writeCSV(edges, "Edges.csv");
            System.out.println("Edges DONE");
        }
    }


    ///////////////////////////////////////////////////////////
    // Method to write a CSV
    //////////////////////////////////////////////////////////
    private static void writeCSV(ArrayList<String[]> csvData, String filename) throws Exception {        
        CSVWriter writer = new CSVWriter(new FileWriter(filename));
        try {
            writer.writeAll(csvData);
        }
        finally {
            writer.close();
        }
    }


    ///////////////////////////////////////////////////////////
    // Method to get the Nodes from MongoDB
    //////////////////////////////////////////////////////////
    private static ArrayList<String[]> getNodes(MongoDatabase database){      
        MongoCollection<Document> collection = database.getCollection("association");
        
        ArrayList<Document> nodes = collection.aggregate(
            Arrays.asList(new Document("$group", 
            new Document("_id", "$<something>.id")  // <-------------- anpassen
                    .append("name", 
            new Document("$first", "$<something>.name"))), // <-------------- anpassen
            new Document("$project", 
            new Document("_id", "$_id")
                    .append("name", "$name")))   
            ).into(new ArrayList<Document>());
        
        ArrayList<String[]> csvData = new ArrayList<>();
        String[] header = {"id", "name"};
        csvData.add(header);

        for (Document node: nodes){
            String[] data = {node.get("_id").toString(), node.get("name").toString()};
            csvData.add(data);
        }

        return csvData;
    }


    ///////////////////////////////////////////////////////////
    // Method to get the Edges from MongoDB
    //////////////////////////////////////////////////////////
    private static ArrayList<String[]> getEdges(MongoDatabase database) {        
                
        MongoCollection<Document> collection = database.getCollection("productions");
        
        ArrayList<Document> edges = collection.aggregate(
            Arrays.asList(new Document("$group", 
            new Document("_id", "$<suptertype>.id")  // <-------------- anpassen
                    .append("nodes", 
            new Document("$addToSet", "$<something>.id"))),  // <-------------- anpassen
            new Document("$project", 
            new Document("_id", 0L)
                    .append("pairs", 
            new Document("$reduce", 
            new Document("input", "$nodes")
                            .append("initialValue", Arrays.asList())
                            .append("in", 
            new Document("$concatArrays", Arrays.asList("$$value", 
                                    new Document("$map", 
                                    new Document("input", 
                                    new Document("$slice", Arrays.asList("$nodes", 
                                                    new Document("$add", Arrays.asList(new Document("$indexOfArray", Arrays.asList("$nodes", "$$this")), 1L)), 
                                                    new Document("$size", "$nodes"))))
                                            .append("as", "otherNode")
                                            .append("in", 
                                    new Document("$cond", Arrays.asList(new Document("$ne", Arrays.asList("$$this", "$$otherNode")), 
                                                    new Document("node1", "$$this")
                                                        .append("node2", "$$otherNode"), 
                                                    new BsonNull())))))))))), 
            new Document("$unwind", "$pairs"), 
            new Document("$project", 
            new Document("node1", "$pairs.node1")
                    .append("node2", "$pairs.node2")))
        ).into(new ArrayList<Document>());

        ArrayList<String[]> csvData = new ArrayList<>();
        String[] header = {"node1", "node2"};
        csvData.add(header);

        for (Document edge: edges){
            String[] data = {edge.get("node1").toString(), edge.get("node2").toString()};
            csvData.add(data);
        }

        return csvData;
    }
}


