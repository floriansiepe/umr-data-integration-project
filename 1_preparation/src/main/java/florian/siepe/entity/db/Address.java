package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Address implements Identifiable {

    public Long id;
    public String street;
    public String houseNumber;
    public String zip;
    public String city;
    public String country;

    public static Address of(final Node node) {
        return of(node.get("street").asString(), node.get("houseNumber").asString(), node.get("zip").asString(), node.get("city").asString(), node.get("country").asString(), node.id());
    }

    public static Address of(final String street, final String houseNumber, final String zip, final String city, final String country) {
        final var address = new Address();
        address.street = street;
        address.houseNumber = houseNumber;
        address.zip = zip;
        address.city = city;
        address.country = country;
        return address;
    }

    public static Address of(final String street, final String houseNumber, final String zip, final String city, final String country, final Long id) {
        final var address = new Address();
        address.street = street;
        address.houseNumber = houseNumber;
        address.zip = zip;
        address.city = city;
        address.country = country;
        address.id = id;
        return address;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }
}
