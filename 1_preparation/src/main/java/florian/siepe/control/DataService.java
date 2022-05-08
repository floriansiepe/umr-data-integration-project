package florian.siepe.control;

import florian.siepe.entity.db.Organisation;
import florian.siepe.entity.db.Person;
import org.neo4j.driver.Driver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class DataService {
    @Inject
    Driver driver;

    public Organisation persistOrganisation(Organisation org) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var record = transaction.run("CREATE (org:Organisation {name: $name}) RETURN org",
                    Map.of("name", org.name)).single();

            return Organisation.of(record.get("org").asNode());
        });
    }

    public Person persistOrganisation(Person person) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var record = transaction.run("CREATE (person:Person {name: $name}) RETURN person",
                    Map.of("name", person.name)).single();

            return Person.of(record.get("org").asNode());
        });
    }
}
