package florian.siepe.control;

import florian.siepe.clients.LobbyRegisterClient;
import florian.siepe.entity.db.*;
import florian.siepe.entity.dto.lobby.detail.LobbyRegisterDetailDonator;
import florian.siepe.entity.dto.lobby.search.LobbyRegisterSearchResponse;
import florian.siepe.entity.dto.lobby.search.LobbyRegisterSearchResult;
import florian.siepe.entity.dto.lobby.search.OrgansationCategory;
import florian.siepe.entity.dto.trading.TradingRegisterEntry;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class DataService {
    private static final Pattern NEW_MANAGER = Pattern.compile("Bestellt als Geschäftsführer: (.*?(\\*\\d{2}\\.\\d{2}\\.\\d{4}))[,|.]");
    private static final Pattern NO_MANAGER_ANYMORE = Pattern.compile("Nicht mehr Geschäftsführer: (.*?(\\*\\d{2}\\.\\d{2}\\.\\d{4}))[,|.]");
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);
    @RestClient
    @Inject
    LobbyRegisterClient lobbyRegisterClient;

    @Inject
    NameClassifier nameClassifier;

    @Inject
    Driver driver;

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
            final var record = transaction.run("CREATE (p:Person {name: $name}) RETURN p",
                    Map.of("name", person.name)).single();

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

    public Address persistAddress(Address address) {
        final var session = driver.session();
        return session.writeTransaction(transaction -> {
            final var params = new HashMap<String, Object>();
            params.put("street", address.street);
            params.put("houseNumber", address.houseNumber);
            params.put("zip", address.zip);
            params.put("city", address.city);
            params.put("country", address.country);
            final var record = transaction.run("CREATE (branch:Branch {street: $street, houseNumber: $houseNumber, zip: $zip, city: $city, country: $country}) RETURN branch",
                    params).single();

            return Address.of(record.get("branch").asNode());
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
            final var record = transaction.run("MATCH\n" +
                            "  (a:Organisation),\n" +
                            "  (b:" + matchingType + ")\n" +
                            "WHERE ID(a) = $id1 AND ID(b) = $id2\n" +
                            "CREATE (a)<-[locates:Locates {from: $from, to: $to}]-(b)\n" +
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

    public void createDb() {

    }

    public void insertLobbyRegisterData(final LobbyRegisterSearchResponse lobbyRegisterData) {
        int counter = 0;
        for (final LobbyRegisterSearchResult orgFromLobby : lobbyRegisterData.results) {
            //TODO: Just for development purpose
            if (counter >= 10) {
                //    break;
            }
            counter++;

            final var split = orgFromLobby.detailsPageUrl.split("/");
            final var registerEntry = split[split.length - 2];
            final var registerEntryId = split[split.length - 1];
            final var details = lobbyRegisterClient.getDetails(registerEntry, registerEntryId);
            logger.info("Processing lobby register entry {}", details.searchUrl);

            final var entry = details.registerEntryDetail;

            final var org = Organisation.of(orgFromLobby.name, true);
            final var persistedOrg = persistOrganisation(org);
            for (final OrgansationCategory fieldOfInterest : entry.fieldsOfInterest) {
                final var industry = Industry.of(fieldOfInterest.de, fieldOfInterest.code);
                final var persistedIndustry = persistIndustry(industry);
                MemberOf.of(persistedOrg.id, persistedIndustry.id);
            }

            for (final LobbyRegisterDetailDonator donator : entry.donators) {
                final var year = Integer.valueOf(donator.fiscalYearEnd.split("/")[1]);
                if (nameClassifier.isOrganisation(donator.name)) {
                    final var donatorOrg = Organisation.of(donator.name, false);
                    final var persistedDonator = persistOrganisation(donatorOrg);
                    final var finances = Finances.of(persistedOrg.id, persistedDonator.id, year, donator.donationEuro.to.intValue());
                    persistFinances(finances, "Organisation");
                } else {
                    final var donatorPerson = Person.of(donator.name);
                    final var persistedPerson = persistPerson(donatorPerson);
                    final var finances = Finances.of(persistedOrg.id, persistedPerson.id, year, donator.donationEuro.to.intValue());
                    persistFinances(finances, "Person");
                }
            }
        }
    }


    public void insertTradingRegisterData(final List<TradingRegisterEntry> tradingRegisterEntries) {
        for (final TradingRegisterEntry tradingRegisterEntry : tradingRegisterEntries) {
            final var source = preprocessText(tradingRegisterEntry.source.information);
            final var split = source.split("\\.\\s");
            final var org = split[0];
            final var orgSplit = org.split(",");
            final var orgName = orgSplit[0];
            final var orgAddress = orgSplit.length > 1 ? orgSplit[1] : null;
            final var newManagers = NEW_MANAGER.matcher(source);

            System.out.println("###");
            System.out.println(orgName);
            System.out.println(orgAddress);
            if (newManagers.find()) {
                String theGroup = newManagers.group(1);
                final var newManagersData = extractPersons(theGroup);
                System.out.println(newManagersData);
            }

            final var noManagers = NO_MANAGER_ANYMORE.matcher(source);
            if (noManagers.find()) {
                String theGroup = noManagers.group(1);
                final var oldManagersData = extractPersons(theGroup);
                System.out.println(oldManagersData);
            }
        }

    }

    private String preprocessText(final String info) {
        return info
                .replaceAll("Prof. ", "Prof.")
                .replaceAll("Dr. ", "Dr.")
                .replaceAll("e. K. ", "e.K.")
                .replaceAll("Str. ", "Straße");
    }

    private List<Object[]> extractPersons(String str) {
        return Arrays.stream(str.split(";")).map(s -> {
            final var split = s.split(",");
            // Has academic title
            if (split.length == 5) {
                var firstName = split[1];
                var lastName = split[0];
                var city = split[3];
                var birthday = split[4];
                return new Object[]{Person.of(firstName + " " + lastName), city};
            } else {
                var firstName = split[1];
                var lastName = split[0];
                var city = split[2];
                var birthday = split[3];
                return new Object[]{Person.of(firstName + " " + lastName), city};
            }

        }).toList();
    }
}
