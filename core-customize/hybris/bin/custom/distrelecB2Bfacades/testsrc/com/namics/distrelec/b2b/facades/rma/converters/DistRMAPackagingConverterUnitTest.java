package com.namics.distrelec.b2b.facades.rma.converters;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.facades.rma.data.DistRMAPackagingData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistRMAPackagingConverterUnitTest {

    @Mock
    private DistCodelistModel source;

    @InjectMocks
    private DistRMAPackagingConverter distRMAPackagingConverter;

    private DistRMAPackagingData target = new DistRMAPackagingData();

    @Test
    public void testPopulateCode() {
        // given
        when(source.getCode()).thenReturn("TestCode");

        // when
        distRMAPackagingConverter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("TestCode"));
    }

    @Test
    public void testPopulateName() {
        // given
        when(source.getName()).thenReturn("TestName");

        // when
        distRMAPackagingConverter.populate(source, target);

        // then
        assertThat(target.getName(), equalTo("TestName"));
    }

    @Test
    public void testPopulateRelevantName() {
        // given
        when(source.getRelevantName()).thenReturn("TestRelevantName");

        // when
        distRMAPackagingConverter.populate(source, target);

        // then
        assertThat(target.getRelevantName(),equalTo("TestRelevantName"));
    }
}
