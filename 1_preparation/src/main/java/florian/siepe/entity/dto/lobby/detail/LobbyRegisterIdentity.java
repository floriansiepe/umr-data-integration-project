package florian.siepe.entity.dto.lobby.detail;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.ZonedDateTime;
import java.util.List;

public class LobbyRegisterIdentity {
    public String identity;
    public String name;
    public String phoneNumber;
    public LobbyistAddress address;
    public LobbyistLegalForm legalForm;
    public List<String> organizationEmails;
    public List<String> websites;
    public List<LobbyRegisterDetailLegalRepresentative> legalRepresentatives;
    public List<LobbyRegisterDetailEmployee> namedEmployees;
    public Long members;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    public ZonedDateTime membersCountDate;
    public List<String> membershipEntries;
}
