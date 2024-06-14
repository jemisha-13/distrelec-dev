package com.namics.distrelec.b2b.storefront.controllers.pages;

import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.TitleData;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public abstract class AbstractLoginPageControllerTest<T extends AbstractLoginPageController> extends AbstractPageControllerTest<AbstractLoginPageController>{

    @Mock
    private UserFacade userFacade;
    @Mock
    protected Model model;

    @Before
    public void setUp() throws Exception {
        setUp(getController());
        super.setUp();
        prepareMocks();
    }

    private void prepareMocks() {
        when(userFacade.getTitles()).thenReturn(createTitleData());



    }

    protected List<TitleData> createTitleData() {
        final TitleData titleData = new TitleData();
        titleData.setName("Mr");
        return Arrays.asList(titleData);
    }


    public void setUp(final AbstractLoginPageController controller) throws Exception {
        controller.setUserFacade(userFacade);
    }

    protected abstract T getController();
}
