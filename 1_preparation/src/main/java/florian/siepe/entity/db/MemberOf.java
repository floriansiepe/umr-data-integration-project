package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

public class MemberOf {
    public Long id1;
    public Long id2;

    public static MemberOf of(final Node org) {
        return of(org.get("id1").asLong(), org.get("id2").asLong());
    }

    public static MemberOf of(final Long id1, final Long id2) {
        final var memberOf = new MemberOf();
        memberOf.id1 = id1;
        memberOf.id2 = id2;
        return memberOf;
    }
}
