package com.namics.distrelec.b2b.facades.order.comparators;

import com.namics.distrelec.b2b.facades.order.data.DistAvailabilityData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Comparator;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistAvailabilityDataComparatorUnitTest {

    private DistAvailabilityData availabilityData1;
    private DistAvailabilityData availabilityData2;
    private Comparator<DistAvailabilityData> comparator;

    @Before
    public void setUp() {
        availabilityData1 = mock(DistAvailabilityData.class);
        availabilityData2 = mock(DistAvailabilityData.class);
        comparator = new DistAvailabilityDataComparator();
    }

    @Test
    public void testComparatorEqualDates() {
        // given
        Date date = new Date();

        // when
        when(availabilityData1.getEstimatedDate()).thenReturn(date);
        when(availabilityData2.getEstimatedDate()).thenReturn(date);

        int result = comparator.compare(availabilityData1, availabilityData2);

        // then
        assertThat(result, equalTo(0));
    }

    @Test
    public void testComparatorFirstDateBeforeSecond() {
        // given
        Date date1 = new Date(1000);
        Date date2 = new Date(2000);

        // when
        when(availabilityData1.getEstimatedDate()).thenReturn(date1);
        when(availabilityData2.getEstimatedDate()).thenReturn(date2);

        int result = comparator.compare(availabilityData1, availabilityData2);

        // then
        assertThat(result, equalTo(-1));
    }

    @Test
    public void testComparatorFirstDateAfterSecond() {
        // given
        Date date1 = new Date(2000);
        Date date2 = new Date(1000);

        // when
        when(availabilityData1.getEstimatedDate()).thenReturn(date1);
        when(availabilityData2.getEstimatedDate()).thenReturn(date2);


        int result = comparator.compare(availabilityData1, availabilityData2);

        // then
        assertThat(result, equalTo(1));
    }

    @Test(expected = NullPointerException.class)
    public void testComparatorWithNullFirstDate() {
        // when
        when(availabilityData1.getEstimatedDate()).thenReturn(null);
        when(availabilityData2.getEstimatedDate()).thenReturn(new Date());

        comparator.compare(availabilityData1, availabilityData2);
    }

    @Test(expected = NullPointerException.class)
    public void testComparatorWithNullSecondDate() {
        // when
        when(availabilityData1.getEstimatedDate()).thenReturn(new Date());
        when(availabilityData2.getEstimatedDate()).thenReturn(null);

        comparator.compare(availabilityData1, availabilityData2);
    }

    @Test(expected = NullPointerException.class)
    public void testComparatorWithBothDatesNull() {
        // given
        when(availabilityData1.getEstimatedDate()).thenReturn(null);
        when(availabilityData2.getEstimatedDate()).thenReturn(null);

        // when
        comparator.compare(availabilityData1, availabilityData2);
    }
}
