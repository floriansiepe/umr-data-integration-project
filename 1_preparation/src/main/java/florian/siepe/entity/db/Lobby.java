package florian.siepe.entity.db;

public class Lobby {
    public Organisation organisation;
    public Person person;
    public Long id;

    public static Lobby of(final Organisation organisation, final Long id) {
        final var lobby = new Lobby();
        lobby.organisation = organisation;
        lobby.id = id;
        return lobby;
    }

    public static Lobby of(final Person person, final Long id) {
        final var lobby = new Lobby();
        lobby.person = person;
        lobby.id = id;
        return lobby;
    }

}
