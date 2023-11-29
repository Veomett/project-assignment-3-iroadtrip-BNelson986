import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 *  Class uses the "Lazy Initialization" to apply the Singleton Pattern
 *  to share a single instance of the class across the program.
 */
public class Countries {
    private static volatile Countries instance = null;
    public final LinkedHashMap<String, Country> countries = new LinkedHashMap<>();
    /*
     **************************
     *  Utils for countries  *
     **************************
     */

    /**
     *  Creates a new entry in "countries" hashtable
     * @param countryName Full name of the country
     * @param countryCode 3-Letter code of the country
     */
    public void addCountry(String countryName, String countryCode){
        String key = countryName.toLowerCase();
        if(countries.containsKey(key)){
            return;
        }
        Country newAddition = new Country();
        newAddition.setName(countryName);
        newAddition.setCode(countryCode);

        countries.put(key, newAddition);
    }

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
    public void addCountryInfo(String countryData) throws ParseException {
        String [] data = countryData.split(",");

        SimpleDateFormat sdf = new SimpleDateFormat();

        Country info;

        int ID = Integer.parseInt(data[0]);
        String code = data[1];
        String name = data[2];
        Date start = sdf.parse(data[3]);
        Date end = sdf.parse(data[4]);

        //  Key is always lowercase country name
        String key = name.toLowerCase();

        if(!countries.containsKey(key)){
            info = new Country();

            info.setID(ID);
            info.setCode(code);
            info.setName(name);
            info.setStart(start);
            info.setEnd(end);

            countries.put(key, info);
        }
        else{
            info = countries.get(key);

            info.setID(ID);
            info.setCode(code);
            info.setName(name);
            info.setStart(start);
            info.setEnd(end);
        }
    }

    /**
     *  Uses the country code to find country's data in the map
     * @param countryName Full name of country to find
     * @return Data from entry mapped to countryName or null
     */
    public Country findCountry(String countryName) {
        if(countries.containsKey(countryName.toLowerCase())){
            return countries.get(countryName.toLowerCase());
        }
        return null;
    }

    /**
     *  Returns the instance of the class if there is one, else creates a new one
     *  and shares that. Used a thread-safe implementation to be cautious.
     * @return A single instance of the class to an outside user.
     */
    public static Countries getInstance(){
        if(instance == null){
            synchronized (Countries.class){
                if(instance == null){
                    instance = new Countries();
                }
            }
        }
        return instance;
    }
}
