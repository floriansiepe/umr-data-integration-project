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
        return lower.contains("amt") || lower.contains("verein") || lower.contains("senat");
        /*
        Also add
        - bund
        - verband
        - ministerium
        - land
        - gemeinde
        - kommune
        - universität
        - kirche
        - Einrichtung
        - Institut
        - Stelle

        fix e.v.
         */
    }
}
