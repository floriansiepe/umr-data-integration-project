package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Industry {

    public String name;
    public String type;
    public Long id;

    public static Industry of(final Node node) {
        return of(node.get("name").asString(), node.get("type").asString(), node.id());
    }

    public static Industry of(final String name, final String type, final Long id) {
        final var industry = new Industry();
        industry.name = name;
        industry.type = type;
        industry.id = id;
        return industry;
    }

    public static Industry of(final String name, final String type) {
        return of(name, type, null);
    }
}
