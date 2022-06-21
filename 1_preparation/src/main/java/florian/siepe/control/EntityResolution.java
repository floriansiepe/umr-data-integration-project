package florian.siepe.control;

import info.debatty.java.lsh.LSHMinHash;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.slf4j.LoggerFactory.getLogger;

public class EntityResolution<T> {
    private static final Logger logger = getLogger(EntityResolution.class);
    private static final JaroWinkler JARO_WINKLER = new JaroWinkler();
    private final List<T> entities;
    private final Function<T, String> mapper;
    private final double similarityThreshold;

    public EntityResolution(List<T> entities, Function<T, String> mapper, double similarityThreshold) {
        this.entities = entities;
        this.mapper = mapper;
        this.similarityThreshold = similarityThreshold;
    }

    public List<Triple<T, T, Double>> duplicateEntities() {
        final var entitiesToken = preprocess(entities);
        final var blockedEntities = block(entitiesToken);

        return deepCompareBlocks(blockedEntities, entitiesToken, similarityThreshold);
    }

    private List<Triple<T, T, Double>> deepCompareBlocks(final List<List<T>> entityBlocks, final HashMap<T, String> entitiesToken, final double similarityThreshold) {
        logger.info("Start block comparing");
        final var matches = new LinkedList<Triple<T, T, Double>>();
        for (final List<T> entityBlock : entityBlocks) {
            matches.addAll(deepCompareBlock(entityBlock, entitiesToken, similarityThreshold));
        }
        return matches;
    }

    private List<Triple<T, T, Double>> deepCompareBlock(final List<T> entityBlock, final HashMap<T, String> entitiesToken, final double similarityThreshold) {
        logger.debug("Compare block of size {}", entityBlock.size());
        final var matches = new LinkedList<Triple<T, T, Double>>();
        for (int i = 0; i < entityBlock.size(); i++) {
            for (int j = i + 1; j < entityBlock.size(); j++) {
                final var e1 = entityBlock.get(i);
                final var e2 = entityBlock.get(j);
                final var similarity = JARO_WINKLER.similarity(entitiesToken.get(e1), entitiesToken.get(e2));
                if (similarity >= similarityThreshold) {
                    final var match = Triple.of(e1, e2, similarity);
                    matches.add(match);
                }
            }
        }
        return matches;
    }

    List<List<T>> block(HashMap<T, String> preprocessedEntities) {
        logger.info("Block preprocessedEntities by LSH");
        final var universe = preprocessedEntities.keySet()
                .stream()
                .flatMap(entity -> Arrays.stream(preprocessedEntities.get(entity).split(" ")))
                .distinct()
                .toList();

        final var orderedEntities = new LinkedList<>(preprocessedEntities.keySet());

        final var vecs = orderedEntities
                .stream()
                .map(entity -> vectorize(preprocessedEntities.get(entity), universe))
                .toList();


        int sizeOfVectors = universe.size();
        int numberOfBuckets = (int) Math.sqrt(vecs.size());
        int stages = 40;

        LSHMinHash lsh = new LSHMinHash(stages, numberOfBuckets, sizeOfVectors);

        final var entityIndexToBucket = new HashMap<Integer, Integer>();

        for (int i = 0; i < vecs.size(); i++) {
            final var hash = lsh.hash(vecs.get(i));

            entityIndexToBucket.put(i, hash[hash.length - 1]);
        }
        return entityIndexToBucket.entrySet()
                .stream()
                .collect(groupingBy(Map.Entry::getValue))
                .values()
                .stream()
                .map(entries -> entries.stream().map(entry -> orderedEntities.get(entry.getKey())).toList())
                .toList();
    }

    private boolean[] vectorize(final String token, final List<String> universe) {
        final var vec = new boolean[universe.size()];

        final var tokens = Arrays.stream(token.split(" ")).collect(Collectors.toSet());

        for (int i = 0; i < universe.size(); i++) {
            if (tokens.contains(universe.get(i))) {
                vec[i] = true;
            }
        }

        return vec;
    }

    HashMap<T, String> preprocess(Collection<T> entities) {
        logger.info("Preprocess entites");
        final var entitiesToken = new HashMap<T, String>();
        for (final T entity : entities) {
            entitiesToken.put(entity, mapper.apply(entity));
        }
        return entitiesToken;
    }
}
