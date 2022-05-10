package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Industry {

    public String name;
    public String type;
    public Long id;

    public static Industry of(final Node org) {
        return of(org.get("name").asString(), org.get("type").asString(), org.get("id").asLong());
    }

    public static Industry of(final String name, final String type, final Long id) {
        final var industry = new Industry();
        industry.name = name;
        industry.type = type;
        industry.id = id;
        return industry;
    }
}
