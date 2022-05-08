package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Person {
    public String name;

    public static Person of(final Node org) {
        return of(org.get("name").asString());
    }

    public static Person of(final String name) {
        final var person = new Person();
        person.name = name;
        return person;
    }
}
