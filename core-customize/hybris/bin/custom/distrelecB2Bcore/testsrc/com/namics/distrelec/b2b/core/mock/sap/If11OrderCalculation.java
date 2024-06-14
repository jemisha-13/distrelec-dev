package com.namics.distrelec.b2b.core.mock.sap;

import java.util.List;

public class If11OrderCalculation {

    private final String customerId;
    private final List<If11OrderCalculationEntry> entries;

    public If11OrderCalculation(String customerId, List<If11OrderCalculationEntry> entries) {
        this.customerId = customerId;
        this.entries = entries;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<If11OrderCalculationEntry> getEntries() {
        return entries;
    }
}
