package com.namics.distrelec.b2b.facades.customer.b2b.budget.converters;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.StandardDateRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BBudgetConverterUnitTest {

    @Mock
    private DistB2BBudgetModel source;

    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @InjectMocks
    private DistB2BBudgetConverter distB2BBudgetConverter;

    B2BBudgetData target = new B2BBudgetData();

    @Test
    public void testPopulateCode() {
        // given
        String code = "BudgetCode";
        Date startDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getStart()).thenReturn(startDate);
        when(source.getCode()).thenReturn(code);

        distB2BBudgetConverter.populate(source, target);

        assertThat(target.getCode(), equalTo(code));
    }

    @Test
    public void testPopulateName() {
        // given
        String name = "BudgetName";
        Date startDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getStart()).thenReturn(startDate);
        when(source.getName()).thenReturn(name);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getName(), equalTo(name));
    }

    @Test
    public void testPopulateActive() {
        // given
        boolean active = true;
        Date startDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getStart()).thenReturn(startDate);
        when(source.getActive()).thenReturn(active);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.isActive(), is(active));
    }

    @Test
    public void testPopulateStartDate() {
        // given
        Date startDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getStart()).thenReturn(startDate);
        when(standardDateRange.getStart()).thenReturn(startDate);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getStartDate(), is(startDate));
    }

    @Test
    public void testPopulateCurrency() {
        // given
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        CurrencyData currencyData = new CurrencyData();
        Date startDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getStart()).thenReturn(startDate);
        when(source.getCurrency()).thenReturn(currencyModel);
        when(currencyConverter.convert(currencyModel)).thenReturn(currencyData);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getCurrency(), equalTo(currencyData));
    }

    @Test
    public void testPopulateEndDate() {
        // given
        Date endDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getEnd()).thenReturn(endDate);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getEndDate(), is(endDate));
    }

    @Test
    public void testPopulateOrderBudget() {
        // given
        Date endDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getOriginalYearlyBudget()).thenReturn(BigDecimal.valueOf(1000.00));
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getEnd()).thenReturn(endDate);
        when(source.getOrderBudget()).thenReturn(BigDecimal.valueOf(1000.00));

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getOrderBudget(), is(BigDecimal.valueOf(1000.00)));
    }

    @Test
    public void testPopulateOriginalYearlyBudget() {
        // given
        Date endDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getOriginalYearlyBudget()).thenReturn(BigDecimal.valueOf(1000.00));
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getEnd()).thenReturn(endDate);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getOriginalYearlyBudget(), is(BigDecimal.valueOf(1000.00)));
    }

    @Test
    public void testPopulateYearlyBudget() {
        // given
        Date endDate = new Date();
        StandardDateRange standardDateRange = mock(StandardDateRange.class);

        // when
        when(source.getYearlyBudget()).thenReturn(BigDecimal.valueOf(1000.00));
        when(source.getDateRange()).thenReturn(standardDateRange);
        when(standardDateRange.getEnd()).thenReturn(endDate);

        distB2BBudgetConverter.populate(source, target);

        // then
        assertThat(target.getYearlyBudget(), is(BigDecimal.valueOf(1000.00)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullSource() {
        // given
        DistB2BBudgetModel source = null;
        B2BBudgetData target = new B2BBudgetData();

        // when
        distB2BBudgetConverter.populate(source, target);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullTarget() {
        // given
        DistB2BBudgetModel source = mock(DistB2BBudgetModel.class);
        B2BBudgetData target = null;

        // when
        distB2BBudgetConverter.populate(source, target);
    }
}
