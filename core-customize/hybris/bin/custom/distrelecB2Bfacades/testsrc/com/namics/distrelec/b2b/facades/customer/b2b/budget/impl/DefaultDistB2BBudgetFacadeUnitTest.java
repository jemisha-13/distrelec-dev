package com.namics.distrelec.b2b.facades.customer.b2b.budget.impl;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistB2BBudgetFacadeUnitTest {

    @Mock
    private DistB2BCommerceBudgetService distB2BCommerceBudgetService;

    @Mock
    private DistCartService cartService;

    @Mock
    private Converter<DistB2BBudgetModel, B2BBudgetData> b2bBudgetConverter;

    @Mock
    private ModelService modelService;

    @InjectMocks
    private DefaultDistB2BBudgetFacade defaultDistB2BBudgetFacade;

    @Test
    public void testGetBudgetForCustomer() {
        // given
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        DistB2BBudgetModel budgetModel = mock(DistB2BBudgetModel.class);
        B2BBudgetData budgetData = new B2BBudgetData();

        // when
        when(distB2BCommerceBudgetService.getActiveBudget(customer)).thenReturn(budgetModel);
        when(b2bBudgetConverter.convert(budgetModel)).thenReturn(budgetData);

        B2BBudgetData result = defaultDistB2BBudgetFacade.getBudgetForCustomer(customer);

        // then
        assertThat(result, equalTo(budgetData));
    }

    @Test
    public void testCalculateExceededBudgetWithBigDecimal() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setOrderBudget(BigDecimal.valueOf(2000));
        budget.setYearlyBudget(BigDecimal.valueOf(5000));
        budget.setOriginalYearlyBudget(BigDecimal.valueOf(7000));
        BigDecimal orderTotal = BigDecimal.valueOf(3000);

        // when
        defaultDistB2BBudgetFacade.calculateExceededBudget(budget, orderTotal);

        // then
        assertThat(budget.getExceededOrderBudget(), equalTo(new BigDecimal("1000")));
        assertThat(budget.getExceededYearlyBudget(), equalTo(new BigDecimal("0")));
    }

    @Test
    public void testDoesOrderRequireApproval() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setExceededOrderBudget(BigDecimal.valueOf(100));
        budget.setExceededYearlyBudget(BigDecimal.ZERO);

        // when
        boolean result = defaultDistB2BBudgetFacade.doesOrderRequireApproval(budget);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testGetExceededBudgetForCart() throws CartException {
        // given
        CartModel cartModel = mock(CartModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        DistB2BBudgetModel budgetModel = mock(DistB2BBudgetModel.class);
        B2BBudgetData budgetData = new B2BBudgetData();
        budgetData.setOrderBudget(new BigDecimal("400"));

        // when
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getTotalPrice()).thenReturn(600.0);
        when(cartModel.getUser()).thenReturn(customer);
        when(distB2BCommerceBudgetService.getActiveBudget(customer)).thenReturn(budgetModel);
        when(b2bBudgetConverter.convert(budgetModel)).thenReturn(budgetData);

        B2BBudgetData result = defaultDistB2BBudgetFacade.getExceededBudgetForCart();

        // then
        assertThat(result.getExceededOrderBudget(), equalTo(new BigDecimal("200.0")));
    }

    @Test(expected = CartException.class)
    public void testGetExceededBudgetForCartThrowException() throws CartException {
        // given
        CartModel cartModel = mock(CartModel.class);
        B2BCustomerModel customer = mock(B2BCustomerModel.class);

        // when
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getUser()).thenReturn(customer);
        when(distB2BCommerceBudgetService.getActiveBudget(customer)).thenReturn(null);

        defaultDistB2BBudgetFacade.getExceededBudgetForCart();
    }

    @Test
    public void testGetExceededBudgetValueWhenYearlyBudgetIsMoreThanZeroExceededYearlyBudgetReturned() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setExceededYearlyBudget(BigDecimal.valueOf(500));
        budget.setExceededOrderBudget(BigDecimal.valueOf(100));

        // when
        BigDecimal result = defaultDistB2BBudgetFacade.getExceededBudgetValue(budget);

        // then
        assertThat(result, equalTo(BigDecimal.valueOf(500)));
    }

    @Test
    public void testGetExceededBudgetValueWhenYearlyIsZeroExceededOrderBudgetIsReturned() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setExceededYearlyBudget(BigDecimal.ZERO);
        budget.setExceededOrderBudget(BigDecimal.valueOf(100));

        // when
        BigDecimal result = defaultDistB2BBudgetFacade.getExceededBudgetValue(budget);

        // then
        assertThat(result, equalTo(BigDecimal.valueOf(100)));
    }

    @Test
    public void testGetExceededBudgetValueWhenBothBudgetsAreZero() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setExceededYearlyBudget(BigDecimal.ZERO);
        budget.setExceededOrderBudget(BigDecimal.ZERO);

        // when
        BigDecimal result = defaultDistB2BBudgetFacade.getExceededBudgetValue(budget);

        // then
        assertThat(result, equalTo(BigDecimal.ZERO));
    }

    @Test
    public void testCalculateExceededYearlyBudgetMoreThanZero() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setYearlyBudget(BigDecimal.valueOf(5000));
        budget.setOriginalYearlyBudget(BigDecimal.valueOf(7000));
        BigDecimal orderTotal = BigDecimal.valueOf(6000);
        BigDecimal expectedExceededYearlyBudget = BigDecimal.valueOf(1000);

        // when
        defaultDistB2BBudgetFacade.calculateExceededBudget(budget, orderTotal);

        // then
        assertThat(budget.getExceededYearlyBudget(), equalTo(expectedExceededYearlyBudget));
    }

    @Test
    public void testDoesOrderNotRequireApproval() {
        // given
        B2BBudgetData budget = new B2BBudgetData();
        budget.setExceededOrderBudget(BigDecimal.ZERO);
        budget.setExceededYearlyBudget(BigDecimal.ZERO);

        // when
        boolean result = defaultDistB2BBudgetFacade.doesOrderRequireApproval(budget);

        // then
        assertThat(result, is(false));
    }
}
