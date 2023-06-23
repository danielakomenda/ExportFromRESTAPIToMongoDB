## Create Nodes
```Cypher
LOAD CSV WITH HEADERS FROM 'file:///Nodes.csv' AS row
WITH 
    toInteger(row.id) as nodeID, 
    row.name as nodeName
MERGE (n:Node {nodeID: nodeD})
SET 
    n.nodeID = nodeID, 
    n.nodeName = nodeName, 
RETURN count(n)
```


## Create Edges
```Cypher
LOAD CSV WITH HEADERS FROM "file:///Edges.csv" AS row
WITH toInteger(row.node1) AS node1,
     toInteger(row.node2) AS node2
MATCH (n1:Node {nodeID: node1})
MATCH (n2:Node {nodeID: node2})
MERGE (n1) - [rel:BELONGS_TO}] - (n2)
RETURN count(rel)
```


## Create Graph
```Cypher
CALL gds.graph.project(
  'myGraph',
  'Node',
  {
    COLLABORATED_IN:
    {
      orientation: 'UNDIRECTED'
    }
  }
)
```


## Calculate Degree Centrality
```Cypher
CALL gds.degree.stream('myGraph')
YIELD nodeId, score
RETURN 
    gds.util.asNode(nodeId).nodeID AS id, 
    gds.util.asNode(nodeId).nodeName AS name, 
    score AS belongsTo
ORDER BY belongsTo DESC, name DESC
```