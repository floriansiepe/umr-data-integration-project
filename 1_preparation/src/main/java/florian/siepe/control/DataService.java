package florian.siepe.control;

import florian.siepe.clients.LobbyRegisterClient;
import florian.siepe.entity.db.*;
import florian.siepe.entity.dto.lobby.detail.LobbyRegisterDetailDonator;
import florian.siepe.entity.dto.lobby.detail.LobbyRegisterDetailResponse;
import florian.siepe.entity.dto.lobby.search.OrgansationCategory;
import florian.siepe.entity.dto.trading.TradingEntryStatus;
import florian.siepe.entity.dto.trading.TradingRegisterEntry;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@ApplicationScoped
public class DataService {
    private static final Pattern NEW_MANAGER = Pattern.compile("Bestellt als Geschaeftsfuehrer: (.*?(\\*\\d{2}\\.\\d{2}\\.\\d{4}))[,|.]");
    private static final Pattern NO_MANAGER_ANYMORE = Pattern.compile("Nicht mehr Geschaeftsfuehrer: (.*?(\\*\\d{2}\\.\\d{2}\\.\\d{4}))[,|.]");
    private static final Pattern ADDRESS = Pattern.compile("\\(([a-zA-Z\\d,\\s-\\.]+)\\)");
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(false));
    private static final List<String> EDGE_TYPES = List.of("IsManager", "Finances", "MemberOf", "Locates");


    //@RestClient
    @Inject
    LobbyRegisterClient lobbyRegisterClient;

    @Inject
    NameClassifier nameClassifier;

    @Inject
    Driver driver;

    public List<Organisation> getOrganizations() {
        final var session = driver.session();
        return session.readTransaction(transaction -> transaction.run("MATCH (org:Organisation) RETURN org").stream().map(record -> Organisation.of(record.get("org").asNode())).toList());
    }

    public List<Person> getPersons() {
        final var session = driver.session();
        return session.readTransaction(transaction -> transaction.run("MATCH (person:Person) RETURN person").stream().map(record -> Person.of(record.get("person").asNode())).toList());
    }

    public List<Address> getAddresses() {
        final var session = driver.session();
        return session.readTransaction(transaction -> transaction.run("MATCH (address:Address) RETURN address").stream().map(record -> Address.of(record.get("address").asNode())).toList());
    }

    public Organisation persistOrganisation(Organisation org) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("name", org.name);
            params.put("isLobby", org.isLobby);
            final var record = transaction.run("CREATE (org:Organisation {name: $name, isLobby: $isLobby}) RETURN org",
                    params).single();

            return Organisation.of(record.get("org").asNode());
        });
    }

    public Person persistPerson(Person person) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("name", person.name);
            params.put("birthday", person.birthday);
            final var record = transaction.run("CREATE (p:Person {name: $name, birthday: $birthday}) RETURN p",
                    params).single();

            return Person.of(record.get("p").asNode());
        });
    }

    public Industry persistIndustry(Industry industry) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("name", industry.name);
            params.put("type", industry.type);
            final var record = transaction.run("CREATE (branch:Branch {name: $name, type: $type}) RETURN branch",
                    params).single();

            return Industry.of(record.get("branch").asNode());
        });
    }

    private Finances persistFinances(final Finances finances, final String matchingType) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("year", finances.year);
            params.put("amount", finances.amount);
            params.put("id1", finances.id1);
            params.put("id2", finances.id2);
            final var record = transaction.run("MATCH\n" +
                            "  (a:Organisation),\n" +
                            "  (b:" + matchingType + ")\n" +
                            "WHERE ID(a) = $id1 AND ID(b) = $id2\n" +
                            "CREATE (a)<-[finances:Finances {year: $year, amount: $amount}]-(b)\n" +
                            "RETURN finances",
                    params);

            return null;// Finances.of(record.get("finances").asRelationship());
        });
    }

    private void persistMemberOf(final MemberOf memberOf) {
        final var session = driver.session();
        session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("id1", memberOf.id1);
            params.put("id2", memberOf.id2);
            final var record = transaction.run("""
                            MATCH
                              (a:Organisation),
                              (b:Branch) WHERE ID(a) = $id1 AND ID(b) = $id2
                            CREATE (a)-[memberOf:MemberOf]->(b)
                            RETURN memberOf""",
                    params);

            return null;
        });
    }

    public Address persistAddress(Address address) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("street", address.street);
            params.put("houseNumber", address.houseNumber);
            params.put("zip", address.zip);
            params.put("city", address.city);
            params.put("country", address.country);
            final var record = transaction.run("CREATE (address:Address {street: $street, houseNumber: $houseNumber, zip: $zip, city: $city, country: $country}) RETURN address",
                    params).single();

            return Address.of(record.get("address").asNode());
        });
    }

    private Locates persistLocates(final Locates locates, final String matchingType) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("from", locates.from);
            params.put("to", locates.to);
            params.put("id1", locates.id1);
            params.put("id2", locates.id2);
            final var record = transaction.run("MATCH " +
                            "  (a:Address), " +
                            "  (b:" + matchingType + ") " +
                            "WHERE ID(a) = $id1 AND ID(b) = $id2 " +
                            "CREATE (a)<-[locates:Locates {from: $from, to: $to}]-(b) " +
                            "RETURN locates",
                    params);

            return null;
        });
    }

    private IsManager persistIsManager(final IsManager isManager, final String matchingType) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("from", isManager.from);
            params.put("to", isManager.to);
            params.put("id1", isManager.id1);
            params.put("id2", isManager.id2);
            final var record = transaction.run("MATCH\n" +
                            "  (a:Organisation),\n" +
                            "  (b:" + matchingType + ")\n" +
                            "WHERE ID(a) = $id1 AND ID(b) = $id2\n" +
                            "CREATE (a)<-[isManager:IsManager {from: $from, to: $to}]-(b)\n" +
                            "RETURN isManager",
                    params);

            return null;
        });
    }

    public void insertLobbyRegisterData(final List<LobbyRegisterDetailResponse> lobbyRegisterData) {
        int counter = 0;
        for (final var details : lobbyRegisterData) {
            //TODO: Just for development purpose
            if (counter >= 400) {
                //break;
            }
            counter++;

            logger.info("Processing lobby register entry {}", details.searchUrl);

            final var entry = details.registerEntryDetail;

            final var org = Organisation.of(details.registerEntryDetail.lobbyistIdentity.name, true);
            final var persistedOrg = persistOrganisation(org);
            for (final OrgansationCategory fieldOfInterest : entry.fieldsOfInterest) {
                final var industry = Industry.of(fieldOfInterest.de, fieldOfInterest.code);
                final var persistedIndustry = persistIndustry(industry);
                final var memberOf = MemberOf.of(persistedOrg.id, persistedIndustry.id);
                persistMemberOf(memberOf);
            }

            for (final LobbyRegisterDetailDonator donator : entry.donators) {
                final var year = Integer.valueOf(donator.fiscalYearEnd.split("/")[1]);
                if (nameClassifier.isOrganisation(donator.name)) {
                    final var donatorOrg = Organisation.of(donator.name, false);
                    final var persistedDonator = persistOrganisation(donatorOrg);
                    final var finances = Finances.of(persistedOrg.id, persistedDonator.id, year, donator.donationEuro.to.intValue());
                    persistFinances(finances, "Organisation");
                } else {
                    final var donatorPerson = Person.of(null, donator.name);
                    final var persistedPerson = persistPerson(donatorPerson);
                    final var finances = Finances.of(persistedOrg.id, persistedPerson.id, year, donator.donationEuro.to.intValue());
                    persistFinances(finances, "Person");
                }
            }
        }
    }


    private void writeToFile(final LobbyRegisterDetailResponse details, final String registerEntry, final String registerEntryId) {
        try (FileWriter myWriter = new FileWriter("registerEntries/" + String.format("%s-%s", registerEntry, registerEntryId));) {
            myWriter.write(jsonb.toJson(details));
        } catch (Exception e) {

        }
    }


    public void insertTradingRegisterData(final List<TradingRegisterEntry> tradingRegisterEntries) {
        for (final TradingRegisterEntry tradingRegisterEntry : tradingRegisterEntries) {
            if (tradingRegisterEntry.source.status == TradingEntryStatus.STATUS_INACTIVE) {
                continue;
            }

            final var source = preprocessText(tradingRegisterEntry.source.information);
            final var split = source.split(",");
            final var org = split[0];
            //System.out.println(org);
            final var organisation = Organisation.of(org, false);

            final var addressMatcher = ADDRESS.matcher(source);
            Address address = null;
            if (addressMatcher.find()) {
                String theGroup = addressMatcher.group(1);
                final var addressGroup = theGroup.split(",");
                String street = null;
                String zip = null;
                String city = null;
                String houseNumber = null;
                String[] zipCity = null;
                if (addressGroup.length > 1) {
                    street = addressGroup[0].split(" ")[0];
                    var houseNumberSplit = addressGroup[0].split(" ");
                    if (houseNumberSplit.length >= 2) {
                        houseNumber = houseNumberSplit[houseNumberSplit.length - 1].replaceAll("[\\.,;-]", "");

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < houseNumberSplit.length - 1; i++) {
                            sb.append(" ").append(houseNumberSplit[i]);
                        }
                        street = sb.toString().trim();
                    }
                    zipCity = addressGroup[1].split(" ");
                } else {
                    zipCity = addressGroup[0].split(" ");
                }
                zipCity = Arrays.stream(zipCity).filter(s -> !s.isBlank()).toArray(String[]::new);
                if (zipCity.length > 1) {
                    zip = zipCity[0];
                    city = zipCity[1];
                }
                address = Address.of(street, houseNumber, zip, city, "DE");
            }
            ;
            final var orgName = org.trim();
            final var newManagers = NEW_MANAGER.matcher(source);

            /*System.out.println("###");
            System.out.println(orgName);
            System.out.println(address != null ? address.toString() : null);*/
            List<Person> newManagersData = null;
            if (newManagers.find()) {
                String theGroup = newManagers.group(1);
                newManagersData = extractPersons(theGroup);
                //System.out.println(newManagersData);
            }

            final var noManagers = NO_MANAGER_ANYMORE.matcher(source);
            List<Person> oldManagersData = null;
            if (noManagers.find()) {
                String theGroup = noManagers.group(1);
                oldManagersData = extractPersons(theGroup);
                //System.out.println(oldManagersData);
            }

            final var persistedOrg = persistOrganisation(organisation);
            if (address != null) {
                final var persistedAddress = persistAddress(address);
                final var persistedLocates = persistLocates(Locates.of(persistedAddress.id, persistedOrg.id, null, null), "Organisation");
            }
            persistRemovedManagers(persistedOrg, oldManagersData, tradingRegisterEntry.source.eventDate);
            persistNewManagers(persistedOrg, newManagersData, tradingRegisterEntry.source.eventDate);
        }

    }

    /*
    Duplicates must be checked
     */
    private void persistNewManagers(final Organisation persistedOrg, final List<Person> managers, final LocalDate eventDate) {
        if (managers == null) {
            return;
        }
        for (final Person manager : managers) {
            final var persitedManager = persistPerson(manager);
            final var isManager = IsManager.of(persistedOrg.id, persitedManager.id, eventDate, null);
            persistIsManager(isManager, "Person");
        }
    }

    /*
    Duplicates must be checked
     */
    private void persistRemovedManagers(final Organisation persistedOrg, final List<Person> managers, final LocalDate eventDate) {
        if (managers == null) {
            return;
        }
        for (final Person manager : managers) {
            final var persitedManager = persistPerson(manager);
            final var isManager = IsManager.of(persistedOrg.id, persitedManager.id, null, eventDate);
            persistIsManager(isManager, "Person");
        }
    }

    private String preprocessText(final String info) {
        return info
                .replaceAll("Prof. ", "Prof ")
                .replaceAll("Dr. ", "Dr ")
                .replaceAll("e.K.", "eK")
                .replaceAll("e. K. ", "eK")
                .replaceAll("Str. ", "Strasse")
                .replaceAll("Str", "Strasse")
                .replaceAll("Str .", "Strasse")
                .replaceAll("Ä", "Ae")
                .replaceAll("ä", "ae")
                .replaceAll("Ü", "Ue")
                .replaceAll("ü", "ue")
                .replaceAll("Ö", "Oe")
                .replaceAll("ö", "oe")
                .replaceAll("ß", "ss")
                .replaceAll("e. V.", "eV")
                .replaceAll("e.V.", "eV")
                .replaceAll("/\\\"", "")
                ;
    }

    private List<Person> extractPersons(String str) {
        return Arrays.stream(str.split(";")).map(s -> {
            final var split = s.split(",");
            // Has academic title
            if (split.length == 5) {
                var firstName = split[1].trim();
                var lastName = split[0].trim();
                var city = split[3].trim();
                var birthday = split[4];
                return Person.of(birthday, (firstName + " " + lastName).trim());
                //return new Object[]{Person.of(birthday, (firstName + " " + lastName).trim()), city};
            } else {
                var firstName = split[1].trim();
                var lastName = split[0].trim();
                var city = split[2].trim();
                var birthday = split[3];
                return Person.of(birthday, (firstName + " " + lastName).trim());
                //return new Object[]{Person.of(birthday, (firstName + " " + lastName).trim()), city};
            }

        }).toList();
    }

    public <T extends Identifiable> void mergeVertices(final Set<Set<T>> cluster) {
        logger.info("Merge {} clusters", cluster.size());
        for (final Set<T> group : cluster) {
            final var session = driver.session();
            final var ids = group.stream().map(T::getId).toList();
            logger.debug("Merge cluster of size {}", group.size());

            session.writeTransaction(transaction -> transaction.run("MATCH (n) " +
                    "WHERE ID(n) in $ids " +
                    "WITH COLLECT(n) AS ns " +
                    "CALL apoc.refactor.mergeNodes(ns) YIELD node " +
                    "RETURN node", Map.of("ids", ids)));
        }
    }

    public void mergeEdges() {
        logger.info("Merge duplicate edges");

        for (final String edgeType : EDGE_TYPES) {
            final var session = driver.session();
            session.writeTransaction(transaction -> transaction.run(
                    "MATCH (A)-[r:" + edgeType + "]->(B)\n" +
                            "WITH count(r) as relsCount\n" +
                            "WHERE relsCount > 1 \n" +
                            "MATCH (A)-[r:" + edgeType + "]->(B)\n" +
                            "WITH A,B,collect(r) as rels\n" +
                            "CALL apoc.refactor.mergeRelationships(rels,{properties:\"combine\"})\n" +
                            "YIELD rel RETURN rel"));
        }

    }
}
