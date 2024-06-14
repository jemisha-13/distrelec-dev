package com.namics.distrelec.b2b.facades.user.converters;

import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
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
public class DistDepartmentConverterUnitTest {

    @Mock
    private DistDepartmentModel source;

    @InjectMocks
    private DistDepartmentConverter converter;

    @Test
    public void testPopulateCode() {
        // given
        DistDepartmentData target = new DistDepartmentData();

        // when
        when(source.getCode()).thenReturn("DEPT123");

        converter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("DEPT123"));
    }

    @Test
    public void testPopulateName() {
        // given
        DistDepartmentData target = new DistDepartmentData();

        // when
        when(source.getRelevantName()).thenReturn("Relevant name");

        converter.populate(source, target);

        // then
        assertThat(target.getName(), equalTo("Relevant name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullSource() {
        // given
        DistDepartmentData target = new DistDepartmentData();

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
        DistDepartmentData target = converter.createTarget();

        // then
        assertThat(target, is(notNullValue()));
    }
}
