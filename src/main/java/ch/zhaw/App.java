package ch.zhaw;

public class App 
{
    public static void main( String[] args ) throws Exception{

        String mongoUri = "";               // <-------------- adjust
        String database = "";               // <-------------- adjust

        String pathForCSV = "";             // <-------------- adjust (I would use the path to the Neo4j-Import-Folder)

        String something = "";              // <-------------- adjust
        String association = "";            // <-------------- adjust
        String supertype = "";              // <-------------- adjust

        String associationEndpoint = "/";   // <-------------- adjust

        ExportToMongoDB exportToMongoDB2 = new ExportToMongoDB();
        exportToMongoDB2.run(mongoUri, database, association, associationEndpoint);
        System.out.println("Export from REST API to MongoDB is finished");

        ExportToCSV exportToCSV2 = new ExportToCSV();
        exportToCSV2.run(mongoUri, database, pathForCSV, something, association, supertype);
        System.out.println("Export from MongoDB to CSV is finished");

    }
}
