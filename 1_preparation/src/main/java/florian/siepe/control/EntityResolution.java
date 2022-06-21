package florian.siepe.control;

import florian.siepe.entity.db.Person;
import info.debatty.java.lsh.LSHMinHash;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class EntityResolution {
    private static final Logger logger = getLogger(EntityResolution.class);
    private static final JaroWinkler JARO_WINKLER = new JaroWinkler();

    public List<Triple<Person, Person, Double>> duplicatePersons(List<Person> persons, double similarityThreshold) {
        preprocess(persons);
        final var personBlocks = block(persons);

        final var matches = deepCompareBlocks(personBlocks, similarityThreshold);
        /*for (final Triple<Person, Person, Double> match : matches) {
            logger.info("{} <-> {}: {}", match.getLeft().name, match.getMiddle().name, match.getRight());
        }*/
        return matches;
    }

    private List<Triple<Person, Person, Double>> deepCompareBlocks(final List<List<Person>> personBlocks, final double similarityThreshold) {
        logger.info("Start block comparing");
        final var matches = new LinkedList<Triple<Person, Person, Double>>();
        for (final List<Person> personBlock : personBlocks) {
            matches.addAll(deepCompareBlock(personBlock, similarityThreshold));
        }
        return matches;
    }

    private List<Triple<Person, Person, Double>> deepCompareBlock(final List<Person> personBlock, final double similarityThreshold) {
        logger.debug("Compare block of size {}", personBlock.size());
        final var matches = new LinkedList<Triple<Person, Person, Double>>();
        for (int i = 0; i < personBlock.size(); i++) {
            for (int j = i + 1; j < personBlock.size(); j++) {
                final var similarity = JARO_WINKLER.similarity(personBlock.get(i).name, personBlock.get(j).name);
                if (similarity >= similarityThreshold) {
                    final var match = Triple.of(personBlock.get(i), personBlock.get(j), similarity);
                    matches.add(match);
                }
            }
        }
        return matches;
    }

    List<List<Person>> block(List<Person> persons) {
        logger.info("Block persons by LSH");
        final var universe = persons.stream()
                .flatMap(person -> Arrays.stream(person.name.split(" ")))
                .distinct()
                .toList();

        final var vecs = persons.stream()
                .map(person -> vectorize(person, universe))
                .toList();


        int sizeOfVectors = universe.size();
        int numberOfBuckets = (int) Math.sqrt(vecs.size());
        int stages = 40;

        LSHMinHash lsh = new LSHMinHash(stages, numberOfBuckets, sizeOfVectors);

        final var personIndexToBucket = new HashMap<Integer, Integer>();

        for (int i = 0; i < vecs.size(); i++) {
            final var hash = lsh.hash(vecs.get(i));

            personIndexToBucket.put(i, hash[hash.length - 1]);
        }
        return personIndexToBucket.entrySet()
                .stream()
                .collect(groupingBy(Map.Entry::getValue))
                .values()
                .stream()
                .map(entries -> entries.stream().map(entry -> persons.get(entry.getKey())).toList())
                .toList();
    }

    private boolean[] vectorize(final Person person, final List<String> universe) {
        final var vec = new boolean[universe.size()];

        final var tokens = Arrays.stream(person.name.split(" ")).collect(Collectors.toSet());

        for (int i = 0; i < universe.size(); i++) {
            if (tokens.contains(universe.get(i))) {
                vec[i] = true;
            }
        }

        return vec;
    }

    void preprocess(Collection<Person> persons) {
        logger.info("Preprocess persons");
        for (final Person person : persons) {
            person.birthday = person.birthday.toLowerCase();
            person.name = person.name.toLowerCase();
        }
    }
}
