This is a possible solution for the WP 4 for Data-Management-Class at ZHAW.

Since it is a difficult task and was not part of the lecture, I hope my solution will help.

In my solution, you create the Nodes as an Endpoint of 'Something' and for the Edges you need an Endpoint of 'Association'.

------------------------------------------------------------------------------------

You need to create a new MAVEN-Project (not Spring-Boot - like we had in Part 2 of the Data Management Course) in a seperate Folder than your Spring-Boot-Project.

And then you need the following additional dependencies in the pom-file:
    
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver</artifactId>
        <version>3.12.14</version>
    </dependency>


    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>


In the App.java you need to use your MongoDB-Connection-String.

In the ExportToMongoDB you need to ajust the different parts of the code to match your project.

------------------------------------------------------------------------------------

TODO:   - App.java: change mongoUri
        - App.java: change database
        - ExportToMongoDB: change Endpoint
        - ExportToCSV: change Nodes-pipeline
        - ExportToCSV: change Edges-pipeline

------------------------------------------------------------------------------------


GOOD LUCK!