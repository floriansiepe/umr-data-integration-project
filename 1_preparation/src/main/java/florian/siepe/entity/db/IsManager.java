package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

import java.time.LocalDate;

public class IsManager {
    public LocalDate from;
    public LocalDate to;
    public Long id1;
    public Long id2;


    public static IsManager of(final Node org) {
        return of(org.get("id1").asLong(), org.get("id2").asLong(), org.get("from").asLocalDate(), org.get("to").asLocalDate());
    }

    public static IsManager of(final Long id1, final Long id2, final LocalDate from, final LocalDate to) {
        final var manager = new IsManager();
        manager.from = from;
        manager.to = to;
        manager.id1 = id1;
        manager.id2 = id2;
        return manager;
    }
}

