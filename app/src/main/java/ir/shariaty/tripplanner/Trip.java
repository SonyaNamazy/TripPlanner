package ir.shariaty.tripplanner;

import java.io.Serializable;
import java.util.Map;

public class Trip implements Serializable {
    private String id;
    private String destination;
    private String departureDate;
    private String returnDate;
    private String companions;
    private String tripType;
    private String budget;
    private Map<String, Boolean> packingList;

    public Trip() {}

    public Trip(String destination, String departureDate, String returnDate,
                String companions, String tripType, String budget,
                Map<String, Boolean> packingList) {
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.companions = companions;
        this.tripType = tripType;
        this.budget = budget;
        this.packingList = packingList;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDepartureDate() { return departureDate; }
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getCompanions() { return companions; }
    public void setCompanions(String companions) { this.companions = companions; }

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public Map<String, Boolean> getPackingList() { return packingList; }
    public void setPackingList(Map<String, Boolean> packingList) { this.packingList = packingList; }
}







