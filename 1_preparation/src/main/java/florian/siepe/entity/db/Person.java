package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Person implements Identifiable {
    public Long id;
    public String name;
    public String birthday;

    @Override
    public Long getId() {
        return id;
    }

    public static Person of(final Node node) {
        return of(node.get("birthday").asString(), node.get("name").asString(), node.id());
    }

    public static Person of(final String birthday, final String name, final Long id) {
        final var person = new Person();
        person.birthday = birthday;
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
                ", birthday='" + birthday + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Person person = (Person) o;

        return id != null ? id.equals(person.id) : person.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
