import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 *  Class uses the "Lazy Initialization" to apply the Singleton Pattern
 *  to share a single instance of the class across the program.
 */
public class CountryMap {
    private static volatile CountryMap instance = null;
    public final LinkedHashMap<String, String> countryCodes = new LinkedHashMap<>();
    public final LinkedHashMap<String, Country> countryMap = new LinkedHashMap<>();


    /*
     ****************************
     *  Utils for countryCodes  *
     ****************************
     */
    /**
     * Finds the code of the country name entered, if it is in the
     * countryCodes Hashmap
     * @param countryName Full name of country to find
     * @return 3-Letter code of country found, else null
     * @throws Exception Hashmap is empty cannot search it
     */
    public String findCountryCode(String countryName) throws Exception {
        if(countryCodes.isEmpty()){
            throw new Exception("Hash Map is empty");
        }

        String countryCode = "";

        return countryCodes.getOrDefault(countryName, null);
    }

    /**
     *  Adds the countryName and countryCode to the hashmap
     * @param countryName Full name of country to add
     * @param countryCode 3-Letter code of country to add
     */
    public void addCountryCode(String countryName, String countryCode) {
        if(countryCodes.containsKey(countryName)){
            System.out.println("Country already present. No need to map it.");
        }
        else{
            countryCodes.put(countryName, countryCode);
        }
    }

    /*
     **************************
     *  Utils for countryMap  *
     **************************
     */

    /**
     *  Parses countryData (csv formatted), creates a new
     *  Country object with that data, and finally creates an entry
     *  into the map using the code as the key.
     *  Format is as follows:
     *  1)  Country ID number
     *  2)  Country Code
     *  3)  Country Name
     *  4)  Country Start Date
     *  5)  Country End Date
     * @param countryData A CSV Formatted string of the values
     *                    in exactly the format as above.
     */
    public void addCountry(String countryData) throws ParseException {
        String [] data = countryData.split(",");

        SimpleDateFormat sdf = new SimpleDateFormat();

        Country newAddition = new Country();


        newAddition.setID(Integer.parseInt(data[0]));
        newAddition.setCode(data[1]);
        newAddition.setName(data[2]);
        newAddition.setStart(sdf.parse(data[3]));
        newAddition.setEnd(sdf.parse(data[4]));


        countryMap.put(data[1], newAddition);
    }

    /**
     *  Uses the country code to find country's data in the map
     * @param countryCode 3-Letter Code of country to find
     * @return Data from entry mapped to countryCode or null
     * @throws Exception
     */
    public Country findCountry(String countryCode) throws Exception {
        if(findCountryCode(countryCode) != null){
            return findCountry(countryCode);
        }
        return null;
    }
    /**
     *  Returns the instance of the class if there is one, else creates a new one
     *  and shares that. Used a thread-safe implementation to be cautious.
     * @return A single instance of the class to an outside user.
     */
    public static CountryMap getInstance(){
        if(instance == null){
            synchronized (CountryMap.class){
                if(instance == null){
                    instance = new CountryMap();
                }
            }
        }
        return instance;
    }
}
