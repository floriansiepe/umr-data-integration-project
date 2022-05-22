package florian.siepe.entity.dto.lobby.detail;

import javax.json.bind.annotation.JsonbProperty;

public class LobbyistLegalForm {
    public String code;
    @JsonbProperty("code_de")
    public String codeDe;
    @JsonbProperty("code_en")
    public String codeEn;
    public String type;
}
