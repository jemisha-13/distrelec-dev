/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code SaltedIShopMD5PasswordEncoderTest}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class SaltedIShopMD5PasswordEncoderTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;
//    @Resource(name = "core.saltedMD5IshopPasswordEncoder")
    private SaltedIShopMD5PasswordEncoder encoder;

    private final static String I_SALT = "eiPh0ui4oluuJai0jie7thooB7Hiemei";
    private final static String PW1_PLAIN = "123456";
    private final static String PW1_HASH = "e81dea40badfe7906e83f1ec439a1249";
    private final static String PW2_PLAIN = "hello_world";
    private final static String PW2_HASH = "ebf35a67622e832a5e2389db6880bc00";

    @Before
    public void setUp() throws Exception {
        // NOP
    }

    @Test
    @Ignore
    public final void testEncodeString() {
        assertNotNull("The encoder must not be null", encoder);
        assertEquals("The hash must be equals to " + PW1_HASH, PW1_HASH, encoder.encode("some_user", PW1_PLAIN));
        assertEquals("The hash must be equals to " + PW2_HASH, PW2_HASH, encoder.encode(null, PW2_PLAIN));
    }

    @Test
    @Ignore
    public final void testIsSaltedAlready() {
        assertNotNull("The encoder must not be null", encoder);
        final String str = I_SALT + "test_string";
        assertTrue("The result must be TRUE since the string param is salted", encoder.isSaltedAlready(str));
        assertFalse("The result must be FALSE since the string param is not salted", encoder.isSaltedAlready("random string for test"));
    }

    @Test
    @Ignore
    public final void testGetSystemSpecificSalt() {
        assertNotNull("The encoder must not be null", encoder);
        assertEquals("The salts must be the same", encoder.getSystemSpecificSalt(), encoder.getSalt());
    }

    @Test
    @Ignore
    public final void testGetSalt() {
        assertNotNull("The encoder must not be null", encoder);
        assertEquals("The salt must have the value : " + I_SALT, I_SALT, encoder.getSalt());
    }

}
