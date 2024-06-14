package com.namics.distrelec.b2b.core.security;

import com.namics.distrelec.b2b.core.security.impl.DistrelecSameSiteService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@UnitTest
public class DistrelecSameSiteServiceUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    HttpServletRequest mockRequest;

    private DistrelecSameSiteService distrelecSameSiteService = new DistrelecSameSiteService();

    private void mockUserAgent(String userAgent){
        when(mockRequest.getHeader("User-Agent")).thenReturn(userAgent);
    }

    @Test
    public void testIosVersion(){
        //iOS 12
        mockUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //iOS 13
        mockUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();
    }

    @Test
    public void testOSX(){
        //OSX 10.14 - Safari
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //OSX 10.15 - Safari
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();

        //OSX 10.14 - Embedded
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko)");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //OSX 10.15 - Embedded
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko)");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();
    }

    @Test
    public void testUCBrowser(){
        //UCBrowser 11.13.2
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.13.2.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //UCBrowser 12.12.2
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/12.12.2.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //UCBrowser 12.13.1
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/12.13.1.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //UCBrowser 12.13.2
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/12.13.2.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();

        //UCBrowser 13.13.2
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/13.13.2.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();

        //UCBrowser 12.14.2
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/12.14.2.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();

        //UCBrowser 12.13.3
        mockUserAgent("Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; F5121 Build/34.0.A.1.247) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/12.13.3.944 Mobile Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();
    }

    @Test
    public void testChrome(){
        //Chromium 50
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.3987.132 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();

        //Chromium 51
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.3987.132 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //Chromium 66
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3987.132 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //Chromium 67
        mockUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3987.132 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();
    }

    @Test
    public void testChromium(){
        //Chromium 50
        mockUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/50.0.3325.181 Chrome/50.0.3325.181 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();

        //Chromium 51
        mockUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/51.0.3325.181 Chrome/51.0.3325.181 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //Chromium 66
        mockUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/66.0.3325.181 Chrome/66.0.3325.181 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isFalse();

        //Chromium 67
        mockUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/67.0.3325.181 Chrome/67.0.3325.181 Safari/537.36");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();
    }

    @Test
    public void testUnknown(){
        mockUserAgent("Dummy user-agent 10.12.1231");
        assertThat(distrelecSameSiteService.isUserAgentSupported(mockRequest))
                .isTrue();
    }

}
