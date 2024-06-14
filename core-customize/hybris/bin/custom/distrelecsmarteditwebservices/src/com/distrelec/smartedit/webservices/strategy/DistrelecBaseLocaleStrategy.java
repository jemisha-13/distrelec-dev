package com.distrelec.smartedit.webservices.strategy;

import java.util.Locale;

public interface DistrelecBaseLocaleStrategy {

    Locale setLocaleToBase(Locale locale);

    void revertLocale(Locale locale);
}
