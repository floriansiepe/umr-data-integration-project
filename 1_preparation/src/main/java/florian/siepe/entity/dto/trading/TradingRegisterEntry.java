package florian.siepe.entity.dto.trading;

import javax.json.bind.annotation.JsonbProperty;

public class TradingRegisterEntry {
    @JsonbProperty("_index")
    public String index;
    @JsonbProperty("_type")
    public String type;
    @JsonbProperty("_id")
    public String id;
    @JsonbProperty("_source")
    public TradingRegisterEntrySource source;
}
