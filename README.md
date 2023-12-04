# CS 245 (Fall 2023) - Assignment 3 - IRoadTrip

#   Program Description
    <div align="justify">
    Determines the optimal route between two countries. Once route is determined, stores the path of every jump, 
    or edge, required to reach the specified destination. Presents the user with a print out of each jump with
    the following format:
    * ${country1} -> ${country2} (${distance from 1 to 2} km)
    </div>
#   Compiling and Running

###  Compiling

    1) Run: gh repo clone Veomett/project-assignment-3-iroadtrip-BNelson986
    2) Run: javac IRoadTrip.java Country.java Countries.java PathFinder.java

###  Run
    1) Run: java IRoadTrip ${fileNames} "Files are included in the repository for the names"
    

#   Design Choices

###  Singleton Pattern
    
    Used "Lazy Implementation" method for ease of implementation and is thread-safe. 
    This allows the program to instantiate only one set of Dictionaries needed for execution.

### Country Codes
    
    Used to store the keys for the main "countries" dictionary. Takes country name and returns 
    unique 3-Letter code that represents the country. This allows the user to input a country name
    instead of the countries' codes and reduces conflicting key/value pairs.

### Countries

    Uses unique 3-Letter code, stored in countryCodes, as the key to access individual countries.
    Each entry contains a List of all neighboring countries and the distances between their capitals.

### Path Finder
    
    Uses Dijkstra's Algorithm to calculate the shortest path between 2 countries. Used OpenAI's ChatGPT 3.5
    to learn in psuedocode how to build a path saving algorithm.

#   UML Breakdown

###  IRoadTrip
    +   <<create>> IRoadTrip (String [])
    =============================================================================================================
    -   <<static, final>> scan : Scanner
    -   <<static>> reader : BufferedReader
    -   <<static, final>> map : Countries (Instance of Countries)
    -   <<static, final>> sdf : SimpleDateFormat
    -   <<static, final>> knownFiles : Dictionary<String, String>
    =============================================================================================================
    +   acceptUserInput() : void
    +   findPath(String, String) : List<String>
    +   getDistance(String, String) : int
    +   main(String []) : void
    +   readBorders(String) : List<Dictionary<String, List<String>>
    +   readCapDistance(String) : List<Dictionary<String, String>>
    +   readStateNames(String) : void
    +   setBorders(List<Dictionary<String, List<String>>>, List<Dictionary<String, String>>) : void
    -   biSearchCapDist(List<Dictionary<String, String>>, int, int, String, String) : Dictionary<String, String>

###  Country
    +   <<create>> Country()
    ======================================
    -   ID : int
    -   name : String
    -   code : String
    -   <<final>> neighbors : List<Neighbor>    
    ======================================
    +   addNeighbor(String, int) : void
    +   getCode() : String
    +   getName() : String
    +   getNeighborDist(String) : int
    +   getNeighbors() : List<Neighbor>
    +   setCode(String) : void
    +   setID(int) : void
    +   setName(String) : void

##### Country.Neighbor
    +   <<create>> Neighbor(String, int) : void
    =============================================
    -   <<final>> distToCap : int
    -   <<final>> name : String
    =============================================
    +   getDistToCap() : int
    +   getName() : int

###  Countries
    -   <<static, volitile>> instance : Countries
    +   <<final>> countries : HashMap<String, Country>
    +   <<final>> countryCodes : HashMap<String, String>
    ============================================
    +   addCountryInfo(String) : void
    +   findCountry(String) : Country
    +   <<static>> getInstance() : Countries
    
###  PathFinder
    +   <<create>> PathFinder()
    =====================================================
    -   <<final>> distance : Dictionay<String, Integer>
    -   <<final>> map : Countries (instance)
    -   <<final>> nodes : PriorityQueue<Node>
    -   <<final>> parent : Dictionary<String, String>
    -   <<final>> visited : Set<String>
    =====================================================
    +   dijkstra(String, String) : List<String>

##### PathFinder.Node implements Comparable<Node>
    +   <<create>> Node(String, int)
    =========================================
    -   <<final>> distFromSource(int) : int
    -   <<final>> name() : String
    =========================================
    +   compareTo(Node) : int
    