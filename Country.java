import java.util.ArrayList;
import java.util.List;


/**
 * Individual Country object. Keeps track of all neighboring
 * countries and the distance between the capitals.
 */
public class Country {
    private final List<Neighbor> neighbors;
    /*
     ************************
     *  Private Properties  *
     ************************
     */
    private String name;
    private String code;
    private int ID;

    /*
     ***********************
     *  Utility Functions  *
     ***********************
     */
    public Country () {
        neighbors = new ArrayList<>();
    }

    public String getCode () {
        return code;
    }

    public void setCode (String code) {
        this.code = code;
    }

    public List<Neighbor> getNeighbors () {
        return neighbors;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setID (int ID) {
        this.ID = ID;
    }

    /**
     *  Adds a new neighbor to the country. Will not add any duplicate entries
     * @param countryName Name of neighboring country
     * @param distBetweenCapitals Distance in km between capitals
     */
    public void addNeighbor (String countryName, int distBetweenCapitals) {
        //  If neighbors not empty, check for duplicate values
        if (!neighbors.isEmpty()) {
            for (Neighbor entry : neighbors) {
                assert entry.name != null;
                if (entry.name.equalsIgnoreCase(countryName)) {
                    return;
                }
            }
        }

        Neighbor newAddition = new Neighbor(countryName, distBetweenCapitals);

        //  Check for proper instantiation before adding to list
        if (newAddition.distToCap != -1) {
            neighbors.add(newAddition);
        }
    }

    /**
     *  Searches neighbor list and returns the distance in km to reach the next country
     * @param countryName Name of country to move to
     * @return Distance in km to travel to countryName
     */
    public int getNeighborDist (String countryName) {
        int dist = Integer.MAX_VALUE;
        for (Neighbor elem : neighbors) {
            assert elem.name != null;
            if (elem.name.equalsIgnoreCase(countryName)) {
                dist = elem.distToCap;
            }
        }
        return dist;
    }


    /**
     *  Object to store information about distance to a neighboring country.
     *  Includes a pointer to that country's object.
     */
    protected static class Neighbor {
        /*
         ************************
         *  Private Properties  *
         ************************
         */
        private final String name;
        private final int distToCap;

        /**
         * Creates a new Neighbor object if the country has already been logged
         *
         * @param countryName Name of Neighbor to create
         * @param distToCaP   Distance in KM from Capital to Capital
         */
        public Neighbor (String countryName, int distToCaP) {
            //  Neighbor must have a Country object to establish the link.
            if (Countries.getInstance().findCountry(countryName) != null) {
                name = countryName;
                distToCap = distToCaP;
            }
            else {
                //  Set all to error values
                name = null;
                distToCap = -1;

                System.out.println("Country not found in map. Please add before assigning neighbors.");
            }
        }

        /*
         ***************************
         *  Util Access Functions  *
         ***************************
         */

        public String getName () {
            return name;
        }

        public int getDistToCap () {
            return distToCap;
        }
    }
}


