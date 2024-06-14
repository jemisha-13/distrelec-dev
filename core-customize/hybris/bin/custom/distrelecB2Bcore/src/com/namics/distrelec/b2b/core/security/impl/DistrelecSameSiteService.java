package com.namics.distrelec.b2b.core.security.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.security.SameSiteService;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistrelecSameSiteService implements SameSiteService {

    @Autowired
    private UserService userService;

    @Autowired
    private DistAribaService distAribaService;

    @Override
    public boolean is3rdPartyCookieAccessRequired() {
        return isEProcurementUser();
    }

    private boolean isEProcurementUser() {
        return distAribaService.isAribaCustomer() || isOciCustomer();
    }

    private boolean isOciCustomer() {
        final UserModel currentUser = userService.getCurrentUser();
        final UserGroupModel userGroup = userService.getUserGroupForUID(DistConstants.User.OCICUSTOMERGROUP_UID);
        if (currentUser == null || userGroup == null) {
            return false;
        }
        return userService.isMemberOfGroup(currentUser, userGroup);
    }

    @Override
    public void allow3rdPartyCookieAccess(HttpServletRequest request, HttpServletResponse response) {
        if (isUserAgentSupported(request)) {
            Collection<String> setCookieHeaders = response.getHeaders("Set-Cookie");
            Iterator<String> setCookieHeadersIterator = setCookieHeaders.iterator();

            if (setCookieHeadersIterator.hasNext()) {
                String firstSetCookieValue = setCookieHeadersIterator.next();
                response.setHeader("Set-Cookie", firstSetCookieValue + "; SameSite=None");
            }

            while (setCookieHeadersIterator.hasNext()) {
                String nextSetCookieValue = setCookieHeadersIterator.next();
                response.addHeader("Set-Cookie", nextSetCookieValue + "; SameSite=None");
            }
        }
    }

    @Override
    public boolean isUserAgentSupported(HttpServletRequest request) {
        String useragent = request.getHeader("User-Agent");
        return !isUserAgentIncompatible(useragent);
    }

    private boolean isUserAgentIncompatible(String useragent) {
        return hasWebKitSameSiteBug(useragent) || dropsUnrecoginizedSameSiteCookies(useragent);
    }

    private boolean hasWebKitSameSiteBug(String useragent) {
        return isIosVersion(12, useragent) ||
                (isMacOsXVersion(10, 14, useragent) &&
                        (isSafari(useragent) || isMacEmbeddedBrowser(useragent)));
    }

    private boolean dropsUnrecoginizedSameSiteCookies(String useragent) {
        if (isUcBrowser(useragent)) {
            return !isUcBrowserVersionAtLeast(12, 13, 2, useragent);
        } else {
            return isChromiumBased(useragent) &&
                    isChromiumVersionAtLeast(51, useragent) && !isChromiumVersionAtLeast(67, useragent);
        }
    }

    private boolean isIosVersion(int major, String useragent) {
        Pattern regex = Pattern.compile("\\(iP.+; CPU .*OS (\\d+)[_\\d]*.*\\) AppleWebKit\\/");
        if(null!=regex && null!=useragent) { 
            Matcher matcher = regex.matcher(useragent);
    
            if(null==matcher || !matcher.find()){
                return false;
            }
    
            int majorVersion = Integer.parseInt(matcher.group(1));
            return major == majorVersion;
        }
        return false;
    }

    private boolean isMacOsXVersion(int major, int minor, String useragent) {
        Pattern regex = Pattern.compile("\\(Macintosh;.*Mac OS X (\\d+)_(\\d+)[_\\d]*.*\\) AppleWebKit\\/");
        if(null!=regex && null!=useragent) {
            Matcher matcher = regex.matcher(useragent);
    
            if(null==matcher || !matcher.find()){
                return false;
            }
    
            int majorVersion = Integer.parseInt(matcher.group(1));
            int minorVersion = Integer.parseInt(matcher.group(2));
            return major == majorVersion && minor == minorVersion;
        }
        return false;
    }

    private boolean isSafari(String useragent) {
        Pattern regex = Pattern.compile("Version\\/.* Safari\\/");
        Matcher matcher = regex.matcher(useragent);
        return matcher.find();
    }

    private boolean isMacEmbeddedBrowser(String useragent) {
        Pattern regex = Pattern.compile("^Mozilla\\/[\\.\\d]+ \\(Macintosh;.*Mac OS X [_\\d]+\\) AppleWebKit\\/[\\.\\d]+ \\(KHTML, like Gecko\\)$");
        Matcher matcher = regex.matcher(useragent);
        return matcher.find();
    }

    private boolean isChromiumBased(String useragent) {
        Pattern regex = Pattern.compile("Chrom(e|ium)");
        Matcher matcher = regex.matcher(useragent);
        return matcher.find();
    }

    private boolean isChromiumVersionAtLeast(int major, String useragent) {
        Pattern regex = Pattern.compile("Chrom[^ \\/]+\\/(\\d+)[\\.\\d]* ");
        Matcher matcher = regex.matcher(useragent);

        if(null==matcher || !matcher.find()){
            return false;
        }

        int majorVersion = Integer.parseInt(matcher.group(1));
        return majorVersion >= major;
    }

    private boolean isUcBrowser(String useragent) {
        Pattern regex = Pattern.compile("UCBrowser\\/");
        Matcher matcher = regex.matcher(useragent);
        return matcher.find();
    }

    private boolean isUcBrowserVersionAtLeast(int major, int minor, int build, String useragent) {
        Pattern regex = Pattern.compile("UCBrowser\\/(\\d+)\\.(\\d+)\\.(\\d+)[\\.\\d]* ");
        Matcher matcher = regex.matcher(useragent);

        if(!matcher.find()){
            return false;
        }

        int majorVersion = Integer.parseInt(matcher.group(1));
        int minorVersion = Integer.parseInt(matcher.group(2));
        int buildVersion = Integer.parseInt(matcher.group(3));
        if (majorVersion != major)
            return majorVersion > major;
        if (minorVersion != minor)
            return minorVersion > minor;
        return buildVersion >= build;
    }

}
