package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Address {

    public Long id;
    public String street;
    public String houseNumber;
    public Integer zip;
    public String city;
    public String country;

    public static Address of(final Node org) {
        return of(org.get("street").asString(), org.get("houseNumber").asString(), org.get("zip").asInt(), org.get("city").asString(), org.get("country").asString(), org.get("id").asLong());
    }

    public static Address of(final String street, final String houseNumber, final Integer zip, final String city, final String country, final Long id) {
        final var address = new Address();
        address.street = street;
        address.houseNumber = houseNumber;
        address.zip = zip;
        address.city = city;
        address.country = country;
        address.id = id;
        return address;
    }
}
