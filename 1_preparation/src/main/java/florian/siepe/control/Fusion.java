package florian.siepe.control;

import florian.siepe.entity.db.Person;
import org.apache.commons.lang3.tuple.Triple;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class Fusion {
    public HashSet<Set<Person>> merge(final List<Triple<Person, Person, Double>> matches) {
        final var groupedMatches = new HashMap<Person, Set<Person>>();

        for (final Triple<Person, Person, Double> match : matches) {
            final var left = match.getLeft();
            final var middle = match.getMiddle();
            groupPersons(groupedMatches, left, middle);
            groupPersons(groupedMatches, middle, left);
        }

        final var clusters = new HashSet<Set<Person>>();

        while (!groupedMatches.isEmpty()) {
            final var entry = groupedMatches.entrySet().iterator().next();

            final var cluster = new HashSet<>(entry.getValue());
            cluster.add(entry.getKey());
            clusters.add(cluster);
            groupedMatches.remove(entry.getKey());


            for (final Person person : entry.getValue()) {
                groupedMatches.remove(person);
                for (final Set<Person> rightHandSide : groupedMatches.values()) {
                    rightHandSide.remove(person);
                }
            }


        }

        return clusters;
    }

    private void groupPersons(final HashMap<Person, Set<Person>> groupedMatches, final Person left, final Person middle) {
        groupedMatches.computeIfPresent(left, (person, people) -> {
            people.add(middle);
            return people;
        });
        groupedMatches.computeIfAbsent(left, person -> {
            final var people = new HashSet<Person>();
            people.add(middle);
            return people;
        });
    }
}
