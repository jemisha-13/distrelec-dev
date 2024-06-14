package com.namics.distrelec.b2b.backoffice.editors;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

public class FutureStockFinderEditor extends AbstractComponentWidgetAdapterAware implements CockpitEditorRenderer<Object> {

    private static final Logger LOG = Logger.getLogger(FutureStockFinderEditor.class);

    @Override
    public void render(Component parent, EditorContext<Object> editorContext, EditorListener<Object> editorListener) {
        Div cnt = new Div();
        Button button = this.createFinderButton();
        button.addEventListener("onClick", (event) -> {
            AdvancedSearchData searchData = new AdvancedSearchData();
            searchData.setTypeCode(FutureStockModel._TYPECODE);
            WidgetInstanceManager wim = (WidgetInstanceManager)editorContext.getParameter("wim");
            searchData.setGlobalOperator(ValueComparisonOperator.AND);
            AdvancedSearchInitContext initContext = createSearchContext(searchData, wim, editorContext);
            sendOutput("finderOutput", initContext);
        });
        parent.appendChild(cnt);
        cnt.appendChild(button);
    }


    protected Button createFinderButton() {
        return new Button(Labels.getLabel("hmc.findfuturestockforproduct"));
    }

    protected AdvancedSearchInitContext createSearchContext(AdvancedSearchData searchData, WidgetInstanceManager wim, EditorContext<Object> editorContext) {
        AdvancedSearch config = this.loadAdvancedConfiguration(wim, FutureStockModel._TYPECODE);

        for (FieldType field : config.getFieldList().getField()){
            if (FutureStockModel.PRODUCTCODE.equals(field.getName())) {
                Object parentObject = editorContext.getParameter("parentObject");
                ProductModel product = (ProductModel) parentObject;
                searchData.addCondition(field, ValueComparisonOperator.STARTS_WITH, product.getCode());
            }
        }

        return new AdvancedSearchInitContext(searchData, config);
    }

    protected AdvancedSearch loadAdvancedConfiguration(WidgetInstanceManager wim, String name) {
        DefaultConfigContext context = new DefaultConfigContext(name, FutureStockModel._TYPECODE);

        try {
            return wim.loadConfiguration(context, AdvancedSearch.class);
        } catch (CockpitConfigurationException ex) {
            LOG.error("Failed to load advanced configuration.", ex);
            return null;
        }
    }
}
