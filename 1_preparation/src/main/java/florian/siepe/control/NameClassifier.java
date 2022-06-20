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
        if (lower.contains("gbr") || lower.contains("ohg") || lower.contains(" hg") || lower.contains("gmbh") || lower.contains(" ug") || lower.contains(" ag")|| lower.contains("kg") || lower.contains("ek") || lower.contains("mbh")) {
            return true;
        }
        return lower.contains("amt") || lower.contains("verein") || lower.contains("senat") || lower.contains("bund") || lower.contains("verband") || lower.contains("ministerium") || lower.contains("land") || lower.contains("gemeinde")
                || lower.contains("kommune") || lower.contains("universität") || lower.contains("kirche") || lower.contains("einrichtung") || lower.contains("institut") || lower.contains("stelle") || lower.contains("ev")
                || lower.contains("gmbr") || lower.contains("haftung")  || lower.contains("beschränkt")  || lower.contains("gesellschaft") || lower.contains("aktien") || lower.contains("werk")
                || lower.contains("betrieb")  || lower.contains("meister") || lower.contains("beratung")  || lower.contains("unternehmen") || lower.contains("consult") || lower.contains("bau")  || lower.contains("media")
                || lower.contains("limited") || lower.contains("ggmbh") || lower.contains("kfz") || lower.contains("marketing") || lower.contains("management") || lower.contains("international") || lower.contains("e v")
                || lower.contains("e. v.") || lower.contains("e.v.") || lower.contains("e k") || lower.contains("co.")  || lower.contains("service") || lower.contains("immobilien")
                || lower.contains("verwaltung") || lower.contains(" immo") || lower.contains("industrie") || lower.contains("auto") || lower.contains("geschäft") || lower.contains("geschaeft") || lower.contains("handel")
                || lower.contains("engineering") || lower.contains("schule") || lower.contains("technik");

    }
}
