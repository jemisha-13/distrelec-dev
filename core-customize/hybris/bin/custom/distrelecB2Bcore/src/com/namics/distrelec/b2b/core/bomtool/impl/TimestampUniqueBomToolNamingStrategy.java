package com.namics.distrelec.b2b.core.bomtool.impl;

import com.namics.distrelec.b2b.core.bomtool.BomToolNamingStrategy;
import com.namics.distrelec.b2b.core.service.bomtool.dao.DistBomImportDao;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimestampUniqueBomToolNamingStrategy implements BomToolNamingStrategy {

    private static final String TIMESTAMP_FORMAT = "uuuu-MM-dd_HH:mm:ss.SSS";

    @Autowired
    private DistBomImportDao distBomImportDao;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private UserService userService;

    @Override
    public String getAvailableName(String requestedName) {
        return distBomImportDao.findBomImportByCustomerAndName(getCurrentCustomer(), requestedName)
                .map(existingFile -> appendTimestamp(requestedName))
                .orElse(requestedName);
    }

    private String appendTimestamp(String name) {
        TimeZone timeZone = i18NService.getCurrentTimeZone();
        LocalDateTime now = LocalDateTime.now(timeZone.toZoneId());
        String formattedTimestamp = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).format(now);
        return name + " " + formattedTimestamp;
    }

    private CustomerModel getCurrentCustomer() {
        return (CustomerModel) userService.getCurrentUser();
    }

}