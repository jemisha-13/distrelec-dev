package com.namics.distrelec.b2b.facades.user.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.core.model.user.TitleModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistTitlePopulatorUnitTest {

    @Mock
    private TitleModel source;

    @InjectMocks
    private DistTitlePopulator populator;

    @Test
    public void testPopulateSapCode() {
        // given
        TitleData target = new TitleData();

        // when
        when(source.getSapCode()).thenReturn("SAP123");

        populator.populate(source, target);

        // then
        assertThat(target.getSapCode(), equalTo("SAP123"));
    }

    @Test
    public void testPopulateActiveStatusTrue() {
        // given
        TitleData target = new TitleData();

        // when
        when(source.isActive()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isActive(), is(true));
    }

    @Test
    public void testPopulateActiveStatusFalse() {
        // given
        TitleData target = new TitleData();

        // when
        when(source.isActive()).thenReturn(false);

        populator.populate(source, target);

        // then
        assertThat(target.isActive(), is(false));
    }
}
