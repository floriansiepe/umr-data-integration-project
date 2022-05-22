package florian.siepe.entity.dto.lobby.detail;

import florian.siepe.entity.dto.lobby.search.EmployeeCount;
import florian.siepe.entity.dto.lobby.search.FinacialExpenses;
import florian.siepe.entity.dto.lobby.search.OrgansationCategory;

import java.util.List;
import java.util.Map;

public class LobbyRegisterEntryDetail {
    public Long id;
    public EmployeeCount employeeCount;
    public Boolean refuseFinancialExpensesInformation;
    public FinacialExpenses financialExpensesEuro;
    public Boolean refusePublicAllowanceInformation;
    public Boolean refuseDonationInformation;
    public Boolean donationInformationRequired;
    public LobbyRegisterDetailAccount account;
    public String activityDescription;
    public String activityOperationType;
    public OrgansationCategory activity;
    public Boolean codexViolation;
    public LobbyRegisterIdentity lobbyistIdentity;
    public List<Map<String, Object>> legislativeProjects;
    public List<LobbyRegisterDetailDonator> donators;
    public List<OrgansationCategory> fieldsOfInterest;
    public List<LobbyRegisterDetailClientOrganization> clientOrganizations;
    public List<LobbyRegisterDetailClientPerson> clientPersons;
    public List<LobbyRegisterDetailEntryMedia> registerEntryMedia;
    public Boolean disclosureRequirementsExist;
    public Boolean annualReportExists;
}
