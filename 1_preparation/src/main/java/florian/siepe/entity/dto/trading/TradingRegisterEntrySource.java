package florian.siepe.entity.dto.trading;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDate;

public class TradingRegisterEntrySource {
    public String id;
    public Integer rbId;
    public String state;
    public String referenceId;
    public String eventType;
    @JsonbDateFormat("dd.MM.yyyy")
    public LocalDate eventDate;
    public TradingEntryStatus status;
    public String information;
}
