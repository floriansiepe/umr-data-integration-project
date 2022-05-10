package florian.siepe.entity.db;

import org.neo4j.driver.types.Node;

import java.time.LocalDate;

public class Finances {
    public LocalDate date;
    public Float amount;
    public Long id1;
    public Long id2;

    public static Finances of(final Node org) {
        return of(org.get("id1").asLong(), org.get("id2").asLong(), org.get("date").asLocalDate(), org.get("amount").asFloat());
    }

    public static Finances of(final Long id1, final Long id2, final LocalDate date, final Float amount) {
        final var finances = new Finances();
        finances.date = date;
        finances.amount = amount;
        finances.id1 = id1;
        finances.id2 = id2;
        return finances;
    }
}
