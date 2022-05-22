package florian.siepe.entity.dto.lobby.detail;

import java.util.List;

public class LobbyRegisterDetailClientOrganization {
    public String name;
    public String phoneNumber;
    public LobbyRegisterDetailClientOrganizationAddress address;
    public List<String> organizationEmails;
    public List<String> website;
    public List<LobbyRegisterDetailClientOrganizationRepresentative> legalRepresentatives;
}
