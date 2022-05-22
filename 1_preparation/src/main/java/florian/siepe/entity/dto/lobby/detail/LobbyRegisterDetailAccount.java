package florian.siepe.entity.dto.lobby.detail;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

public class LobbyRegisterDetailAccount {
    public String registerNumber;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    public LocalDateTime firstPublicationDate;
    public Boolean inactive;
}
