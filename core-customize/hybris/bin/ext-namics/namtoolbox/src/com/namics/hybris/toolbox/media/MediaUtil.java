package com.namics.hybris.toolbox.media;

import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaContainer;
import de.hybris.platform.jalo.media.MediaContext;
import de.hybris.platform.jalo.media.MediaFolder;
import de.hybris.platform.jalo.media.MediaFormat;
import de.hybris.platform.jalo.media.MediaManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.namics.hybris.toolbox.items.HybrisItemsUtils;

public class MediaUtil {

    private static final Logger log = Logger.getLogger(MediaUtil.class);

    /**
     * Extracts from a media model the file.
     */
    public static File extractMediaFile(final Media media) throws FileNotFoundException {
        File mediaFile;
        try {
            mediaFile = (File) media.getFiles().iterator().next();
        } catch (final Exception e) {
            throw new FileNotFoundException("No file was found for the media " + media);
        }

        return mediaFile;
    }

    /**
     * Search a <code>MediaContext</code> and create one, if it does not exist.
     * 
     * <p>
     * <i>Synchronized method to avoid that two the Object can be created by two parallel tasks in the same time (and therefore twice). The
     * use of this method is less performant.</i>
     * </p>
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaContext</code>.
     * @return The found or new created <code>MediaContext</code>.
     */
    public synchronized static MediaContext createOrGetMediaContextSynchronized(final String qualifier) {
        return createOrGetMediaContext(qualifier);
    }

    /**
     * Search a <code>MediaContext</code> and create one, if it does not exist.
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaContext</code>.
     * @return The found or new created <code>MediaContext</code>.
     */
    @SuppressWarnings("unchecked")
    public static MediaContext createOrGetMediaContext(final String qualifier) {

        final Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("qualifier", qualifier);

        final String query = "SELECT {" + Item.PK + "} FROM {MediaContext} " + "WHERE {" + MediaContext.QUALIFIER + "} = ?qualifier";

        final List<MediaContext> mediaContexts = FlexibleSearch.getInstance().search(query, parameters, MediaContext.class).getResult();

        MediaContext mediaContext;

        if (mediaContexts == null || mediaContexts.isEmpty()) {
            final Map<String, Object> attributes = new HashMap<String, Object>(2);
            attributes.put(MediaContext.QUALIFIER, qualifier);

            mediaContext = MediaManager.getInstance().createMediaContext(attributes);
            mediaContext.setAllName(HybrisItemsUtils.createLanguageMapWithInternationalName(qualifier));
        } else {
            mediaContext = mediaContexts.iterator().next();
        }
        return mediaContext;
    }

    /**
     * Search a <code>MediaContainer</code> and create one, if it does not exist.
     * 
     * <p>
     * <i>Synchronized method to avoid that two the Object can be created by two parallel tasks in the same time (and therefore twice). The
     * use of this method is less performant.</i>
     * </p>
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaContainer</code>.
     * @param catalogVersion
     *            The catalogVersion of the <code>MediaContainer</code>.
     * @return The found or new created <code>MediaContainer</code>.
     */
    public synchronized static MediaContainer createOrGetMediaContainerSynchronized(final String qualifier, final CatalogVersion catalogVersion) {
        return createOrGetMediaContainer(qualifier, catalogVersion);
    }

    /**
     * Search a <code>MediaContainer</code> and create one, if it does not exist.
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaContainer</code>.
     * @param catalogVersion
     *            The catalogVersion of the <code>MediaContainer</code>.
     * @return The found or new created <code>MediaContainer</code>.
     */
    @SuppressWarnings("unchecked")
    public static MediaContainer createOrGetMediaContainer(final String qualifier, final CatalogVersion catalogVersion) {

        final Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("qualifier", qualifier);
        parameters.put("catalogVersion", catalogVersion);

        final String query = "SELECT {" + Item.PK + "} FROM {MediaContainer} " + "WHERE {" + MediaContainer.QUALIFIER + "} = ?qualifier AND " + "{"
                + "catalogVersion" + "} = ?catalogVersion ";

        final List<MediaContainer> mediaContainers = FlexibleSearch.getInstance().search(query, parameters, MediaContainer.class).getResult();

        MediaContainer mediaContainer;

        if (mediaContainers == null || mediaContainers.isEmpty()) {
            final Map<String, Object> attributes = new HashMap<String, Object>(2);
            attributes.put(MediaContainer.QUALIFIER, qualifier);
            attributes.put("catalogVersion", catalogVersion);

            mediaContainer = MediaManager.getInstance().createMediaContainer(attributes);
            mediaContainer.setAllName(HybrisItemsUtils.createLanguageMapWithInternationalName(qualifier));

        } else {
            mediaContainer = mediaContainers.iterator().next();
        }
        return mediaContainer;
    }

    /**
     * Search a <code>MediaFormat</code> and create one, if it does not exist.
     * 
     * <p>
     * <i>Synchronized method to avoid that two the Object can be created by two parallel tasks in the same time (and therefore twice). The
     * use of this method is less performant.</i>
     * </p>
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaFormat</code>.
     * @return The found or new created <code>MediaFormat</code>.
     */
    public synchronized static MediaFormat createOrGetMediaFormatSynchronized(final String qualifier) {
        return createOrGetMediaFormat(qualifier);
    }

    /**
     * Search a <code>MediaFormat</code> and create one, if it does not exist.
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaFormat</code>.
     * @return The found or new created <code>MediaFormat</code>.
     */
    public static MediaFormat createOrGetMediaFormat(final String qualifier) {
        MediaFormat mediaFormat = MediaManager.getInstance().getMediaFormatByQualifier(qualifier);
        if (mediaFormat == null) {
            final Map<String, Object> attributes = new HashMap<String, Object>(2);
            attributes.put(MediaFormat.QUALIFIER, qualifier);
            attributes.put(MediaFormat.EXTERNALID, qualifier);
            mediaFormat = MediaManager.getInstance().createMediaFormat(attributes);
            mediaFormat.setAllName(HybrisItemsUtils.createLanguageMapWithInternationalName(qualifier));

        }
        return mediaFormat;
    }

    /**
     * Search a <code>MediaFolder</code> and create one, if it does not exist.
     * 
     * <p>
     * <i>Synchronized method to avoid that two the Object can be created by two parallel tasks in the same time (and therefore twice). The
     * use of this method is less performant.</i>
     * </p>
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaFolder</code>.
     * @return The found or new created <code>MediaFolder</code>.
     */
    public synchronized static MediaFolder createOrGetMediaFolderSynchronized(final String qualifier, final String path) {
        return createOrGetMediaFolder(qualifier, path);
    }

    /**
     * Search a <code>MediaFolder</code> and create one, if it does not exist.
     * 
     * @param qualifier
     *            The qualifier of the <code>MediaFolder</code>.
     * @return The found or new created <code>MediaFolder</code>.
     */
    public static MediaFolder createOrGetMediaFolder(final String qualifier, final String path) {
        final Collection<MediaFolder> mediaFolderCollection = MediaManager.getInstance().getMediaFolderByQualifier(qualifier);
        MediaFolder mediaFolder = null;
        if (mediaFolderCollection == null || mediaFolderCollection.isEmpty()) {
            mediaFolder = MediaManager.getInstance().createMediaFolder(qualifier, path);
        } else {
            mediaFolder = mediaFolderCollection.iterator().next();
        }
        return mediaFolder;
    }

    /**
     * Returns the content of a media as file.
     * 
     * @param media
     *            The media, from that the content is taken.
     * @return A string representing the content of the media.
     */
    public static String getMediaContent(final Media media) {
        StringBuilder sb;
        BufferedReader r;
        sb = new StringBuilder();
        if (media == null || !media.hasData() && media.getURL() == null) {
            return null;
        }
        r = null;
        StringBuilder stringbuilder;
        try {
            r = new BufferedReader(new InputStreamReader(media.getDataFromStreamSure()));
            boolean first = false;
            for (String s = r.readLine(); s != null; s = r.readLine()) {
                if (!first) {
                    sb.append("\n");
                } else {
                    first = false;
                }
                sb.append(s);
            }

            stringbuilder = sb;
        } catch (final JaloBusinessException e) {
            throw new JaloSystemException(e);
        } catch (final IOException e) {
            throw new JaloSystemException(e);
        }
        try {
            r.close();
        } catch (final IOException e) {
            log.error((new StringBuilder("Error while closing stream: ")).append(e.getMessage()).toString(), e);
        }
        return stringbuilder.toString();
    }

}
