package florian.siepe.control;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NameClassifier {
    public boolean isOrganisation(String name) {
        final var lower = name.toLowerCase();
        // e.v.
        if (lower.matches("e\\s?\\.\\s?v\\s?\\.")) {
            return true;
        }
        if (lower.contains("gbr") || lower.contains("ohg") || lower.contains(" hg") || lower.contains("gmbr") || lower.contains(" ug") || lower.contains(" ag")) {
            return true;
        }
        return lower.contains("amt") || lower.contains("verein") || lower.contains("senat") || lower.contains("bund") || lower.contains("verband") || lower.contains("ministerium") || lower.contains("land") || lower.contains("gemeinde")
                || lower.contains("kommune") || lower.contains("universität") || lower.contains("kirche") || lower.contains("einrichtung") || lower.contains("institut") || lower.contains("stelle") || lower.contains("ev");

    }
}
