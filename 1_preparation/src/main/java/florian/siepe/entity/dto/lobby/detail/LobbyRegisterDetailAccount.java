package florian.siepe.entity.dto.lobby.detail;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.ZonedDateTime;

public class LobbyRegisterDetailAccount {
    public String registerNumber;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    public ZonedDateTime firstPublicationDate;
    public Boolean inactive;
}
