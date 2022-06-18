package florian.siepe.entity.dto.lobby.search;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.ZonedDateTime;
import java.util.List;

public class LobbyRegisterSearchResult {
    public Integer id;
    public String registerNumber;
    public String detailsPageUrl;
    public String name;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    public ZonedDateTime firstPublicationDate;
    public Boolean activeLobbyist;
    public Boolean codexViolation;
    public Boolean updateMissing;
    public Boolean refusedAnything;
    public OrgansationCategory activity;
    public List<OrgansationCategory> fieldsOfInterest;
    public FinacialExpenses financialExpensesEuro;
    public EmployeeCount employeeCount;
}
