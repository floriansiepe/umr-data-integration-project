package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Person {
    public Long id;
    public String name;

    public static Person of(final Node node) {
        return of(node.get("name").asString(), node.id());
    }

    public static Person of(final String name, final Long id) {
        final var person = new Person();
        person.id = id;
        person.name = name;
        return person;
    }

    public static Person of(final String name) {
        return of(name, null);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
