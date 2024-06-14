package com.namics.distrelec.b2b.backoffice.widgets.collectionbrowser.mold.impl.listview.renderer;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.services.media.ObjectPreview;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.impl.listview.renderer.DefaultPreviewListCellRenderer;
import de.hybris.platform.util.MediaUtil;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listcell;

/**
 * {@link #getUrl(ObjectPreview)} method is different from default implementation.
 */
public class DefaultDistPreviewListCellRenderer extends DefaultPreviewListCellRenderer {

    @Override
    public void render(Listcell listcell, ListColumn configuration, Object data, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        ObjectPreview preview = this.getObjectPreview(configuration, data, dataType, widgetInstanceManager);
        if (preview != null) {
            String url = getUrl(preview);
            Image image = new Image(url);
            UITools.modifySClass(image, "ye-listview-def-preview-img", true);
            listcell.appendChild(image);
            this.fireComponentRendered(image, listcell, configuration, data);
            if (!preview.isFallback()) {
                this.appendPopupPreview(listcell, configuration, data, preview.getUrl(), image, "ye-listview-preview-popup-image");
            }
        }

        this.fireComponentRendered(listcell, configuration, data);
    }

    private String getUrl(ObjectPreview preview) {
        String url = preview.getUrl();
        String localMediaRootUrl = MediaUtil.getLocalMediaWebRootUrl();

        if (url.startsWith(localMediaRootUrl)) {
            return url;
        } else {
            return UITools.adjustURL(url);
        }
    }
}
