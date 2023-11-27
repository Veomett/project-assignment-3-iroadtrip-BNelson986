# CS 245 (Fall 2023) - Assignment 3 - IRoadTrip

#   Project Plan Layout:
    HASH MAP to store Contry Names and Codes <Key: Hash(name), Value: code>
        ex: {
                name: "United States",
                code: "USA",
                link: node <Country Object>
            } 

    Country Object
        ex: {
                name: "United States",
                code: "USA",
                start: "1816-01-01",
                end: "2020-12-31",
                ID: 2,
                neighbors:   [
                                {
                                    code: "CAN",
                                    link: node <JSON Object>,
                                    distToCap: 731,
                                },
                                {
                                    code: "MEX",
                                    link: node <JSON Object>,
                                    distToCap: 3024,
                                }
                            ]
            }
