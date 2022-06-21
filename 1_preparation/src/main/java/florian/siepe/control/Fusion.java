package florian.siepe.control;

import org.apache.commons.lang3.tuple.Triple;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class Fusion {
    public <T> HashSet<Set<T>> merge(final List<Triple<T, T, Double>> matches) {
        final var groupedMatches = new HashMap<T, Set<T>>();

        for (final Triple<T, T, Double> match : matches) {
            final var left = match.getLeft();
            final var middle = match.getMiddle();
            groupTs(groupedMatches, left, middle);
            groupTs(groupedMatches, middle, left);
        }

        final var clusters = new HashSet<Set<T>>();

        while (!groupedMatches.isEmpty()) {
            final var entry = groupedMatches.entrySet().iterator().next();

            final var cluster = new HashSet<>(entry.getValue());
            cluster.add(entry.getKey());
            clusters.add(cluster);
            groupedMatches.remove(entry.getKey());


            for (final T person : entry.getValue()) {
                groupedMatches.remove(person);
                for (final Set<T> rightHandSide : groupedMatches.values()) {
                    rightHandSide.remove(person);
                }
            }


        }

        return clusters;
    }

    private <T> void groupTs(final HashMap<T, Set<T>> groupedMatches, final T left, final T middle) {
        groupedMatches.computeIfPresent(left, (person, people) -> {
            people.add(middle);
            return people;
        });
        groupedMatches.computeIfAbsent(left, person -> {
            final var people = new HashSet<T>();
            people.add(middle);
            return people;
        });
    }
}
