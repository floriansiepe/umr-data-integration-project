package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Organisation {
    public String name;

    public static Organisation of(final Node org) {
        return of(org.get("name").asString());
    }

    public static Organisation of(final String name) {
        final var org = new Organisation();
        org.name = name;
        return org;
    }
}
