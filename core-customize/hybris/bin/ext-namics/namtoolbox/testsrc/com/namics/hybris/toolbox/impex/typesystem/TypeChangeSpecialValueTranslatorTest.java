/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.typesystem;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;
import de.hybris.platform.testframework.runlistener.ItemCreationListener;
import de.hybris.platform.testframework.runlistener.LogRunListener;
import de.hybris.platform.testframework.runlistener.PlatformRunListener;
import de.hybris.platform.testframework.RunListeners;
import de.hybris.platform.testframework.runlistener.TransactionRunListener;
import de.hybris.platform.testframework.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;

@RunWith(HybrisJUnit4ClassRunner.class)
@RunListeners({ TransactionRunListener.class, ItemCreationListener.class, LogRunListener.class, PlatformRunListener.class })
@Transactional
public class TypeChangeSpecialValueTranslatorTest {

    @Test
    public void testPerformImport() throws ImpExException, FileNotFoundException, IOException, ConsistencyCheckException {
        final Resource res = FileUtils.createResourceFromFilepath("/test/importexport/TypeChangeSpecialValueTranslatorTest.impex", true);

        final String impexString = FileUtils.readFile(res.getInputStream());
        final InputStream impexInputStream = new ByteArrayInputStream(impexString.getBytes());

        final Importer importer = ImpExManager.getInstance().importDataLight(impexInputStream, "UTF-8", true);

        Assert.assertFalse("There were errors during import.", importer.hadError());
        Assert.assertFalse("Import was aborted.", importer.isAborted());
        Assert.assertFalse("Import had unresolved lines.", importer.hasUnresolvedLines());

        final User newCreatedUser1 = UserManager.getInstance().getUserByLogin("newCreatedUser1");
        final User newCreatedUser2 = UserManager.getInstance().getUserByLogin("newCreatedUser2");

        final ComposedType userComposedType = TypeManager.getInstance().getComposedType("user");
        final ComposedType customerComposedType = TypeManager.getInstance().getComposedType("customer");

        Assert.assertEquals(userComposedType, newCreatedUser1.getComposedType());
        Assert.assertEquals(userComposedType, newCreatedUser2.getComposedType());

        Assert.assertFalse("Anonymous is still a 'Customer'.", newCreatedUser1.getComposedType().equals(customerComposedType));
        Assert.assertFalse("The new created user is still a 'Customer'.", newCreatedUser2.getComposedType().equals(customerComposedType));

    }

}
