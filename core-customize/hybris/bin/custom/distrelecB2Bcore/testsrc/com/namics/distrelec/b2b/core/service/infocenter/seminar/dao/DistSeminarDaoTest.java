/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.infocenter.seminar.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarModel;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DistSeminarDaoTest}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistSeminarDaoTest extends ServicelayerTransactionalTest {

    private static final String SEMINAR_UID = UUID.randomUUID().toString();
    private static final String SEMINAR_UID_2 = UUID.randomUUID().toString();
    private static final Random RAND = new Random();

    @Resource
    private DistSeminarDao distSeminarDao;
    @Resource
    private ModelService modelService;


    @Before
    public void setUp() throws Exception {
        final DistSeminarModel seminar = modelService.create(DistSeminarModel.class);
        seminar.setUid(SEMINAR_UID);
        seminar.setDate(new Date(System.currentTimeMillis() + 123456789L));
        seminar.setRegistrationDelay(new Date(System.currentTimeMillis() + 987654321L));
        seminar.setTopic("Something intersting");
        seminar.setNote("No comment!");
        seminar.setContent("This is a very interesting topic");
        seminar.setCostDesc("CHF 200 all incl.");
        seminar.setLocation("Distrelec Nanikon");
        seminar.setSpeakers("Me and You :)");

        modelService.save(seminar);
    }

    /**
     * Test method for {@link com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#getSeminarForUID(java.lang.String)}.
     */
    @Test
    public final void testGetSeminarForUID() {
        final DistSeminarModel seminar = distSeminarDao.getSeminarForUID(SEMINAR_UID);
        assertNotNull("The seminar must be not null", seminar);
    }

    /**
     * Test method for {@link com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#removeSeminar(java.lang.String)}.
     */
    @Test(expected = de.hybris.platform.servicelayer.exceptions.ModelNotFoundException.class)
    public final void testRemoveSeminar() {
        final DistSeminarModel seminar = distSeminarDao.getSeminarForUID(SEMINAR_UID);
        assertNotNull("The seminar must be not null", seminar);
        // removing the seminar
        distSeminarDao.removeSeminar(SEMINAR_UID);
        // Here an exception must be thrown
        distSeminarDao.getSeminarForUID(SEMINAR_UID);
    }

    /**
     * Test method for {@link com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#createSeminar(com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData)}.
     */
    @Test
    public final void testCreateSeminar() {
        distSeminarDao.createSeminar(createSeminarData());
        final DistSeminarModel seminar = distSeminarDao.getSeminarForUID(SEMINAR_UID_2);
        assertNotNull("The seminar must be not null", seminar);
    }

    /**
     * Test method for {@link com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#registerForSeminar(java.lang.String, com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData)}.
     */
    @Test
    public final void testRegisterForSeminar() {
        final DistSeminarRegistrationData registrationData = createRegistration();
        for (int i = 0; i < 5; i++) {
            distSeminarDao.registerForSeminar(SEMINAR_UID, registrationData);
        }

        final DistSeminarModel seminar = distSeminarDao.getSeminarForUID(SEMINAR_UID);
        assertNotNull("The seminar must be not null", seminar);
        assertNotNull("The seminar registration list must be not null", seminar.getRegistrations());
        assertFalse("The seminar registration list must be not empty", seminar.getRegistrations().isEmpty());
    }

    private DistSeminarData createSeminarData() {
        final DistSeminarData seminar = new DistSeminarData();
        seminar.setUid(SEMINAR_UID_2);
        seminar.setDate(new Date(System.currentTimeMillis() + 123456789L));
        seminar.setRegistrationDelay(new Date(System.currentTimeMillis() + 987654321L));
        seminar.setTopic("Something intersting");
        seminar.setNote("No comment!");
        seminar.setContent("This is a very interesting topic");
        seminar.setCostDesc("CHF 200 all incl.");
        seminar.setLocation("Distrelec Nanikon");
        seminar.setSpeakers("Me and You :)");

        return seminar;
    }

    private DistSeminarRegistrationData createRegistration() {
        final DistSeminarRegistrationData registration = new DistSeminarRegistrationData();
        registration.setTopic("some topic");
        registration.setDate(new Date());
        registration.setCustomerNumber(String.valueOf(RAND.nextInt()));
        registration.setCompanyName("CompanyName #" + RAND.nextInt());
        registration.setFirstName("FirstName #" + RAND.nextInt());
        registration.setLastName("LastName #" + RAND.nextInt());
        registration.seteMail("email.email@email.com");
        registration.setSalutation("me");
        registration.setStreet("somewhere in the world #" + RAND.nextInt());
        registration.setNumber(String.valueOf(RAND.nextInt(100)));
        registration.setPlace("City #" + RAND.nextInt());
        registration.setZip(String.valueOf(RAND.nextInt(2000)));
        registration.setCountry("Switzerland");
        registration.setDirectPhone(String.valueOf(RAND.nextInt()));
        registration.setDepartment("Dep. #" + RAND.nextInt());

        return registration;
    }

}
