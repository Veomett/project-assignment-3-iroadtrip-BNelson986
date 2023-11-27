import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

public class IRoadTrip {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public IRoadTrip (String [] args) {
        // Replace with your code
    }


    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

    public void readStateNames(String filename) throws IOException {
        BufferedReader reader;
        CountryMap map = CountryMap.getInstance();
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
                    map.addCountryCode(countryInfo[2], countryInfo[1]);
                    map.addCountry(line);
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

        BufferedReader reader;
        CountryMap map = CountryMap.getInstance();

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

                    entry.put("Start", map.findCountryCode(origCountry));
                    entry.put("End", map.findCountryCode(countryName));

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
    public List<String[]> readCapDistance(String filename){
        BufferedReader reader;
        CountryMap map = CountryMap.getInstance();

        try{
            List<String []> capDistInfo = new ArrayList<>();

            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            line = reader.readLine();

            while(line != null){
                String [] info = line.split(",");

                String ida = info[1], idb = info[3], kmdist = info[4];

                String [] temp = {ida, idb, kmdist};

                capDistInfo.add(temp);

                line = reader.readLine();
            }

            reader.close();
            return capDistInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

