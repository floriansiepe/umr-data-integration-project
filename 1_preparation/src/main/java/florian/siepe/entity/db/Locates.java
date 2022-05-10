package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

import java.time.LocalDate;

public class Locates {
    public LocalDate from;
    public LocalDate to;
    public Long id1;
    public Long id2;

    public static Locates of(final Node org) {
        return of(org.get("id1").asLong(), org.get("id2").asLong(), org.get("from").asLocalDate(), org.get("to").asLocalDate());
    }

    public static Locates of(final Long id1, final Long id2, final LocalDate from, final LocalDate to) {
        final var locates = new Locates();
        locates.from = from;
        locates.to = to;
        locates.id1 = id1;
        locates.id2 = id2;
        return locates;
    }
}
