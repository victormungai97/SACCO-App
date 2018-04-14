package ke.co.venturisys.ucreditessentials.others;

public class Sacco {

    private Object icon;
    private String name;
    private String creation_date;
    private String status;
    private String agm;
    private String category;
    private String country;
    private String county;
    private String email;
    private String management;
    private String membershipType;
    private String phone;
    private String regno;
    private String sasraDateRegistration;
    private Boolean sasraRegistration;
    private String website;

    public Sacco(Object icon, String name, String creation_date, String status, String agm,
                 String category, String country, String county, String email, String management,
                 String membershipType, String phone, String regno, String sasraDateRegistration,
                 Boolean sasraRegistration, String website) {
        this.icon = icon;
        this.name = name;
        this.creation_date = creation_date;
        this.status = status;
        this.agm = agm;
        this.category = category;
        this.country = country;
        this.county = county;
        this.email = email;
        this.management = management;
        this.membershipType = membershipType;
        this.phone = phone;
        this.regno = regno;
        this.sasraDateRegistration = sasraDateRegistration;
        this.sasraRegistration = sasraRegistration;
        this.website = website;
    }

    public Sacco(Object icon, String name, String creation_date, String status) {
        this.icon = icon;
        this.name = name;
        this.creation_date = creation_date;
        this.status = status;
    }

    public Object getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getStatus() {
        return status;
    }

    public String getAgm() {
        return agm;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public String getCounty() {
        return county;
    }

    public String getEmail() {
        return email;
    }

    public String getManagement() {
        return management;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public String getPhone() {
        return phone;
    }

    public String getRegno() {
        return regno;
    }

    public String getSasraDateRegistration() {
        return sasraDateRegistration;
    }

    public Boolean getSasraRegistration() {
        return sasraRegistration;
    }

    public String getWebsite() {
        return website;
    }
}
