This is a possible solution for the WP 4 for Data-Management-Class at ZHAW.

Since it is a difficult task, I hope my solution will help.

In my solution, you create the Nodes and Edges through the End-Point "/association".

Please make sure, that your Association-Entity has @ManyToOne-Relationship-Annotation to Something and Supertype and that you have Getter-Methods to get the Something- and Supertype-Information via this End-Point.

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


------------------------------------------------------------------------------------

<h1>TODO:</h1>
- App.java: change mongoUri <br>
- App.java: change database <br>
- App.java: change pathForCSV (if you leave it empty, you will find the CSV in the root-folder of this project) <br>
- App.java: change something <br>
- App.java: change association <br>
- App.java: change supertype <br>
- App.java: change associationEndpoint <br>

------------------------------------------------------------------------------------


GOOD LUCK!