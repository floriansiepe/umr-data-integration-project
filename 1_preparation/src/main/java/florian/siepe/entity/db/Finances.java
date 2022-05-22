package florian.siepe.entity.db;

import org.neo4j.driver.types.Relationship;

public class Finances {
    public Integer year;
    public Integer amount;
    public Long id1;
    public Long id2;

    public static Finances of(final Relationship relation) {
        return of(relation.startNodeId(), relation.endNodeId(), relation.get("year").asInt(), relation.get("amount").asInt());
    }

    public static Finances of(final Long id1, final Long id2, final Integer year, final Integer amount) {
        final var finances = new Finances();
        finances.year = year;
        finances.amount = amount;
        finances.id1 = id1;
        finances.id2 = id2;
        return finances;
    }
}
