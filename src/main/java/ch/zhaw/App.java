package ch.zhaw;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        String mongoUri = ""; // <---------- Your MongoDB-Connection-String

        ExportToMongoDB export = new ExportToMongoDB();
        export.run(mongoUri);
    }
}
