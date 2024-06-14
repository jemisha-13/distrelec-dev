package com.namics.hybris.toolbox.servicelayer;

import java.util.Collection;

import org.junit.Test;

import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.Employee;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

public class ServicelayerUsageTest extends ServicelayerTransactionalTest {

    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        createDefaultUsers();
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testServicelayerData() {
        for (Catalog catalog : CatalogManager.getInstance().getAllCatalogs()) {
            System.err.println("Catalog: " + catalog.getId());
            for (CatalogVersion catalogVersion : catalog.getCatalogVersions()) {
                System.err.println(" - Catalogversion: " + catalogVersion.getVersion());

            }
        }

        for (Language language : C2LManager.getInstance().getAllLanguages()) {
            System.err.println("Language: " + language.getIsoCode());
        }

        for (Customer customer : ((Collection<Customer>) UserManager.getInstance().getAllCustomers())) {
            System.err.println("Customer: " + customer.getCustomerID());
        }
        for (Employee employee : ((Collection<Employee>) UserManager.getInstance().getAllEmployees())) {
            System.err.println("Employee: " + employee.getName());
        }

    }

}
