package be.howest.ti.mars.logic.data.repoInterfaces;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;

import java.util.Set;

public interface ReportsRepository {
    Set<String> getReportSections();

    void addReport(BaseAccount baseAccount, String section, String body);
}
