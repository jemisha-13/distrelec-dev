package com.namics.distrelec.b2b.backoffice.renderers;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.common.configuration.MenupopupPosition;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Parameter;
import com.hybris.cockpitng.core.model.ModelObserver;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.editors.EditorUtils;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.services.media.MimeTypeChecker;
import com.hybris.cockpitng.services.media.ObjectPreview;
import com.hybris.cockpitng.services.media.ObjectPreviewService;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaPanelRenderer;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.platformbackoffice.renderers.util.DateFormatter;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.impl.Utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Everything is the same as in GenericMediaItemUploadPanelRenderer instead of {@link #getMediaURL(MediaModel)}
 * method which is adjusted because of medias served directly from tomcat.
 */
public class DistGenericMediaItemUploadPanelRenderer extends AbstractEditorAreaPanelRenderer<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(DistGenericMediaItemUploadPanelRenderer.class);
    protected final static String MEDIA_IMAGE_UPLOAD_MAXSIZE_ERROR = "media.image.upload.maxsize.error";
    protected String MODEL_MEDIA_SAVED;
    protected String MODEL_MEDIA_CANCELED;
    protected String MODEL_RENDER_PARAMETER_MAP;
    protected String MODEL_ZK_MEDIA_MODEL_KEY;
    protected String MODEL_ZK_MEDIA_CLEARED;
    protected String MODEL_DISPLAY_FROM_MODEL;
    protected String currentlyDisplayedMode;
    protected String currentlyDisplayedMediaInfo;
    protected String initiallyDisplayedMediaModelInfo;
    private ConfigurationService configurationService;
    private ModelService modelService;
    private MediaService mediaService;
    private MimeTypeChecker mimeTypeChecker;
    private MediaStorageConfigService mediaStorageConfigService;
    private CockpitProperties cockpitProperties;
    private PermissionFacadeStrategy permissionFacadeStrategy;
    private WidgetInstanceManager widgetInstanceManager;
    private ObjectPreviewService objectPreviewService;
    private Div tmpImageDiv;
    private Div imgDiv;
    private VersionAwareImage tmpImage;
    private Button downloadButton;
    private Button deleteButton;
    private Fileupload fileupload;
    private Label labelPkValue;
    private Label labelTimeCreatedValue;
    private Label labelTimeModifiedValue;
    private DateFormatter dateFormatter;
    private String format;

    public void render(Component parent, AbstractPanel panel, Object data, DataType type, WidgetInstanceManager widgetInstanceManager) {
        this.widgetInstanceManager = widgetInstanceManager;
        this.initPreRenderModelConstants(this.getEntityId(data, panel));
        this.initializeRenderParametersMap(panel);
        Div rendererPanel = new Div();
        parent.appendChild(rendererPanel);
        String accept = (String)((Map)widgetInstanceManager.getModel().getValue(this.MODEL_RENDER_PARAMETER_MAP, Map.class)).get("accept");
        rendererPanel.setClientAttribute("accept", accept);
        this.adjustView(rendererPanel, type);
        this.addModelObservers(rendererPanel, data, type);
    }

    protected void adjustView(Component rendererPanel, DataType type) {
        if (this.isMediaModelAvailable()) {
            if ("active".equals(this.currentlyDisplayedMode)) {
                this.refreshActiveViewForMediaModelOrZkMedia(type);
            } else {
                this.removeAfterSaveAndCancelListeners();
                Components.removeAllChildren(rendererPanel);
                this.initializeActiveView(rendererPanel, type);
            }
        } else if (!"inactive".equals(this.currentlyDisplayedMode)) {
            this.removeAfterSaveAndCancelListeners();
            Components.removeAllChildren(rendererPanel);
            this.initializeInactiveView(rendererPanel, type);
        }

    }

    private void refreshActiveViewForMediaModelOrZkMedia(DataType type) {
        if (this.isDisplayContentFromMediaModel()) {
            if (ObjectUtils.notEqual(this.currentlyDisplayedMediaInfo, this.getMediaModelInfo(this.getMediaModel()))) {
                this.refreshActiveView(type);
            }
        } else if (ObjectUtils.notEqual(this.currentlyDisplayedMediaInfo, this.getZKMediaInfo(this.getZkMedia()))) {
            this.refreshActiveView(type);
        }

    }

    protected void initializeInactiveView(Component rendererPanel, DataType type) {
        String qualifier = this.getModelAttributeToMedia();
        DataAttribute attribute = qualifier == null ? null : type.getAttribute(qualifier);
        if (attribute != null) {
            Editor editor = new Editor();
            editor.setProperty(this.getPathToMediaObjectInModel());
            editor.setWidgetInstanceManager(this.getWidgetInstanceManager());
            editor.setType(EditorUtils.getEditorType(attribute, true));
            editor.setReadableLocales(this.getPermissionFacade().getAllReadableLocalesForCurrentUser());
            editor.setWritableLocales(this.getPermissionFacade().getAllReadableLocalesForCurrentUser());
            editor.setOptional(!attribute.isMandatory());
            editor.setReadOnly(!attribute.isWritable() || !this.canEdit(type));
            editor.afterCompose();
            rendererPanel.appendChild(this.getAttributeLabel(attribute));
            rendererPanel.appendChild(editor);
            this.currentlyDisplayedMode = "inactive";
        }

        this.refreshDisplayedMediaInfo();
    }

    protected void initializeActiveView(Component rendererPanel, DataType type) {
        if (this.isMediaNestedInRootObject()) {
            String qualifier = this.getModelAttributeToMedia();
            DataAttribute attribute = qualifier == null ? null : type.getAttribute(qualifier);
            if (attribute != null) {
                rendererPanel.appendChild(this.getAttributeLabel(attribute));
            }
        }

        if (!this.canRead(type)) {
            rendererPanel.appendChild(new Label(Labels.getLabel("upload.media.no.access")));
        } else {
            Div mediaPreviewRow = this.initMediaPreviewRow(rendererPanel);
            Div previewPlaceholder = this.initPreviewPlaceholder();
            Vlayout mediaInfoPanel = this.initMediaInfoPanel();
            if (this.areButtonsVisible()) {
                this.initFileUpload(mediaInfoPanel, type);
                this.initDownloadButton(mediaInfoPanel);
                this.initDeleteButton(mediaInfoPanel, type);
            }

            this.initImgDiv(previewPlaceholder);
            this.initTmpImg(previewPlaceholder);
            if (!this.isZKMediaCleared() && this.getZkMedia() == null) {
                if (this.isDisplayable(this.getMediaModel())) {
                    this.createPreviewForDisplayableMedia();
                } else {
                    this.createPreviewForNonDisplayableMedia();
                }
            }

            mediaPreviewRow.appendChild(mediaInfoPanel);
            mediaPreviewRow.appendChild(previewPlaceholder);
            this.setAfterSaveListener(type);
            this.setAfterCancelListener();
            this.updateButtonsState(type);
            this.currentlyDisplayedMode = "active";
            this.refreshDisplayedMediaInfo();
        }
    }

    protected Label getAttributeLabel(DataAttribute attribute) {
        Label label = new Label(attribute.getLabel(this.getCockpitLocaleService().getCurrentLocale()));
        label.setTooltiptext(this.getModelAttributeToMedia());
        return label;
    }

    protected void refreshActiveView(DataType type) {
        if ("active".equals(this.currentlyDisplayedMode)) {
            this.initMediaInfoLabels();
            this.clearPreviewImage();
            if (this.isMediaModelAvailable()) {
                if (this.isDisplayContentFromMediaModel()) {
                    if (this.isDisplayable(this.getMediaModel())) {
                        this.createPreviewForDisplayableMedia();
                    } else {
                        this.createPreviewForNonDisplayableMedia();
                    }
                } else if (this.getZkMedia() != null && !this.isZKMediaCleared()) {
                    this.renderPreviewWithZKMedia(this.getZkMedia());
                }

                this.updateButtonsState(type);
            }

            this.refreshDisplayedMediaInfo();
        }
    }

    protected void cleanupModel() {
        WidgetModel widgetModel = this.getWidgetInstanceManager().getModel();
        widgetModel.remove(this.MODEL_MEDIA_SAVED);
        widgetModel.remove(this.MODEL_MEDIA_CANCELED);
        widgetModel.remove(this.MODEL_RENDER_PARAMETER_MAP);
        widgetModel.remove(this.MODEL_ZK_MEDIA_MODEL_KEY);
        widgetModel.remove(this.MODEL_ZK_MEDIA_CLEARED);
        widgetModel.remove(this.MODEL_DISPLAY_FROM_MODEL);
    }

    protected void initPreRenderModelConstants(String suffix) {
        this.MODEL_MEDIA_SAVED = "media_saved_" + suffix;
        this.MODEL_MEDIA_CANCELED = "media_canceled_" + suffix;
        this.MODEL_RENDER_PARAMETER_MAP = "render_parameter_map_" + suffix;
        this.MODEL_ZK_MEDIA_MODEL_KEY = "zkMedia_tmp_" + suffix;
        this.MODEL_ZK_MEDIA_CLEARED = "zkMediaCleared_" + suffix;
        this.MODEL_DISPLAY_FROM_MODEL = "displayFromModel" + suffix;
    }

    protected void removeAfterSaveAndCancelListeners() {
        EditorAreaRendererUtils.removeAfterSaveListener(this.getWidgetInstanceManager().getModel(), this.MODEL_MEDIA_SAVED);
        EditorAreaRendererUtils.removeAfterCancelListener(this.getWidgetInstanceManager().getModel(), this.MODEL_MEDIA_CANCELED);
    }

    protected String getEntityId(Object data, AbstractPanel panel) {
        StringBuilder entityId = new StringBuilder();
        if (data instanceof ItemModel && !this.getObjectFacade().isNew(data)) {
            entityId.append(((ItemModel)data).getPk().getHex());
        }

        if (panel instanceof CustomPanel) {
            Map<String, String> renderParameters = (Map)((CustomPanel)panel).getRenderParameter().stream().collect(Collectors.toMap(Parameter::getName, Parameter::getValue));
            String modelProperty = (String)renderParameters.get("dataModelProperty");
            String modelAttribute = (String)renderParameters.get("dataModelAttribute");
            entityId.append("_");
            entityId.append(StringUtils.isNotBlank(modelProperty) ? modelProperty.trim() : "currentObject");
            if (StringUtils.isNotBlank(modelAttribute)) {
                entityId.append(".");
                entityId.append(modelAttribute);
            }
        } else {
            entityId.append("currentObject");
        }

        return entityId.toString();
    }

    protected boolean areButtonsVisible() {
        return BooleanUtils.isNotFalse(BooleanUtils.toBooleanObject(this.getRendererParameterValue("showContentButtons")));
    }

    private String getRendererParameterValue(String parameterName) {
        Map<String, String> params = (Map)this.getWidgetInstanceManager().getModel().getValue(this.MODEL_RENDER_PARAMETER_MAP, Map.class);
        if (params != null && params.containsKey(parameterName)) {
            String paramValue = (String)params.get(parameterName);
            return StringUtils.isNotBlank(paramValue) ? paramValue.trim() : null;
        } else {
            return null;
        }
    }

    private void initializeRenderParametersMap(AbstractPanel panel) {
        Object renderParameters;
        if (panel instanceof CustomPanel) {
            renderParameters = (Map)((CustomPanel)panel).getRenderParameter().stream().collect(Collectors.toMap(Parameter::getName, Parameter::getValue));
        } else {
            renderParameters = Maps.newHashMap();
        }

        this.getWidgetInstanceManager().getModel().put(this.MODEL_RENDER_PARAMETER_MAP, renderParameters);
    }

    private boolean isMediaModelAvailable() {
        return this.getMediaModel() != null;
    }

    protected MediaModel getMediaModel() {
        Object value = this.getWidgetInstanceManager().getModel().getValue(this.getPathToRootObjectInModel(), Object.class);
        String qualifier = this.getModelAttributeToMedia();
        if (qualifier != null) {
            Object result = this.getPropertyValueService().readValue(value, qualifier);

            try {
                return (MediaModel)result;
            } catch (ClassCastException var5) {
                LOG.error(String.format("Wrong configuration; attribute %s is not of Media type", qualifier), var5);
                return null;
            }
        } else {
            return (MediaModel)value;
        }
    }

    private boolean isMediaNestedInRootObject() {
        return this.getModelAttributeToMedia() != null;
    }

    private String getModelAttributeToMedia() {
        return this.getRendererParameterValue("dataModelAttribute");
    }

    private String getPathToRootObjectInModel() {
        String modelProperty = this.getRendererParameterValue("dataModelProperty");
        return modelProperty != null ? modelProperty : "currentObject";
    }

    private String getPathToMediaObjectInModel() {
        String rootObject = this.getPathToRootObjectInModel();
        String attribute = this.getModelAttributeToMedia();
        StringBuilder path = new StringBuilder();
        path.append(rootObject);
        if (attribute != null) {
            path.append(".");
            path.append(attribute);
        }

        return path.toString();
    }

    protected void refreshModelAndViewAfterSaveListeners(DataType type) {
        this.refreshWidgetModelAfterSaveListeners();
        this.refreshActiveView(type);
    }

    protected void refreshWidgetModelAfterSaveListeners() {
        try {
            this.setDisplayContentFromMediaModel(true);
            this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_MODEL_KEY, (Object)null);
            this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_CLEARED, Boolean.FALSE);
            MediaModel freshMediaModel = (MediaModel)this.getObjectFacade().reload(this.getMediaModel());
            this.getWidgetInstanceManager().getModel().setValue(this.getPathToMediaObjectInModel(), freshMediaModel);
        } catch (ObjectNotFoundException var2) {
            LOG.error("Media not found on reload", var2);
        }

    }

    private void createPreviewForNonDisplayableMedia() {
        Image img = new Image();
        MediaModel mediaModel = this.getMediaModel();
        if (mediaModel != null) {
            ObjectPreview preview = this.objectPreviewService.getPreview(this.mediaService.hasData(mediaModel) && StringUtils.isNotBlank(mediaModel.getMime()) ? mediaModel.getMime() : "");
            if (!preview.isFallback()) {
                img.setSrc(preview.getUrl());
                this.imgDiv.appendChild(img);
            }
        }

    }

    private void initImgDiv(Component parent) {
        this.imgDiv = new Div();
        this.imgDiv.setSclass("previewPlaceholder");
        this.imgDiv.setVisible(true);
        parent.appendChild(this.imgDiv);
    }

    private Div initPreviewPlaceholder() {
        Div previewPlaceholder = new Div();
        previewPlaceholder.setSclass("mediaPreviewCnt");
        return previewPlaceholder;
    }

    private Div initMediaPreviewRow(Component parent) {
        Div mediaPreviewRow = new Div();
        mediaPreviewRow.setSclass("mediaPreview");
        parent.appendChild(mediaPreviewRow);
        return mediaPreviewRow;
    }

    private void initTmpImg(Component parent) {
        this.tmpImage = new VersionAwareImage();
        this.tmpImageDiv = new Div();
        this.tmpImageDiv.appendChild(this.tmpImage);
        this.tmpImageDiv.setSclass("previewPlaceholder");
        this.tmpImageDiv.setVisible(false);
        parent.appendChild(this.tmpImageDiv);
        if (this.getZkMedia() != null) {
            this.renderPreviewWithZKMedia(this.getZkMedia());
        }

    }

    private boolean canRead(DataType type) {
        boolean canReadProperty = true;
        if (this.isMediaNestedInRootObject()) {
            canReadProperty = this.permissionFacadeStrategy.canReadProperty(type.getCode(), this.getModelAttributeToMedia());
        }

        MediaModel mediaModel = this.getMediaModel();
        if (canReadProperty && mediaModel != null) {
            return this.permissionFacadeStrategy.canReadType(type.getCode()) && this.permissionFacadeStrategy.canReadInstance(this.getMediaModel());
        } else {
            return canReadProperty;
        }
    }

    private boolean canEdit(DataType type) {
        boolean canChangeProperty = true;
        if (this.isMediaNestedInRootObject()) {
            canChangeProperty = this.permissionFacadeStrategy.canChangeProperty(type.getCode(), this.getModelAttributeToMedia());
        }

        MediaModel mediaModel = this.getMediaModel();
        if (canChangeProperty && mediaModel != null) {
            boolean canChangeMimeProperty = this.permissionFacadeStrategy.canChangeProperty("Media", "mime") && this.permissionFacadeStrategy.canChangeInstanceProperty(mediaModel, "mime");
            boolean canChangeRealFileNameProperty = this.permissionFacadeStrategy.canChangeProperty("Media", "realFileName") && this.permissionFacadeStrategy.canChangeInstanceProperty(mediaModel, "realFileName");
            return canChangeMimeProperty && canChangeRealFileNameProperty && this.canRead(type) && this.canChangeMedia(mediaModel);
        } else {
            return canChangeProperty;
        }
    }

    private boolean canChangeMedia(MediaModel mediaModel) {
        return this.permissionFacadeStrategy.canChangeType("Media") && this.permissionFacadeStrategy.canChangeInstance(mediaModel);
    }

    void initFileUpload(Component parent, DataType type) {
        boolean canChangeInstance = this.getPermissionFacade().canChangeInstance(this.getMediaModel());
        this.fileupload = new Fileupload(Labels.getLabel("upload.media.upload"));
        this.setLimitOnMaxFileSize(this.fileupload);
        parent.appendChild(this.fileupload);
        boolean disabled = !canChangeInstance || !this.canEdit(type);
        this.fileupload.setDisabled(disabled);
        this.fileupload.setSclass("media-fileupload");
        if (canChangeInstance) {
            this.fileupload.addEventListener("onUploadLater", (event) -> {
                String url = this.getDynamicUrlForMedia(this.tmpImage);
                this.imgDiv.setVisible(false);
                this.tmpImageDiv.setVisible(true);
                this.tmpImage.setVisible(false);
                this.clearTmpImageDivButTmpImage();
                this.tmpImageDiv.appendChild(this.createFakeImageZoomCnt(UITools.adjustURL(url)));
            });
            this.fileupload.addEventListener("onUpload", (event) -> {
                UploadEvent uploadEvent = (UploadEvent) event;
                Media zkMedia = uploadEvent.getMedia();
                Component panel = parent.getParent().getParent();
                if (this.mimeTypeChecker.isMediaAcceptable(zkMedia, panel.getClientAttribute("accept"))) {
                    if (!isImageOversized(zkMedia)) {
                        this.setDisplayContentFromMediaModel(false);
                        this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_CLEARED, Boolean.FALSE);
                        this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_MODEL_KEY, zkMedia);
                        this.getWidgetInstanceManager().getModel().changed();
                    } else {
                        showImageMaxSizeMediaMessage(fileupload, zkMedia);
                    }
                } else {
                    this.showUnsupportedMediaMessage(this.fileupload, (Media)zkMedia);
                }

            });
        }

    }

    protected boolean isImageOversized(Media zkMedia) {
        if (mimeTypeChecker.isMediaAcceptable(zkMedia, "image*")) {
            int length = zkMedia.getByteData().length;

            Long maxSize = getImageUploadMaxSize();
            return maxSize > 0L && maxSize < length;
        }
        return false;
    }

    protected Long getImageUploadMaxSize() {
        return getConfigurationService().getConfiguration().getLong(DistConfigConstants.Media.MEDIA_IMAGE_UPLOAD_MAXSIZE, 0L);
    }

    protected void showImageMaxSizeMediaMessage(Component parent, Media media) {
        String fileName = media != null ? StringEscapeUtils.escapeHtml(media.getName()) : "";
        String label = Labels.getLabel(MEDIA_IMAGE_UPLOAD_MAXSIZE_ERROR, new String[]{fileName});
        this.showUnsupportedMediaMessage(parent, label);
    }

    protected void showUnsupportedMediaMessage(Component parent, Media media) {
        String fileName = media != null ? StringEscapeUtils.escapeHtml(media.getName()) : "";
        String label = Labels.getLabel("editor.unsupported.filetype", new String[]{fileName});
        this.showUnsupportedMediaMessage(parent, label);
    }

    private void showUnsupportedMediaMessage(Component parent, String label) {
        Clients.showNotification(label, "warning", parent, MenupopupPosition.BEFORE_END.getName(), 5000);
    }

    private void renderPreviewWithZKMedia(Media zkMedia) {
        if (zkMedia instanceof org.zkoss.image.Image) {
            this.tmpImage.setContent((org.zkoss.image.Image)zkMedia);
            Events.echoEvent("onUploadLater", this.fileupload, (String)null);
        } else {
            ObjectPreview imgv = this.objectPreviewService.getPreview(zkMedia.getContentType());
            this.tmpImage.setSrc(imgv.getUrl());
            this.tmpImage.setVisible(true);
            this.clearTmpImageDivButTmpImage();
        }

        if (this.areButtonsVisible()) {
            this.downloadButton.setDisabled(this.isMediaEmpty(this.getMediaModel(), zkMedia));
            this.deleteButton.setDisabled(false);
        }

        this.tmpImageDiv.setVisible(true);
        this.imgDiv.setVisible(false);
        this.refreshDisplayedMediaInfo();
    }

    private void setAfterCancelListener() {
        EditorAreaRendererUtils.setAfterCancelListener(this.getWidgetInstanceManager().getModel(), this.MODEL_MEDIA_CANCELED, (event) -> {
            this.setDisplayContentFromMediaModel(true);
            this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_CLEARED, Boolean.FALSE);
            this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_MODEL_KEY, (Object)null);
        }, false);
    }

    private void setAfterSaveListener(DataType type) {
        EditorAreaRendererUtils.setAfterSaveListener(this.getWidgetInstanceManager().getModel(), this.MODEL_MEDIA_SAVED, (event) -> {
            this.handleModelMediaSaved(type);
        }, false);
    }

    private void handleModelMediaSaved(DataType type) {
        Media zkMedia = this.getZkMedia();
        if (zkMedia != null && !this.getModelService().isNew(this.getMediaModel())) {
            this.updateMediaMetadata(zkMedia);
            if (zkMedia.isBinary()) {
                this.getMediaService().setStreamForMedia(this.getMediaModel(), zkMedia.getStreamData());
            } else {
                this.getMediaService().setDataForMedia(this.getMediaModel(), zkMedia.getStringData() != null ? zkMedia.getStringData().getBytes() : null);
            }

            this.refreshModelAndViewAfterSaveListeners(type);
        } else if (zkMedia == null && this.isZKMediaCleared()) {
            this.mediaService.removeDataFromMedia(this.getMediaModel());
            this.refreshModelAndViewAfterSaveListeners(type);
        }

    }

    private void updateMediaMetadata(Media zkMedia) {
        MediaModel mediaModel = this.getMediaModel();
        if (mediaModel != null) {
            mediaModel.setMime(zkMedia.getContentType());
            mediaModel.setRealFileName(zkMedia.getName());
        }

    }

    private boolean isZKMediaCleared() {
        Boolean zkMediaCleared = (Boolean)this.getWidgetInstanceManager().getModel().getValue(this.MODEL_ZK_MEDIA_CLEARED, Boolean.class);
        return zkMediaCleared != null && zkMediaCleared;
    }

    private void addModelObservers(Component rendererPanel, final Object initialObject, DataType type) {
        final WidgetModel model = this.getWidgetInstanceManager().getModel();
        final String pathToRootObjectInModel = this.getPathToRootObjectInModel();
        final ModelObserver zkContentObserver = () -> {
            Object currentRoot = model.getValue(pathToRootObjectInModel, Object.class);
            if (Objects.equals(currentRoot, initialObject)) {
                this.adjustView(rendererPanel, type);
            }

        };
        this.initiallyDisplayedMediaModelInfo = this.getMediaModelInfo(this.getMediaModel());
        final ModelObserver mediaObjectChangeObserver = () -> {
            Object currentRoot = model.getValue(pathToRootObjectInModel, Object.class);
            if (Objects.equals(currentRoot, initialObject)) {
                MediaModel currentMedia = this.getMediaModel();
                String currentMediaInfo = this.getMediaModelInfo(currentMedia);
                if (!StringUtils.equals(this.initiallyDisplayedMediaModelInfo, currentMediaInfo)) {
                    this.setDisplayContentFromMediaModel(true);
                    this.initiallyDisplayedMediaModelInfo = currentMediaInfo;
                }

                this.adjustView(rendererPanel, type);
            }

        };
        ModelObserver rootObjectChangeObserver = new ModelObserver() {
            public void modelChanged() {
                Object currentRoot = model.getValue(pathToRootObjectInModel, Object.class);
                if (ObjectUtils.notEqual(currentRoot, initialObject)) {
                    model.removeObserver(zkContentObserver);
                    model.removeObserver(mediaObjectChangeObserver);
                    model.removeObserver(this);
                    cleanupModel();
                }

            }
        };
        model.addObserver(this.MODEL_ZK_MEDIA_MODEL_KEY, zkContentObserver);
        model.addObserver(pathToRootObjectInModel, rootObjectChangeObserver);
        model.addObserver(this.getPathToMediaObjectInModel(), mediaObjectChangeObserver);
    }

    private void refreshDisplayedMediaInfo() {
        if (this.getZkMedia() != null && !this.isZKMediaCleared()) {
            this.currentlyDisplayedMediaInfo = this.getZKMediaInfo(this.getZkMedia());
        } else {
            this.currentlyDisplayedMediaInfo = this.getMediaModelInfo(this.getMediaModel());
        }

    }

    private String getMediaModelInfo(MediaModel mediaModel) {
        return mediaModel != null ? String.format("%s_created:%s_modified:%s_url:%s", String.valueOf(mediaModel.getPk()), String.valueOf(mediaModel.getModifiedtime()), String.valueOf(mediaModel.getCreationtime()), this.getMediaURL(mediaModel)) : "media.is.null";
    }

    private String getMediaURL(MediaModel mediaModel) {
        return StringUtils.isBlank(mediaModel.getUrl()) ? StringUtils.EMPTY : UITools.adjustURL(mediaModel.getURL());
    }

    protected boolean isInSecureFolder(MediaModel mediaModel) {
        boolean isSecure = false;
        if (mediaModel.getFolder() != null) {
            MediaStorageConfigService.MediaFolderConfig cfg = this.mediaStorageConfigService.getConfigForFolder(mediaModel.getFolder().getQualifier());
            if (cfg != null) {
                isSecure = cfg.isSecured();
            }
        }

        return isSecure;
    }

    private String getZKMediaInfo(Media zkMedia) {
        return zkMedia != null ? String.format("zkMedia name:%s_contentType:%s_format:%s_objIdentity:%s", zkMedia.getName(), zkMedia.getContentType(), zkMedia.getFormat(), String.valueOf(zkMedia.hashCode())) : "zk.is.null";
    }

    private boolean isDisplayContentFromMediaModel() {
        return (Boolean)ObjectUtils.defaultIfNull((Boolean)this.getWidgetInstanceManager().getModel().getValue(this.MODEL_DISPLAY_FROM_MODEL, Boolean.class), Boolean.TRUE);
    }

    private void setDisplayContentFromMediaModel(boolean displayContentFromMediaModel) {
        this.getWidgetInstanceManager().getModel().setValue(this.MODEL_DISPLAY_FROM_MODEL, displayContentFromMediaModel);
    }

    private void createPreviewForDisplayableMedia() {
        MediaModel mediaModel = this.getMediaModel();
        if (mediaModel != null) {
            String mediaURL = this.getMediaURL(mediaModel);
            Div fakeImageZoomCnt = this.createFakeImageZoomCnt(mediaURL);
            this.imgDiv.appendChild(fakeImageZoomCnt);
        }

    }

    private void updateButtonsState(DataType type) {
        if (this.areButtonsVisible()) {
            boolean hasData = this.isMediaEmpty(this.getMediaModel(), this.getZkMedia());
            this.deleteButton.setDisabled(hasData || !this.canEdit(type));
            this.downloadButton.setDisabled(hasData);
        }

    }

    private void clearTmpImageDivButTmpImage() {
        Iterator iterator = this.tmpImageDiv.getChildren().iterator();

        while(iterator.hasNext()) {
            Component cmp = (Component)iterator.next();
            if (cmp != this.tmpImage) {
                iterator.remove();
            }
        }

    }

    private Div createFakeImageZoomCnt(String url) {
        final Div imageZoomContainer = new Div();
        final Image fakeSmallImage = new Image();
        fakeSmallImage.setSrc(url);
        Div smallImageCnt = new Div();
        smallImageCnt.setSclass("small small-image-preview-cnt");
        smallImageCnt.appendChild(fakeSmallImage);
        smallImageCnt.setParent(imageZoomContainer);
        imageZoomContainer.addEventListener("onMouseOver", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                Clients.evalJavaScript("$(\"#" + imageZoomContainer.getUuid() + "\").anythingZoomer({clone:true});");
                imageZoomContainer.invalidate();
                fakeSmallImage.invalidate();
                imageZoomContainer.removeEventListener("onMouseOver", this);
            }
        });
        return imageZoomContainer;
    }

    protected Vlayout initMediaInfoPanel() {
        Vlayout mediaInfoPanel = new Vlayout();
        mediaInfoPanel.setSclass("mediaInfoCnt");
        this.initMediaInfoLabels();
        if (this.labelPkValue != null) {
            mediaInfoPanel.appendChild(this.createMediaInfoHeaderLabel(Labels.getLabel("media.info.pk")));
            mediaInfoPanel.appendChild(this.labelPkValue);
        }

        if (this.labelTimeCreatedValue != null) {
            mediaInfoPanel.appendChild(this.createMediaInfoHeaderLabel(Labels.getLabel("media.info.timecreated")));
            mediaInfoPanel.appendChild(this.labelTimeCreatedValue);
        }

        if (this.labelTimeModifiedValue != null) {
            mediaInfoPanel.appendChild(this.createMediaInfoHeaderLabel(Labels.getLabel("media.info.timemodified")));
            mediaInfoPanel.appendChild(this.labelTimeModifiedValue);
        }

        return mediaInfoPanel;
    }

    protected void initMediaInfoLabels() {
        MediaModel mediaModel = this.getMediaModel();
        if (mediaModel != null) {
            if (this.getObjectFacade().isNew(mediaModel)) {
                this.labelPkValue = this.createMediaInfoValueLabel("media.info.pk", Labels.getLabel("media.info.not.available"));
                this.labelTimeCreatedValue = this.createMediaInfoValueLabel("media.info.timecreated", Labels.getLabel("media.info.not.available"));
                this.labelTimeModifiedValue = this.createMediaInfoValueLabel("media.info.timemodified", Labels.getLabel("media.info.not.available"));
            } else {
                this.labelPkValue = this.createMediaInfoValueLabel("media.info.pk", mediaModel.getPk().toString());
                Locale currentLocale = this.getCockpitLocaleService().getCurrentLocale();
                this.labelTimeCreatedValue = this.createMediaInfoValueLabel("media.info.timecreated", this.getDateFormatter().format(mediaModel.getCreationtime(), this.format, currentLocale));
                this.labelTimeModifiedValue = this.createMediaInfoValueLabel("media.info.timemodified", this.getDateFormatter().format(mediaModel.getModifiedtime(), this.format, currentLocale));
            }
        }

    }

    protected Label createMediaInfoHeaderLabel(String initialValue) {
        Label label = new Label(initialValue);
        label.setClass("mediaInfoHeader");
        return label;
    }

    protected Label createMediaInfoValueLabel(String infoLabel, String initialValue) {
        Label label = new Label(initialValue);
        label.setClass("mediaInfoValue");
        return label;
    }

    private Button initDownloadButton(Component parent) {
        this.downloadButton = new Button(Labels.getLabel("download.media.download"));
        this.downloadButton.setSclass("media-download-btn");
        this.downloadButton.setDisabled(this.isMediaEmpty(this.getMediaModel(), this.getZkMedia()));
        parent.appendChild(this.downloadButton);
        this.downloadButton.addEventListener("onClick", (event) -> {
            Media zkMedia = this.getZkMedia();
            InputStream streamFromMedia;
            if (zkMedia != null) {
                if (zkMedia.isBinary()) {
                    streamFromMedia = zkMedia.getStreamData();
                    Filedownload.save(streamFromMedia, zkMedia.getContentType(), zkMedia.getName());
                } else {
                    byte[] bytes = zkMedia.getStringData() != null ? zkMedia.getStringData().getBytes() : null;
                    if (bytes != null) {
                        Filedownload.save(bytes, zkMedia.getContentType(), zkMedia.getName());
                    }
                }
            } else {
                streamFromMedia = this.getMediaService().getStreamFromMedia(this.getMediaModel());
                Filedownload.save(streamFromMedia, this.getMediaModel().getMime(), this.getMediaModel().getRealFileName());
            }

        });
        return this.downloadButton;
    }

    private void initDeleteButton(Component parent, DataType type) {
        this.deleteButton = new Button(Labels.getLabel("media.clear.content.command"));
        this.deleteButton.setDisabled(!this.canEdit(type));
        UITools.modifySClass(this.deleteButton, "media-clear-content-btn", true);
        UITools.modifySClass(this.deleteButton, "y-btn-warning", true);
        this.deleteButton.addEventListener("onClick", (event) -> {
            this.clearPreviewImage();
            this.downloadButton.setDisabled(true);
            this.deleteButton.setDisabled(true);
            MediaModel mediaModel = this.getMediaModel();
            mediaModel.setURL("");
            mediaModel.setRealFileName("");
            mediaModel.setMime("");
            this.setDisplayContentFromMediaModel(true);
            this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_CLEARED, Boolean.TRUE);
            this.getWidgetInstanceManager().getModel().put(this.MODEL_ZK_MEDIA_MODEL_KEY, (Object)null);
            this.getWidgetInstanceManager().getModel().setValue(this.getPathToMediaObjectInModel(), mediaModel);
        });
        parent.appendChild(this.deleteButton);
    }

    private void clearPreviewImage() {
        this.tmpImageDiv.getChildren().clear();
        this.tmpImage.setVisible(false);
        this.tmpImageDiv.setVisible(false);
        this.tmpImageDiv.appendChild(this.tmpImage);
        this.imgDiv.getChildren().clear();
        this.imgDiv.setVisible(true);
    }

    private boolean isMediaEmpty(MediaModel mediaModel, Media zkMedia) {
        return StringUtils.isEmpty(this.getMediaURL(mediaModel)) && zkMedia == null;
    }

    protected void setLimitOnMaxFileSize(Fileupload fileupload) {
        String propertyMaxFileSize = this.getCockpitProperties().getProperty("fileUpload.maxSize");
        long sizeInKB = 10000L;
        if (StringUtils.isNumeric(propertyMaxFileSize)) {
            sizeInKB = NumberUtils.toLong(propertyMaxFileSize);
        }

        fileupload.setUpload("maxsize=" + sizeInKB);
    }

    protected boolean isDisplayable(MediaModel media) {
        return media.getMime() != null && media.getMime().contains("image");
    }

    private String getDynamicUrlForMedia(VersionAwareImage media) {
        Media zkMedia = this.getZkMedia();
        return Utils.getDynamicMediaURI(media, media.getVersion(), "c/" + zkMedia.getName(), zkMedia.getFormat());
    }

    protected Media getZkMedia() {
        return (Media)this.getWidgetInstanceManager().getModel().getValue(this.MODEL_ZK_MEDIA_MODEL_KEY, Media.class);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected ModelService getModelService() {
        return this.modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    protected MediaService getMediaService() {
        return this.mediaService;
    }

    @Required
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public MimeTypeChecker getMimeTypeChecker() {
        return this.mimeTypeChecker;
    }

    @Required
    public void setMimeTypeChecker(MimeTypeChecker mimeTypeChecker) {
        this.mimeTypeChecker = mimeTypeChecker;
    }

    protected MediaStorageConfigService getMediaStorageConfigService() {
        return this.mediaStorageConfigService;
    }

    @Required
    public void setMediaStorageConfigService(MediaStorageConfigService mediaStorageConfigService) {
        this.mediaStorageConfigService = mediaStorageConfigService;
    }

    public CockpitProperties getCockpitProperties() {
        return this.cockpitProperties;
    }

    @Required
    public void setCockpitProperties(CockpitProperties cockpitProperties) {
        this.cockpitProperties = cockpitProperties;
    }

    protected PermissionFacadeStrategy getPermissionFacadeStrategy() {
        return this.permissionFacadeStrategy;
    }

    @Required
    public void setPermissionFacadeStrategy(PermissionFacadeStrategy permissionFacadeStrategy) {
        this.permissionFacadeStrategy = permissionFacadeStrategy;
    }

    protected ObjectPreviewService getObjectPreviewService() {
        return this.objectPreviewService;
    }

    @Required
    public void setObjectPreviewService(ObjectPreviewService objectPreviewService) {
        this.objectPreviewService = objectPreviewService;
    }

    protected WidgetInstanceManager getWidgetInstanceManager() {
        return this.widgetInstanceManager;
    }

    protected Div getTmpImageDiv() {
        return this.tmpImageDiv;
    }

    protected Div getImgDiv() {
        return this.imgDiv;
    }

    protected VersionAwareImage getTmpImage() {
        return this.tmpImage;
    }

    protected Button getDownloadButton() {
        return this.downloadButton;
    }

    protected Button getDeleteButton() {
        return this.deleteButton;
    }

    protected Fileupload getFileupload() {
        return this.fileupload;
    }

    protected Label getLabelPkValue() {
        return this.labelPkValue;
    }

    protected Label getLabelTimeCreatedValue() {
        return this.labelTimeCreatedValue;
    }

    protected Label getLabelTimeModifiedValue() {
        return this.labelTimeModifiedValue;
    }

    public DateFormatter getDateFormatter() {
        return this.dateFormatter;
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    private static class VersionAwareImage extends Image {
        private byte version;

        private VersionAwareImage() {
        }

        public void setContent(org.zkoss.image.Image image) {
            super.setContent(image);
            if (image != null) {
                ++this.version;
            }

        }

        public byte getVersion() {
            return this.version;
        }
    }
}
