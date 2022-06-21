package florian.siepe.control;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApplicationScoped
public class Deduplication {
    private static final Logger logger = LoggerFactory.getLogger(Deduplication.class);
    private final DataService dataService;
    private final Fusion fusion;

    public Deduplication(final DataService dataService, final Fusion fusion) {
        this.dataService = dataService;
        this.fusion = fusion;
    }

    public void deduplicate() {
        deduplicatePersons();
        deduplicateOrganisations();
    }

    public void deduplicatePersons() {
        deduplicateEntity(dataService::getPersons, person -> person.name, dataService::mergeVertices);
    }

    public void deduplicateOrganisations() {
        deduplicateEntity(dataService::getOrganizations, org -> org.name, dataService::mergeVertices);
    }

    public <T> void deduplicateEntity(Supplier<List<T>> entitySupplier, Function<T, String> mapper, Consumer<Set<Set<T>>> fusionProvider) {
        List<T> entities;
        int maxRetries = 0;
        List<Triple<T, T, Double>> matches;
        do {
            maxRetries++;
            entities = entitySupplier.get();
            logger.info("Got {} entities", entities.size());
            final var entityResolution = new EntityResolution<>(entities, mapper, 0.9);
            matches = entityResolution.duplicateEntities();
            logger.info("Matches iteration {}: {}", maxRetries, matches.size());
            final var cluster = fusion.merge(matches);

            fusionProvider.accept(cluster);
        } while (!matches.isEmpty() && maxRetries < 5);

        logger.info("Got {} entities after fusion", entitySupplier.get().size());
    }
}
