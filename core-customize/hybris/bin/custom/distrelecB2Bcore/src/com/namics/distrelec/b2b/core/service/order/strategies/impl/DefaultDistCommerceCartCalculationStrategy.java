package com.namics.distrelec.b2b.core.service.order.strategies.impl;

import com.namics.distrelec.b2b.core.inout.erp.exception.MoqConversionException;
import com.namics.distrelec.b2b.core.inout.erp.exception.TemporaryQualityBlockException;
import com.namics.distrelec.b2b.core.inout.erp.exception.ProductStatusMisalignmentException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.tx.Transaction;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class DefaultDistCommerceCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy {
    private boolean calculateExternalTaxes = false;

    @Override
    public boolean calculateCart(final CommerceCartParameter parameter) {
        final CartModel cartModel = parameter.getCart();

        validateParameterNotNull(cartModel, "Cart model cannot be null");

        final CalculationService calcService = getCalculationService();
        boolean recalculated = false;
        if (calcService.requiresCalculation(cartModel)) {
            final Transaction tx = Transaction.current();
            tx.begin();
            boolean rollbackNeeded = true;
            try {
                try {
                    parameter.setRecalculate(false);
                    beforeCalculate(parameter);
                    calcService.calculate(cartModel);
                    getPromotionsService().updatePromotions(getPromotionGroups(), cartModel, true, PromotionsManager.AutoApplyMode.APPLY_ALL,
                                                            PromotionsManager.AutoApplyMode.APPLY_ALL, getTimeService().getCurrentTime());
                    rollbackNeeded = false;
                } catch (ProductStatusMisalignmentException exception) {
                    rollbackNeeded = false;
                    throw exception;
                } catch (MoqConversionException exception) {
                    rollbackNeeded = false;
                    throw exception;
                } catch (TemporaryQualityBlockException exception) {
                    rollbackNeeded = false;
                    throw exception;
                } catch (final CalculationException calculationException) {
                    throw new IllegalStateException(
                                                    "Cart model " + cartModel.getCode() + " was not calculated due to: " + calculationException.getMessage(),
                                                    calculationException);
                } finally {
                    afterCalculate(parameter);
                }
                recalculated = true;
            } finally {
                if (rollbackNeeded || tx.isRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }
        }
        if (calculateExternalTaxes) {
            getExternalTaxesService().calculateExternalTaxes(cartModel);
        }
        return recalculated;
    }
}
