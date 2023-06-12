package ch.zhaw;

public class App 
{
    public static void main( String[] args ) throws Exception{

        String mongoUri = "";  // <-------------- anpassen
        String database = "";  // <-------------- anpassen

        ExportToMongoDB exportToMongoDB2 = new ExportToMongoDB();
        exportToMongoDB2.run(mongoUri, database);
        System.out.println("Export Version 2 from REST API to MongoDB is finished");

        ExportToCSV exportToCSV2 = new ExportToCSV();
        exportToCSV2.run(mongoUri, database);
        System.out.println("Export Version 2 from MongoDB to CSV is finished");

    }
}
