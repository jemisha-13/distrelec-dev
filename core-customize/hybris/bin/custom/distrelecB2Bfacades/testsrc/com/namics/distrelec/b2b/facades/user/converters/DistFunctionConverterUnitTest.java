package com.namics.distrelec.b2b.facades.user.converters;

import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistFunctionConverterUnitTest {

    @Mock
    private DistFunctionModel source;

    @InjectMocks
    private DistFunctionConverter converter;

    @Test
    public void testPopulateCode() {
        // given
        DistFunctionData target = new DistFunctionData();

        // when
        when(source.getCode()).thenReturn("F123");

        converter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("F123"));
    }

    @Test
    public void testPopulateName() {
        // given
        DistFunctionData target = new DistFunctionData();

        // when
        when(source.getRelevantName()).thenReturn("Relevant Function Name");

        converter.populate(source, target);

        // then
        assertThat(target.getName(), equalTo("Relevant Function Name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullSource() {
        // given
        DistFunctionData target = new DistFunctionData();

        // when
        converter.populate(null, target);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullTarget() {
        // when
        converter.populate(source, null);
    }

    @Test
    public void testCreateTarget() {
        // when
        DistFunctionData target = converter.createTarget();

        // then
        assertThat(target, is(notNullValue()));
    }

}
