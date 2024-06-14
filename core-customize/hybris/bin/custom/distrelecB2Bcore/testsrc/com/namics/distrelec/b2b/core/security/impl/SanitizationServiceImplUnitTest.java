package com.namics.distrelec.b2b.core.security.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SanitizationServiceImplUnitTest {

    private final SanitizationServiceImpl sanitizationService = new SanitizationServiceImpl();

    @Test
    public void testRemovePeriodsNullInputReturnNull() {
        // given
        String input = null;

        // when
        String result = sanitizationService.removePeriods(input);

        // then
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testRemovePeriodsEmptyStringInputReturnEmptyString() {
        // given
        String input = StringUtils.EMPTY;

        // when
        String result = sanitizationService.removePeriods(input);

        // then
        assertThat(result, is(StringUtils.EMPTY));
    }

    @Test
    public void testRemovePeriodsNoPeriodsReturnOriginalString() {
        // given
        String input = "TestStringWithoutPeriods";

        // when
        String result = sanitizationService.removePeriods(input);

        // then
        assertThat(result, is(input));
    }

    @Test
    public void testRemovePeriodsInputStringWithPeriodsReturnStringWithoutPeriods() {
        // given
        String input = "Test.String.With.Periods.";
        String expected = "TestStringWithPeriods";

        // when
        String result = sanitizationService.removePeriods(input);

        // then
        assertThat(result, is(expected));
    }

    @Test
    public void testRemovePeriodsInputIsAllPeriodsReturnEmptyString() {
        // given
        String input = "....";

        // when
        String result = sanitizationService.removePeriods(input);

        // then
        assertThat(result, is(StringUtils.EMPTY));
    }
}
