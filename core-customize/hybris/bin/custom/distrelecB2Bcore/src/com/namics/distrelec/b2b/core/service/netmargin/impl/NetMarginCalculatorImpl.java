package com.namics.distrelec.b2b.core.service.netmargin.impl;

import com.namics.distrelec.b2b.core.service.netmargin.NetMarginCalculator;
import com.namics.distrelec.b2b.core.service.netmargin.NetMarginConfiguration;
import com.namics.distrelec.b2b.core.service.netmargin.NetMarginConfigurationLoader;
import org.springframework.stereotype.Service;

import java.util.Comparator;

public class NetMarginCalculatorImpl implements NetMarginCalculator {

    private final NetMarginConfiguration netMarginConfiguration;

    public NetMarginCalculatorImpl(NetMarginConfigurationLoader netMarginConfigurationLoader) {
        netMarginConfiguration = netMarginConfigurationLoader.loadNetMarginConfiguration();
    }

    @Override
    public Integer calculateNetMarginRank(double netMargin, double netMarginPercentage) {
        return netMarginConfiguration.getPerValueRanges()
                .stream()
                .filter(perValueRange -> perValueRange.getMinValue() > netMargin)
                .min(Comparator.naturalOrder())
                .map(perValueRange -> perValueRange.getPerPercentageRanges()
                        .stream()
                        .filter(perPercentageRange -> perPercentageRange.getPercentage() > netMarginPercentage)
                        .min(Comparator.naturalOrder())
                        .map(correctRange -> correctRange.getRank())
                        .orElse(null)
                ).orElse(null);
    }

}
