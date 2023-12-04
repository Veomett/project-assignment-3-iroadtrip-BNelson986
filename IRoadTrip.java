import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class IRoadTrip {
    /*
     **********************
     *  Global Constants  *
     **********************
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final Countries map = Countries.getInstance();   //  Get the singular instance of the collection of countries
    private static final Scanner scan = new Scanner(System.in);
    private static final Dictionary<String, String> knownFiles = new Hashtable<>() {{
        put("borders", "borders.txt");
        put("capDist", "capdist.csv");
        put("stateNames", "state_name.tsv");
    }};
    private static BufferedReader reader;

    /**
     * Constructs initial program state. Checks args to ensure all files are present
     * Reads all files and inputs necessary data into 'Countries.countries' and 'Countries.countryCodes'
     *
     * @param args List of files to include *See knownFiles for necessary file names*
     */
    public IRoadTrip (String[] args) throws IOException {

        //  Use small map to check if all files are present
        Map<String, Boolean> hasFile = new HashMap<>(3) {{
            put("borders", false);
            put("capDist", false);
            put("stateNames", false);
        }};

        //  Parse args to check for all needed files and update knownFiles
        for (String arg : args) {
            if (arg.contains(knownFiles.get("borders"))) {
                knownFiles.put("borders", arg);
                hasFile.put("borders", true);
            }
            if (arg.contains(knownFiles.get("capDist"))) {
                knownFiles.put("capDist", arg);
                hasFile.put("capDist", true);
            }
            if (arg.contains(knownFiles.get("stateNames"))) {
                knownFiles.put("stateNames", arg);
                hasFile.put("stateNames", true);
            }
        }

        /*
         *************************************
         *  Order For Proper Initialization  *
         *************************************
         *  1)  Read State Names file to populate known countries
         *  2)  Populate known countries with info from files
         *      a)  Read Borders file to get all border pairings
         *      b)  Read Cap Dist file to get all cap dist measurements
         *      c)  Populate each country's neighbors using info from steps 'a' and 'b'
         */


        //  End execution if missing any files
        if (hasFile.containsValue(false)) {
            for (String key : hasFile.keySet()) {
                if (!hasFile.get(key)) {
                    System.out.println("ERROR! Missing file: " + knownFiles.get(key));
                }
            }
            System.out.println("Ending execution. Provide necessary files to run program.");
            System.exit(1);
        }

        readStateNames(knownFiles.get("stateNames"));
        setBorders(readBorders(knownFiles.get("borders")), readCapDistance(knownFiles.get("capDist")));
    }

    /**
     * Main driver loop for the program. Creates initial objects/data structures
     *
     * @param args List of files, must match 'knownFiles' values
     */
    public static void main (String[] args) throws IOException, InterruptedException {

        args = new String[]{"borders.txt", "capdist.csv", "state_name.tsv"};

        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

    /**
     * Reads user input to find a path between two countries. Will
     * not accept any invalid country names.
     */
    @SuppressWarnings("BusyWait")
    public void acceptUserInput () throws IOException, InterruptedException {
        while (true) {
            String start = "", end = "";
            int tries = 0;

            //  Ensure both start and end are valid countries
            while (map.findCountry(start) == null) {
                //noinspection deprecation
                Runtime.getRuntime().exec("clear");
                if (tries++ > 1) {
                    System.out.println(start + " is not a valid country. Try Again.");
                }

                System.out.print("Enter the name of the first country (type EXIT to quit): ");
                start = scan.nextLine();
                if (start.matches("EXIT")) {
                    return;
                }
            }

            tries = 0;

            while (map.findCountry(end) == null) {
                if (tries++ > 1) {
                    System.out.println(end + " is not a valid country. Try Again.");
                }
                System.out.print("Enter the name of the second country (type EXIT to quit): ");
                end = scan.nextLine();
                if (start.matches("EXIT")) {
                    return;
                }
            }

            System.out.println("Excellent. Calculating the best path. Please Wait...");

            List<String> shortestPath = findPath(start, end);

            System.out.println("Route from " + start + " to " + end + ":");
            for (int i = 1; i < shortestPath.size(); i++) {
                int dist = getDistance(shortestPath.get(i - 1), shortestPath.get(i));

                System.out.println("* " + shortestPath.get(i - 1) + " --> " + shortestPath.get(i) + " (" + dist + " km.)");
            }

            //  Sleep program for 0.5s
            Thread.sleep(500);
        }
    }

    /**
     * Retrieves the distance in km from each country's capital city
     *
     * @param country1 Originating country
     * @param country2 Destination country
     * @return Distance in km from country to country
     */
    public int getDistance (String country1, String country2) {
        Country orig = map.findCountry(country1.toLowerCase());
        return orig.getNeighborDist(country2);
    }

    /**
     * Returns the shortest distance between the 2 countries
     *
     * @param country1 Origin country
     * @param country2 Destination country
     * @return List of 'jumps' (edges) to get from country 1 to country 2
     */
    public List<String> findPath (String country1, String country2) {
        PathFinder pf = new PathFinder();

        return pf.dijkstra(country1, country2);
    }

    /**
     * Reads the stateNames file and populates both 'Countries.countryCodes'
     * and 'Countries.countries' with initial info needed for further execution
     *
     * @param filename Name of the stateNames file
     */
    public void readStateNames (String filename) throws IOException {
        try {
            reader = new BufferedReader(new FileReader(filename));
            /*
             ********** Fields to read **********
             *  1)  State Number -> (ID)
             *  2)  State Code -> (code)
             *  3)  Country Name -> (name)
             *  4)  Start Date -> (start)
             *  5)  End Date -> (end)
             */


            //  Skip first line
            String line = reader.readLine();
            line = reader.readLine();

            while (line != null) {
                String[] countryInfo = line.split("\t");
                //  Ensure exclusion of not current countries
                if (!sdf.parse(countryInfo[4]).before(sdf.parse("2020-12-31"))) {
                    map.addCountryInfo(line);
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        System.out.println(filename + " has been read and processed.");
    }

    /**
     * Reads the borders file specified and parses all data into pairs of borders
     * then creates a list of pairings.
     *
     * @param filename Name of the file to read
     * @return A full list of all pairings
     */
    public List<Dictionary<String, List<String>>> readBorders (String filename) throws RuntimeException {

        List<Dictionary<String, List<String>>> allBorderPairings = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            while (line != null) {

                Dictionary<String, List<String>> entry = new Hashtable<>();

                String[] borderInfo = line.split(" = ");


                //  No bordering countries? Skip adding to the list
                if (borderInfo.length > 1) {
                    String[] borders = borderInfo[1].split("; ");
                    String origCountry = borderInfo[0];
                    List<String> orig = new ArrayList<>() {{
                        add(origCountry);
                    }};
                    List<String> borderList = new ArrayList<>();

                    for (String country : borders) {
                        String countryName = country.split(" ")[0];

                        borderList.add(countryName);
                    }

                    if (orig.get(0).equalsIgnoreCase("United States")) {
                        System.out.println(" ");
                    }
                    entry.put("origin", orig);
                    entry.put("borders", borderList);

                    allBorderPairings.add(entry);
                }
                line = reader.readLine();
            }
            reader.close();
            return allBorderPairings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the capdist file specified and
     * parses the data into (ida, idb, kmdist)
     * return a list of all pairings processed
     *
     * @param filename Name of the file to read
     * @return A full list of all pairings
     */
    public List<Dictionary<String, String>> readCapDistance (String filename) {

        try {
            List<Dictionary<String, String>> capDistInfo = new ArrayList<>();

            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            line = reader.readLine();

            while (line != null) {
                Dictionary<String, String> pairings = new Hashtable<>();

                String[] info = line.split(",");
                if (info[1].matches("UKG")) {
                    info[1] = "UK";
                }
                pairings.put("origin", info[1]);
                pairings.put("dest", info[3]);
                pairings.put("distance", info[4]);

                capDistInfo.add(pairings);

                line = reader.readLine();
            }

            reader.close();

            //  Sort pairings by origin, then by dest (Enables biSearch while processing)
            capDistInfo.sort((o1, o2) -> {
                int origCompVal = o1.get("origin").compareTo(o2.get("origin"));
                if (origCompVal == 0) {
                    return o1.get("dest").compareTo(o2.get("dest"));
                }
                else {
                    return origCompVal;
                }
            });
            return capDistInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Goes through borderPairs list and sets all valid connections to bordering countries
     *
     * @param borderPairs List of all pairings between bordering countries
     * @param capDistInfo List of distances between nation capitals
     */
    public void setBorders (List<Dictionary<String, List<String>>> borderPairs, List<Dictionary<String, String>> capDistInfo) {
        Countries map = Countries.getInstance();
        Dictionary<String, String> distInfo;

        for (Dictionary<String, List<String>> pair : borderPairs) {
            String origin = pair.get("origin").get(0);
            List<String> borders = pair.get("borders");

            //  Edge cases due to inconsistent naming conventions
            if (origin.contains("United States")) {
                origin = "United States of America";
            }
            if (origin.contains("Germany")) {
                origin = "German Federal Republic";
            }
            Country start = map.findCountry(origin);

            //  Ensure skipping of non-existent countries
            if (start != null) {
                for (String border : borders) {
                    if (border.equalsIgnoreCase("US")) {
                        border = "United States of America";
                    }
                    Country end = map.findCountry(border);
                    if (end != null) {
                        //  Retrieve info from capDistInfo list
                        distInfo = biSearchCapDist(capDistInfo, 0, capDistInfo.size() - 1, start.getCode(), end.getCode());

                        if (distInfo != null) {
                            //  Convert String dist to int
                            int distance = Integer.parseInt(distInfo.get("distance"));

                            //  Add link to both countries, since borders work two ways
                            start.addNeighbor(border, distance);
                            end.addNeighbor(origin, distance);
                        }
                    }
                }
            }
        }
    }

    /**
     * Performs binary search on borderDistances list
     *
     * @param capDistInfo List of measurements between capitals belonging to two nations
     * @param left        Index of the furthest left value to search through
     * @param right       Index of the furthest right value to search through
     * @param origin      3-Letter code of starting country
     * @param dest        3-Letter code of the destination country
     * @return Dictionary entry containing the distance in km from origin to dest
     */
    private Dictionary<String, String> biSearchCapDist (List<Dictionary<String, String>> capDistInfo,
                                                        int left, int right, String origin, String dest) {
        int mid = ((right - left) / 2) + left;

        Dictionary<String, String> entry = capDistInfo.get(mid);

        String entryOrigin = entry.get("origin"), entryDest = entry.get("dest");

        int origMatch = entryOrigin.compareToIgnoreCase(origin);
        int destMatch = entryDest.compareToIgnoreCase(dest);

        if (right - left < 0) {
            return null;
        }
        //  Matching Dictionary found, return entry
        if (origMatch == 0 && destMatch == 0) {
            return entry;
        }

        if (origMatch > 0) {
            return biSearchCapDist(capDistInfo, left, mid - 1, origin, dest);
        }
        if (origMatch < 0) {
            return biSearchCapDist(capDistInfo, mid + 1, right, origin, dest);
        }

        //  If origin is found, check for matching dest
        //  If entryDest is larger than "dest" move left
        if (destMatch > 0) {
            return biSearchCapDist(capDistInfo, left, mid - 1, origin, dest);
        }
        return biSearchCapDist(capDistInfo, mid + 1, right, origin, dest);
    }
}