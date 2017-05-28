package org.uant.textservice.db;

import java.util.HashSet;
import java.util.Set;

import org.uant.textservice.db.TestEmailGenerator;


public final class ResourceDb {
    TestEmailGenerator testEmailGen = new TestEmailGenerator();
    final Set<String> validCustomers = new HashSet<String>(testEmailGen.getTestEmails());

    public boolean isValidCustomer(String customerEmail) {
        return validCustomers.contains(customerEmail);
    }

    public String getCustomerMessage(String customerEmail) {
        boolean isValid = isValidCustomer(customerEmail);
        if (isValid)
            //TODO actual DB lookup...
            return "all orders shipped";
        else
            return "invalid customer";
    }
}
