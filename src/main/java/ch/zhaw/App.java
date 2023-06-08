package ch.zhaw;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        String mongoUri = "mongodb+srv://dkomenda:nybmiZ-xafsod-deqbe0@cluster0.1xqx8hx.mongodb.net/Abschlussprojekt";

        ExportTest test = new ExportTest();
        test.run(mongoUri);
    }
}
