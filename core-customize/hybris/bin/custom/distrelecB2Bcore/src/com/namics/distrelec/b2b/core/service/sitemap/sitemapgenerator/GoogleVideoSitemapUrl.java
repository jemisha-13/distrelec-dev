package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * One configurable Google Video Search URL. To configure, use {@link Options}
 * 
 * @author Dan Fabulich
 * @see Options
 * @see <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=80472">Creating Video Sitemaps</a>
 */
public class GoogleVideoSitemapUrl extends WebSitemapUrl {

    private final URL playerUrl;
    private final URL contentUrl;
    private final URL thumbnailUrl;
    private final String title;
    private final String description;
    private final Double rating;
    private final Integer viewCount;
    private final Date publicationDate;
    private final List<String> tags;
    private final String category;
    // TODO can there be multiple categories?
    // "Usually a video will belong to a single category."
    // http://www.google.com/support/webmasters/bin/answer.py?answer=80472
    private final String familyFriendly;
    private final Integer durationInSeconds;
    private final String allowEmbed;

    /** Options to configure Google Video URLs */
    public static class Options extends AbstractSitemapUrlOptions<GoogleVideoSitemapUrl, Options> {
        private URL playerUrl;
        private URL contentUrl;
        private URL thumbnailUrl;
        private String title;
        private String description;
        private Double rating;
        private Integer viewCount;
        private Date publicationDate;
        private List<String> tags;
        private String category;
        // TODO can there be multiple categories?
        // "Usually a video will belong to a single category."
        // http://www.google.com/support/webmasters/bin/answer.py?answer=80472
        private Boolean familyFriendly;
        private Integer durationInSeconds;
        private Boolean allowEmbed;

        /**
         * Specifies a landing page URL, together with a "player" (e.g. SWF)
         * 
         * @param url
         *            the landing page URL
         * @param playerUrl
         *            the URL of the "player" (e.g. SWF file)
         * @param allowEmbed
         *            when specifying a player, you must specify whether embedding is allowed
         */
        public Options(final URL url, final URL playerUrl, boolean allowEmbed) {
            super(url, GoogleVideoSitemapUrl.class);
            this.playerUrl = playerUrl;
            this.allowEmbed = allowEmbed;
        }

        /**
         * Specifies a landing page URL, together with the URL of the underlying video (e.g. FLV)
         * 
         * @param url
         *            the landing page URL
         * @param contentUrl
         *            the URL of the underlying video (e.g. FLV)
         */
        public Options(final URL url, final URL contentUrl) {
            super(url, GoogleVideoSitemapUrl.class);
            this.contentUrl = contentUrl;
        }

        /**
         * Specifies a player URL (e.g. SWF)
         * 
         * @param playerUrl
         *            the URL of the "player" (e.g. SWF file)
         * @param allowEmbed
         *            when specifying a player, you must specify whether embedding is allowed
         */
        public Options playerUrl(final URL playerUrl, final boolean allowEmbed) {
            this.playerUrl = playerUrl;
            this.allowEmbed = allowEmbed;
            return this;
        }

        /** Specifies the URL of the underlying video (e.g FLV) */
        public Options contentUrl(final URL contentUrl) {
            this.contentUrl = contentUrl;
            return this;
        }

        /**
         * A URL pointing to the URL for the video thumbnail image file. This allows you to suggest the thumbnail you want displayed in
         * search results. If you provide a {@link #contentUrl(URL)}, Google will attempt to generate a set of representative thumbnail
         * images from your actual video content. However, we strongly recommended that you provide a thumbnail URL to increase the
         * likelihood of your video being included in the video index.
         */
        public Options thumbnailUrl(final URL thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        /** The title of the video. Limited to 100 characters. */
        public Options title(final String title) {
            if (title != null && title.length() > 100) {
                throw new RuntimeException("Video title is limited to 100 characters: " + title);
            }
            this.title = title;
            return this;
        }

        /** The description of the video. Descriptions longer than 2048 characters will be truncated. */
        public Options description(final String description) {
            if (description != null && description.length() > 2048) {
                throw new RuntimeException("Truncate video descriptions to 2048 characters: " + description);
            }
            this.description = description;
            return this;
        }

        /** The rating of the video. The value must be number in the range 0.0-5.0. */
        public Options rating(final Double rating) {
            if (rating != null && (rating < 0 || rating > 5.0)) {
                throw new RuntimeException("Rating must be between 0.0 and 5.0:" + rating);
            }
            this.rating = rating;
            return this;
        }

        /** The number of times the video has been viewed */
        public Options viewCount(final int viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        /** The date the video was first published, in {@link W3CDateFormat}. */
        public Options publicationDate(final Date publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        /**
         * Tag associated with the video; tags are generally very short descriptions of key concepts associated with a video or piece of
         * content. A single video could have several tags, although it might belong to only one category. For example, a video about
         * grilling food may belong in the Grilling category, but could be tagged "steak", "meat", "summer", and "outdoor". Create a new
         * <video:tag> element for each tag associated with a video. A maximum of 32 tags is permitted.
         */
        public Options tags(final List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Tag associated with the video; tags are generally very short descriptions of key concepts associated with a video or piece of
         * content. A single video could have several tags, although it might belong to only one category. For example, a video about
         * grilling food may belong in the Grilling category, but could be tagged "steak", "meat", "summer", and "outdoor". Create a new
         * <video:tag> element for each tag associated with a video. A maximum of 32 tags is permitted.
         */
        public Options tags(final Iterable<String> tags) {
            this.tags = new ArrayList<String>();
            for (String tag : tags) {
                this.tags.add(tag);
            }
            return this;
        }

        /**
         * Tag associated with the video; tags are generally very short descriptions of key concepts associated with a video or piece of
         * content. A single video could have several tags, although it might belong to only one category. For example, a video about
         * grilling food may belong in the Grilling category, but could be tagged "steak", "meat", "summer", and "outdoor". Create a new
         * <video:tag> element for each tag associated with a video. A maximum of 32 tags is permitted.
         */
        public Options tags(final String... tags) {
            return tags(Arrays.asList(tags));
        }

        /**
         * The video's category; for example, <code>cooking</code>. The value should be a string no longer than 256 characters. In general,
         * categories are broad groupings of content by subject. Usually a video will belong to a single category. For example, a site about
         * cooking could have categories for Broiling, Baking, and Grilling
         */
        public Options category(final String category) {
            if (category != null && category.length() > 256) {
                throw new RuntimeException("Video category is limited to 256 characters: " + title);
            }
            this.category = category;
            return this;
        }

        /** Whether the video is suitable for viewing by children */
        public Options familyFriendly(final boolean familyFriendly) {
            this.familyFriendly = familyFriendly;
            return this;
        }

        /** The duration of the video in seconds; value must be between 0 and 28800 (8 hours). */
        public Options durationInSeconds(final int durationInSeconds) {
            if (durationInSeconds < 0 || durationInSeconds > 28800) {
                throw new RuntimeException("Duration must be between 0 and 28800 (8 hours):" + durationInSeconds);
            }
            this.durationInSeconds = durationInSeconds;
            return this;
        }

    }

    /**
     * Specifies a landing page URL, together with a "player" (e.g. SWF)
     * 
     * @param url
     *            the landing page URL
     * @param playerUrl
     *            the URL of the "player" (e.g. SWF file)
     * @param allowEmbed
     *            when specifying a player, you must specify whether embedding is allowed
     */
    public GoogleVideoSitemapUrl(final URL url, final URL playerUrl, final boolean allowEmbed) {
        this(new Options(url, playerUrl, allowEmbed));
    }

    /**
     * Specifies a landing page URL, together with the URL of the underlying video (e.g. FLV)
     * 
     * @param url
     *            the landing page URL
     * @param contentUrl
     *            the URL of the underlying video (e.g. FLV)
     */
    public GoogleVideoSitemapUrl(final URL url, final URL contentUrl) {
        this(new Options(url, contentUrl));
    }

    /** Configures the url with options */
    public GoogleVideoSitemapUrl(final Options options) {
        super(options);
        contentUrl = options.contentUrl;
        playerUrl = options.playerUrl;
        if (playerUrl == null && contentUrl == null) {
            throw new RuntimeException("You must specify either contentUrl or playerUrl or both; neither were specified");
        }
        allowEmbed = convertBooleanToYesOrNo(options.allowEmbed);
        if (playerUrl != null && allowEmbed == null) {
            throw new RuntimeException("allowEmbed must be specified if playerUrl is specified");
        }
        category = options.category;

        description = options.description;
        durationInSeconds = options.durationInSeconds;
        familyFriendly = convertBooleanToYesOrNo(options.familyFriendly);

        publicationDate = options.publicationDate;
        rating = options.rating;
        tags = options.tags;
        if (tags != null && tags.size() > 32) {
            throw new RuntimeException("A maximum of 32 tags is permitted");
        }
        thumbnailUrl = options.thumbnailUrl;
        title = options.title;
        viewCount = options.viewCount;
    }

    private static String convertBooleanToYesOrNo(final Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? "Yes" : "No";
    }

    /** Retrieves the {@link Options#playerUrl} */
    public URL getPlayerUrl() {
        return playerUrl;
    }

    /** Retrieves the {@link Options#contentUrl} */
    public URL getContentUrl() {
        return contentUrl;
    }

    /** Retrieves the {@link Options#thumbnailUrl} */
    public URL getThumbnailUrl() {
        return thumbnailUrl;
    }

    /** Retrieves the {@link Options#title} */
    public String getTitle() {
        return title;
    }

    /** Retrieves the {@link Options#description} */
    public String getDescription() {
        return description;
    }

    /** Retrieves the {@link Options#rating} */
    public Double getRating() {
        return rating;
    }

    /** Retrieves the {@link Options#viewCount} */
    public Integer getViewCount() {
        return viewCount;
    }

    /** Retrieves the {@link Options#publicationDate} */
    public Date getPublicationDate() {
        return publicationDate;
    }

    /** Retrieves the {@link Options#tags} */
    public List<String> getTags() {
        return tags;
    }

    /** Retrieves the {@link Options#category} */
    public String getCategory() {
        return category;
    }

    /** Retrieves whether the video is {@link Options#familyFriendly} */
    public String getFamilyFriendly() {
        return familyFriendly;
    }

    /** Retrieves the {@link Options#durationInSeconds} */
    public Integer getDurationInSeconds() {
        return durationInSeconds;
    }

    /** Retrieves whether embedding is allowed */
    public String getAllowEmbed() {
        return allowEmbed;
    }

}
