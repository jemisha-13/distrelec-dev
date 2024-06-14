package com.namics.distrelec.b2b.backoffice.cockpitng.collectionbrowser.mold.impl.treeview.renderer;

import java.util.function.Consumer;

import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.impl.treeview.TreeViewCollectionBrowserNode;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.impl.treeview.renderer.DefaultTreeViewRenderer;
import com.namics.distrelec.b2b.backoffice.cockpitng.labels.DistLabelService;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class DefaultDistTreeViewRenderer extends DefaultTreeViewRenderer {

    @Override
    protected Div renderEntryLabel(Div labelDiv, TreeViewCollectionBrowserNode entry, Consumer<HtmlBasedComponent> trigger) {
        UITools.modifySClass(labelDiv, "yw-treeview-cell-label", true);
        Label label = new Label(getLabelService().getObjectLabel(entry.getData(), "tree-view-base"));
        trigger.accept(label);
        labelDiv.appendChild(label);
        return labelDiv;
    }

    @Override
    public DistLabelService getLabelService() {
        return (DistLabelService) super.getLabelService();
    }
}
