import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

public class IRoadTrip {

    private static BufferedReader reader;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final Countries map = Countries.getInstance();
    private static final Scanner scan = new Scanner(System.in);
    private static final Dictionary<String, String> knownFiles = new Hashtable<>() {{
        put("borders", "borders.txt");
        put("capDist", "capdist.csv");
        put("stateNames", "state_name.tsv");
    }};

    public IRoadTrip (String [] args) throws IOException {
        boolean hasBFile = false;
        boolean hasCFile = false;
        boolean hasSFile = false;

        String bFile, cFile, sFile;

        //  Parse args to ensure proper initialization order
        for(String arg : args){
            if(!hasBFile && arg.contains(knownFiles.get("borders"))){
                bFile = arg;
                hasBFile = true;
            }
            if(!hasCFile && arg.contains(knownFiles.get("capDist"))){
                cFile = arg;
                hasCFile = true;
            }
            if(!hasSFile && arg.contains(knownFiles.get("stateNames"))){
                sFile = arg;
                hasSFile = true;
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

        readStateNames(knownFiles.get("stateNames"));

        setBorders(readBorders(knownFiles.get("borders")), readCapDistance(knownFiles.get("capDist")));
    }

    public int getDistance (String country1, String country2) {
        Country orig = map.findCountry(country1.toLowerCase());
        return orig.getNeighborDist(country2);
    }

    public List<String> findPath (String country1, String country2) {
        PathFinder pf = new PathFinder();

        return pf.dijkstra(country1, country2);
    }


    public void acceptUserInput() throws IOException, InterruptedException {
        while(true){
            String start = "", end = "";
            int tries = 0;

            //  Ensure both start and end are valid countries
            while(map.findCountry(start) == null){
                //noinspection deprecation
                Runtime.getRuntime().exec("clear");
                if(tries++ > 1){
                    System.out.println(start + " is not a valid country. Try Again.");
                }

                System.out.print("Enter the name of the first country (type EXIT to quit): ");
                start = scan.nextLine();
                if(start.matches("EXIT")){
                    return;
                }
            }

            tries = 0;

            while(map.findCountry(end) ==  null){
                if(tries++ > 1){
                    System.out.println(end + " is not a valid country. Try Again.");
                }
                System.out.print("Enter the name of the second country (type EXIT to quit): ");
                end = scan.nextLine();
                if(start.matches("EXIT")){
                    return;
                }
            }

            System.out.println("Excellent. Calculating the best path. Please Wait...");

            List<String> shortestPath = findPath(start, end);

            System.out.println("Route from " + start + " to " + end + ":");
            for (int i = 1; i < shortestPath.size(); i++) {
                int dist = getDistance(shortestPath.get(i - 1), shortestPath.get(i));

                System.out.println("* " + shortestPath.get(i - 1)+ " --> " + shortestPath.get(i) + " (" + dist + " km.)");
            }
            Thread.sleep(500);
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

    public void readStateNames(String filename) throws IOException {
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

            while(line != null){
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
     *  Reads the borders file specified and parses all data into pairs of borders
     *  then creates a list of pairings.
     * @param filename Name of the file to read
     * @return A full list of all pairings
     * @throws RuntimeException
     */
    public List<Dictionary<String, List<String>>> readBorders(String filename) throws RuntimeException {

        List<Dictionary<String, List<String>>> allBorderPairings = new ArrayList<>();

        try{
            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            int i = 0;
            while(line != null){

                Dictionary<String, List<String>> entry = new Hashtable<>();

                String [] borderInfo = line.split(" = ");


                //  No bordering countries? Skip adding to the list
                if(borderInfo.length > 1){
                    String [] borders = borderInfo[1].split("; ");
                    String origCountry = borderInfo[0];
                    List<String> orig = new ArrayList<>(){{ add(origCountry); }};
                    List<String> borderList = new ArrayList<>();

                    for(String country : borders){
                        String countryName = country.split(" ")[0];

                        borderList.add(countryName);
                    }

                    if(orig.get(0).equalsIgnoreCase("United States")){
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
     *  Reads the capdist file specified and
     *  parses the data into (ida, idb, kmdist)
     *  return a list of all pairings processed
     * @param filename Name of the file to read
     * @return A full list of all pairings
     */
    public List<Dictionary<String, String>> readCapDistance(String filename){

        try{
            List<Dictionary<String, String>> capDistInfo = new ArrayList<>();

            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            line = reader.readLine();

            while(line != null){
                Dictionary<String, String> pairings = new Hashtable<>();

                String [] info = line.split(",");
                if(info[1].matches("UKG")){
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
            capDistInfo.sort(new Comparator<Dictionary<String, String>>() {
                @Override
                public int compare (Dictionary<String, String> o1, Dictionary<String, String> o2) {
                    int origCompVal = o1.get("origin").compareTo(o2.get("origin"));
                    if(origCompVal == 0){
                        return o1.get("dest").compareTo(o2.get("dest"));
                    }
                    else{
                        return origCompVal;
                    }
                }
            });
            return capDistInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *  Goes through borderPairs list and sets all valid connections to bordering countries
     * @param borderPairs List of all pairings between bordering countries
     * @param capDistInfo List of distances between nation capitals
     */
    public void setBorders(List<Dictionary<String, List<String>>> borderPairs, List<Dictionary<String, String>> capDistInfo) {
        Countries map = Countries.getInstance();
        Dictionary<String, String> distInfo;

        for(Dictionary<String, List<String>> pair : borderPairs){
            String origin = pair.get("origin").get(0);
            List<String> borders = pair.get("borders");

            //  Edge cases due to inconsistent naming conventions
            if(origin.contains("United States")){
                origin = "United States of America";
            }
            if(origin.contains("Germany")){
                origin = "German Federal Republic";
            }
            Country start = map.findCountry(origin);

            //  Ensure skipping of non-existent countries
            if(start != null) {
                for (String border : borders) {
                    if(border.equalsIgnoreCase("US")){
                        border = "United States of America";
                    }
                    Country end = map.findCountry(border);
                    if(end != null) {
                        //  Retrieve info from capDistInfo list
                        distInfo = biSearchCapDist(capDistInfo, 0, capDistInfo.size() - 1, start.getCode(), end.getCode());

                        if(distInfo != null){
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
     *  Performs binary search on borderDistances list
     * @param capDistInfo List of measurements between capitals belonging to two nations
     * @param left  Index of the furthest left value to search through
     * @param right Index of the furthest right value to search through
     * @param origin    3-Letter code of starting country
     * @param dest  3-Letter code of the destination country
     * @return  Dictionary entry containing the distance in km from origin to dest
     */
    private Dictionary<String, String> biSearchCapDist (List<Dictionary<String, String>> capDistInfo,
                                                        int left, int right, String origin, String dest) {
        int mid = ((right - left) / 2) + left;

        Dictionary<String, String> entry = capDistInfo.get(mid);

        String entryOrigin = entry.get("origin"), entryDest = entry.get("dest");

        int origMatch = entryOrigin.compareToIgnoreCase(origin);
        int destMatch = entryDest.compareToIgnoreCase(dest);

        if(right - left < 0){
            return null;
        }
        //  Matching Dictionary found, return entry
        if(origMatch == 0 && destMatch == 0){
            return entry;
        }

        if(origMatch > 0){
            return biSearchCapDist(capDistInfo, left, mid - 1, origin, dest);
        }
        if(origMatch < 0) {
            return biSearchCapDist(capDistInfo, mid + 1, right, origin, dest);
        }

        //  If origin is found, check for matching dest
        //  If entryDest is larger than "dest" move left
        if(destMatch > 0){
            return biSearchCapDist(capDistInfo, left, mid - 1, origin, dest);
        }
        return biSearchCapDist(capDistInfo, mid + 1, right, origin, dest);
    }
}