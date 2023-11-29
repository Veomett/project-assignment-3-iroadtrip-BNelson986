import java.io.*;
import java.nio.charset.Charset;
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
        put("stateNames", "state_names.tsv");
    }};

    public IRoadTrip (String [] args) {
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
         *  3)  Accept user input
         */
    }

    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }

    public List<String> findPath (String country1, String country2) {
        List<List<String>> onePath;
        Dictionary<Integer, List<List<String>>> allPaths = new Hashtable<>();

    }


    public void acceptUserInput() throws IOException {
        String start = "", end = "";
        int tries = 0;

        //  Ensure both start and end are valid countries
        while(map.findCountry(start.toLowerCase()) == null){
            Runtime.getRuntime().exec("cls");
            if(tries++ > 1){
                System.out.println(start + " is not a valid country. Try Again.");
            }

            System.out.print("Please enter a starting Country: ");
            start = scan.nextLine();
        }

        tries = 0;

        while(map.findCountry(end.toLowerCase()) ==  null){
            Runtime.getRuntime().exec("cls");
            if(tries++ > 1){
                System.out.println(end + " is not a valid country. Try Again.");
            }
            System.out.println("Starting country: " + start);
            System.out.print("Now, enter a destination country: ");
            end = scan.nextLine();
        }

        System.out.println("Excellent. Calculating the best path from: '" + start + "' to: '" + end + "' ...");

    }


    public static void main(String[] args) {
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
    public List<Dictionary<String, String>> readBorders(String filename) throws RuntimeException {

        List<Dictionary<String, String>> allBorderPairings = new ArrayList<>();

        try{
            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            while(line != null){

                Dictionary<String, String> entry = new Hashtable<>();

                String [] borderInfo = line.split(" = ");

                String origCountry = borderInfo[0];
                String [] borders = borderInfo[1].split("; ");

                for(String country : borders){
                    String countryName = country.split(" ")[0];

                    entry.put("origin", origCountry);
                    entry.put("dest", countryName);

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
    public void setBorders(List<Dictionary<String, String>> borderPairs, List<Dictionary<String, String>> capDistInfo) {
        Countries map = Countries.getInstance();
        Dictionary<String, String> distInfo;

        for(Dictionary<String, String> pair : borderPairs){
            String origin = pair.get("origin");
            String dest = pair.get("dest");

            Country start = map.findCountry(origin);
            Country end = map.findCountry(dest);

            //  Retrieve info from capDistInfo list
            distInfo = biSearchCapDist(capDistInfo, 0, capDistInfo.size() - 1, origin, dest);

            //  Convert String dist to int
            int distance = Integer.parseInt(distInfo.get("dist"));

            //  Add link to both countries, since borders work two ways
            start.addNeighbor(dest, distance);
            end.addNeighbor(origin, distance);
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