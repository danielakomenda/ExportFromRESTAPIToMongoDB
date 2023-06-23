## Nodes-Pipeline

[
  {
    $group: {
      _id: "$something.id",
      name: {
        $first: "$something.name",
      },
    },
  },
  {
    $project: {
      _id: "$_id",
      name: "$name"
    },
  },
]




## Edges-Pipeline

[
  {
    $group: {
      _id: "$supertype.id",  <!-- Change this -->
      nodes: {
        $addToSet: "$something.id", <!-- Change this -->
      },
    },
  },
  {
    $project: {
      _id: "$_id",
      pairs: {
        $reduce: {
          input: "$nodes",
          initialValue: [],
          in: {
            $concatArrays: [
              "$$value",
              {
                $map: {
                  input: {
                    $slice: [
                      "$nodes",
                      {
                        $add: [
                          {
                            $indexOfArray: [
                              "$nodes",
                              "$$this",
                            ],
                          },
                          1,
                        ],
                      },
                      {
                        $size: "$nodes",
                      },
                    ],
                  },
                  as: "otherNode",
                  in: {
                    $cond: [
                      {
                        $ne: [
                          "$$this",
                          "$$otherNode",
                        ],
                      },
                      {
                        node1: "$$this",
                        node2: "$$otherNode",
                      },
                      null,
                    ],
                  },
                },
              },
            ],
          },
        },
      },
    },
  },
  {
    $unwind: "$pairs",
  },
  {
    $project: {
      node1: "$pairs.node1",
      node2: "$pairs.node2",
    },
  },
]