package florian.siepe.control;

import florian.siepe.entity.db.Person;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Deduplication {
    private static final Logger logger = LoggerFactory.getLogger(Deduplication.class);
    private final DataService dataService;
    private final EntityResolution entityResolution;
    private final Fusion fusion;

    public Deduplication(final DataService dataService, final EntityResolution entityResolution, final Fusion fusion) {
        this.dataService = dataService;
        this.entityResolution = entityResolution;
        this.fusion = fusion;
    }

    public void deduplicatePersons() {
        List<Person> persons;
        int maxRetries = 0;
        List<Triple<Person, Person, Double>> matches;
        do {
            maxRetries++;
            persons = dataService.getPersons();
            logger.info("Got {} persons", persons.size());
            matches = entityResolution.duplicatePersons(persons, 0.9);
            logger.info("Matches iteration {}: {}", maxRetries, matches.size());
            final var cluster = fusion.merge(matches);
            dataService.mergeVertices(cluster);
        } while (!matches.isEmpty() && maxRetries < 5);

        logger.info("Got {} persons after fusion", dataService.getPersons().size());
    }
}
