package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Person {
    public Long id;
    public String name;
    public String birthday;

    public static Person of(final Node node) {
        return of(node.get("birthday").asString(), node.get("name").asString(), node.id());
    }

    public static Person of(final String birthday, final String name, final Long id) {
        final var person = new Person();
        person.id = id;
        person.name = name;
        return person;
    }

    public static Person of(final String birthday, final String name) {
        return of(birthday, name, null);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
