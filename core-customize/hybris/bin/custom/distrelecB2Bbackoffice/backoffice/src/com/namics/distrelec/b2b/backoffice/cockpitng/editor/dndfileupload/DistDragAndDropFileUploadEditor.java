package com.namics.distrelec.b2b.backoffice.cockpitng.editor.dndfileupload;

import com.hybris.cockpitng.common.configuration.MenupopupPosition;
import com.hybris.cockpitng.editor.defaultfileupload.FileUploadResult;
import com.hybris.cockpitng.editor.dndfileupload.DragAndDropFileUploadEditor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.services.media.MimeTypeChecker;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringEscapeUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;

import java.util.Optional;

public class DistDragAndDropFileUploadEditor extends DragAndDropFileUploadEditor {

    protected final static String MEDIA_IMAGE_UPLOAD_MAXSIZE_ERROR = "media.image.upload.maxsize.error";

    private ConfigurationService configurationService;

    private MimeTypeChecker mimeTypeChecker;

    @Override
    protected void onFileUpload(Component parent, EditorContext<FileUploadResult> context, EditorListener<FileUploadResult> listener, UploadEvent event) {
        if (this.isUploadedMediaAcceptable(event.getMedia(), context)) {
            if (!isImageOversized(event.getMedia())) {
                Optional<FileUploadResult> fileUploadResult = this.toFileUploadResult(event.getMedia());
                if (fileUploadResult.isPresent()) {
                    listener.onValueChanged(fileUploadResult.get());
                    parent.getChildren().clear();
                    this.renderPreview(parent, fileUploadResult.get(), context, listener);
                    return;
                }
            } else {
                showImageMaxSizeMediaMessage(parent, event.getMedia());
                return;
            }
        }

        this.showUnsupportedMediaMessage(parent, context, event.getMedia());
    }

    protected boolean isImageOversized(Media zkMedia) {
        if (getMimeTypeChecker().isMediaAcceptable(zkMedia, "image*")) {
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

    private void showUnsupportedMediaMessage(Component parent, String label) {
        Clients.showNotification(label, "warning", parent, MenupopupPosition.BEFORE_END.getName(), 5000);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public MimeTypeChecker getMimeTypeChecker() {
        return mimeTypeChecker;
    }

    public void setMimeTypeChecker(MimeTypeChecker mimeTypeChecker) {
        this.mimeTypeChecker = mimeTypeChecker;
    }
}
