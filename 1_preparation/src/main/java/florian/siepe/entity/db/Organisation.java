package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class Organisation {
    public Long id;
    public String name;
    public Boolean isLobby;

    public static Organisation of(final Node node) {
        return of(node.get("name").asString(), node.get("isLobby").asBoolean(), node.id());
    }

    public static Organisation of(final String name, final Boolean isLobby) {
        return of(name, isLobby, null);
    }

    public static Organisation of(final String name, final Boolean isLobby, final Long id) {
        final var org = new Organisation();
        org.name = name;
        org.isLobby = isLobby;
        org.id = id;
        return org;
    }
}
