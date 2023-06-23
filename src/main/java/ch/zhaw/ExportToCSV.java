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

	public void run(String mongoUriProperty, String databaseName, String pathToCSV, String something, String association, String supertype) throws Exception {

        String mongoUri = mongoUriProperty;
            
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            
            // Get the Nodes
            ArrayList<String[]> nodes = getNodes(database, association, something);
            writeCSV(nodes, "Nodes.csv", pathToCSV);
            System.out.println("Nodes DONE");
                
            // Get the Edges
            ArrayList<String[]> edges = getEdges(database, association, something, supertype);
            writeCSV(edges, "Edges.csv", pathToCSV);
            System.out.println("Edges DONE");
        }
    }


    ///////////////////////////////////////////////////////////
    // Method to write a CSV
    //////////////////////////////////////////////////////////
    private static void writeCSV(ArrayList<String[]> csvData, String filename, String path) throws Exception {        
        CSVWriter writer = new CSVWriter(new FileWriter(path+filename));
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
    private static ArrayList<String[]> getNodes(MongoDatabase database, String collectionName, String something){      
        MongoCollection<Document> collection = database.getCollection(collectionName);
        
        ArrayList<Document> nodes = collection.aggregate(
            Arrays.asList(new Document("$group", 
            new Document("_id", "$"+something+".id")
                    .append("name", 
            new Document("$first", "$"+something+".name"))),
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
    private static ArrayList<String[]> getEdges(MongoDatabase database, String collectionName, String something, String supertype) {        
                
        MongoCollection<Document> collection = database.getCollection(collectionName);
        
        ArrayList<Document> edges = collection.aggregate(
            Arrays.asList(
                
            // Groups all Elements by the Supertype-ID and adds all SomethingIDs to an Array
            new Document("$group", 
                new Document("_id", "$"+supertype+".id")
                        .append("nodes", 
                new Document("$addToSet", "$"+something+".id"))),

                // Iterates over the Array and creates pairs (duplicates are excluded because it only iterates until two IDs are the same)
                new Document("$project", 
                    new Document("_id", "$_id")
                            .append("pairs", 
                    new Document("$reduce", 
                    new Document("input", "$nodes")
                            .append("initialValue", Arrays.asList())
                            .append("in", 
                    new Document("$concatArrays", Arrays.asList("$$value", 
                    new Document("$map", 
                    new Document("input", 
                    new Document("$slice", Arrays.asList("$nodes", 
                    new Document("$add", Arrays.asList(
                        new Document("$indexOfArray", Arrays.asList("$nodes", "$$this")), 1L)), 
                    new Document("$size", "$nodes"))))
                            .append("as", "otherNode")
                            .append("in", 
                    new Document("$cond", Arrays.asList(
                        new Document("$ne", Arrays.asList("$$this", "$$otherNode")), 
                    new Document("node1", "$$this")
                        .append("node2", "$$otherNode"), 
                    new BsonNull())))))))))), 
                
                // Unwinds the pairs, so there is a seperate document for every pair
                new Document("$unwind", "$pairs"), 
                
                // Projects all the relevant information directly to flatten the object; if you want, you can add the supertype-information too.
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


