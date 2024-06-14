package com.namics.distrelec.b2b.core.service.netmargin.impl;

import com.namics.distrelec.b2b.core.service.netmargin.NetMarginConfiguration;
import com.namics.distrelec.b2b.core.service.netmargin.NetMarginConfigurationLoader;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetMarginConfigurationLoaderImpl implements NetMarginConfigurationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(NetMarginConfigurationLoaderImpl.class);

    private final ConfigurationService configurationService;

    public NetMarginConfigurationLoaderImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public NetMarginConfiguration loadNetMarginConfiguration() {
        Configuration configuration = configurationService.getConfiguration().subset("netmargin.rank.lessThen");

        SortedSet<Integer> marginValues = new TreeSet<>();
        Pattern marginValueRegex = Pattern.compile("([0-9]*|\\*).percentage.([0-9_]*|\\*)");
        configuration.getKeys().forEachRemaining(key -> {
            Matcher matcher = marginValueRegex.matcher(key);
            if(matcher.matches()) {
                String matchedText = matcher.group(1);
                if (matchedText.equals("*")) {
                    marginValues.add(Integer.MAX_VALUE);
                } else {
                    marginValues.add(Integer.parseInt(matchedText));
                }
            }else{
                LOG.error("Found invalid netmargin config line: {}", "netmargin.rank.lessThen." + key);
            }
        });

        SortedSet<NetMarginConfiguration.PerValueRange> perValueRanges = new TreeSet<>();
        Pattern marginPercentageRegex = Pattern.compile("percentage.([0-9_]*|\\*)");
        for (int marginValue : marginValues) {
            String prefix = marginValue == Integer.MAX_VALUE ? "*" : String.valueOf(marginValue);
            Configuration perValueConfiguration = configuration.subset(prefix);

            SortedSet<NetMarginConfiguration.PerPercentageRange> perPercentageRanges = new TreeSet<>();
            perValueConfiguration.getKeys().forEachRemaining(key -> {
                Matcher matcher = marginPercentageRegex.matcher(key);
                if(matcher.matches()) {
                    String matchedText = matcher.group(1);
                    float marginPercentage = 1.0f;
                    if (!matchedText.equals("*")) {
                        marginPercentage = Float.parseFloat(matchedText.replace("_", "."));
                    }
                    int rank = perValueConfiguration.getInt(key);
                    perPercentageRanges.add(new NetMarginConfiguration.PerPercentageRange(marginPercentage, rank));
                }
            });
            perValueRanges.add(new NetMarginConfiguration.PerValueRange(marginValue, perPercentageRanges));
        }

        return new NetMarginConfiguration(perValueRanges);
    }

}
