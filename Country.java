import java.util.Date;
import java.util.List;


public class Country {
    /*
     ************************
     *  Private Properties  *
     ************************
     */
    private String name;
    private String code;
    private Date start;
    private Date end;
    private int ID;
    private List<Neighbor> neighbors;

    /*
     ***********************
     *  Utility Functions  *
     ***********************
     */

    public String getCode () {
        return code;
    }

    public Date getEnd () {
        return end;
    }

    public int getID () {
        return ID;
    }

    public Date getStart () {
        return start;
    }

    public List<Neighbor> getNeighbors () {
        return neighbors;
    }

    public String getName () {
        return name;
    }

    public void setCode (String code) {
        this.code = code;
    }

    public void setEnd (Date end) {
        this.end = end;
    }

    public void setID (int ID) {
        this.ID = ID;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void addNeighbor (String countryName, int distBetweenCapitials){
        //  If neighbors not empty, check for duplicate values
        if(!neighbors.isEmpty()){
            for(Neighbor entry : neighbors){
                assert entry.name != null;
                if(entry.name.equalsIgnoreCase(countryName)){
                    System.out.println("Duplicate entry. Abort insertion.");
                    return;
                }
            }
        }

        Neighbor newAddition = new Neighbor(countryName, distBetweenCapitials);

        //  Check for proper instantiation before adding to list
        if(newAddition.distToCap != -1){
            neighbors.add(newAddition);
        }
    }

    public void setStart (Date start) {
        this.start = start;
    }

    protected static class Neighbor{
        /*
         ************************
         *  Private Properties  *
         ************************
         */
        private final String name;
        private final Country link;
        private final int distToCap;

        /**
         *  Creates a new Neighbor object if the country has already been logged
         * @param countryName Name of Neighbor to create
         * @param distToCaP Distance in KM from Capital to Capital
         */
        Neighbor (String countryName, int distToCaP){
            //  Neighbor must have a Country object to establish the link.
            if(Countries.getInstance().findCountry(countryName.toLowerCase()) != null) {
                name = countryName;
                distToCap = distToCaP;
                link = Countries.getInstance().findCountry(name);
            }
            else{
                //  Set all to error values
                name = null;
                link = null;
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

        public Country getLink () {
            return link;
        }

        public int getDistToCap () {
            return distToCap;
        }
    }
}


