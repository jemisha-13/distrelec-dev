package com.namics.distrelec.b2b.core.constants;

import com.namics.distrelec.b2b.core.enums.SearchExperience;

public class DistConfigConstants {

    public static final String FEATURE_DATALAYER = "feature.datalayer.enable";

    public static final String SAP_CATALOG_ARTICLES = "sap.catalog.order.articles";

    public static final String COMMA_COUNTRIES = "distrelec.decimal.comma.countries";

    public static final String ENVIRONMENT_ISPROD = "environment.isprod";

    public static class SearchExperienceConfig {

        /**
         * Config for defining a default search experience on accelerator storefronts.
         */
        public static final String DEFAULT_ACC_SEARCH_EXPERIENCE_CONFIG = "distrelec.acc.search.experience";

        public static final SearchExperience DEFAULT_ACC_SEARCH_EXPERIENCE = SearchExperience.FACTFINDER;

        public static final SearchExperience DEFAULT_HEADLESS_SEARCH_EXPERIENCE = SearchExperience.FACTFINDER;
    }

    public static class ErpSalesStatus {

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC = "distrelec.noproduct.forsale.salestatus";

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_NOT_BUYABLE = "distrelec.noproduct.forsale.salestatus.notbuyable";

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_BACKORDER = "distrelec.noproduct.forsale.salesstatus.backorder";

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_IMPORTTOOL = "distrelec.noproduct.forsale.salesstatus.importtool";

        public static final String ATTRIBUTE_IS_AVAILABLE_AFTER_STOCK_DEPLETION_IMPORTTOOL = "distrelec.noproduct.available.after.depletion";

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK = "distrelec.noproduct.forsale.salesstatus.outofstock";

        public static final String ATTRIBUTE_SITEMAP_END_OF_LIFE = "distrelec.sitemap.product.eol";

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_MPA = "distrelec.noproduct.forsale.salestatus.mpa";

        public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_BLOCKED = "distrelec.noproduct.forsale.salestatus.blocked";
    }

    public static class Quote {
        public static final String ATTRIBUTE_MAXIMUM_ALLOWED_QUOTES = "maximum.quotes.allowed";

        public static final String ATTRIBUTE_EXPIRY_DAYS = "quotes.valid.duration.days";

        public static final String ATTRIBUTE_QUOTATION_CSV_HEADER = "distrelec.default.quotation.csv.header";

    }

    public static class PromoLabel {
        public static final String ATTRIBUTE_SINGLE_LABEL_TO_HIDE = "distrelec.promolabel.tohide";

        public static final String ATTRIBUTE_SINGLE_LABEL_TO_REPLACE_THE_HIDDEN_LABEL = "distrelec.promolabel.toreplace.thehiddenlabel";
    }

    public static class InvoiceDocumentArchive {
        public static final String INVOICE_DOCUMENT_ARCHIVE_URL = "invoice.document.archive.url";
    }

    public static class CronJob {
        public static class DistUpdateTimeStamp {
            public static final String ATTRIBUTE_UPDATE_FIRST_APPEARANCE_DATE_PARTITION_SIZE = "distrelec.update.product.firstappearance.partition";

            public static final String BOM_UNUSED_FILE_102_WEEKS_TIMESTAMP = "bom.unused.file.102.weeks.timestamp";

            public static final String BOM_UNUSED_FILE_104_WEEKS_TIMESTAMP = "bom.unused.file.104.weeks.timestamp";
        }

        public static class DistOrderExport {
            public static final String ATTRIBUTE_FTP_DIRECTORY = "distrelec.order.export.ftp.directory";

            public static final String ATTRIBUTE_FTP_HOST = "distrelec.order.export.ftp.host";

            public static final String ATTRIBUTE_FTP_USER = "distrelec.order.export.ftp.user";

            public static final String ATTRIBUTE_FTP_PORT = "distrelec.order.export.ftp.port";

            public static final String ATTRIBUTE_FTP_PASSWORD = "distrelec.order.export.ftp.password";

        }
    }

    public static class Media {
        public static final String MEDIA_IMAGE_UPLOAD_MAXSIZE = "media.image.upload.maxsize";
    }

    public static class SelfServiceQuotation {
        public static final String ATTRIBUTE_DEFAULT_ROW_COUNT = "quote.row.count";

        public static final String ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT = "distrelec.default.quote.submit.limit";

        public static final String ATTRIBUTE_DEFAULT_DELETE_QUOTATION_CHUNK_SIZE = "distrelec.default.delete.quotation.chunk.size";

        public static class Email {
            public static final String ATTRIBUTE_QUOTATION_CSV_HEADER = "distrelec.default.quotation.csv.header";

            public static final String ATTRIBUTE_CART_QUOTATION_CSV_HEADER = "distrelec.default.cart.quotation.csv.header";
        }
    }

    public static class Taxonomy {
        public static final String ROOT_CATEGORY_CODE_KEY = "distrelec.taxonomy.root.category.code";

        public static final String CATEGORY_MAX_LEVEL_KEY = "distrelec.taxonomy.category.level.max";

        public static final String DELETE_UNUSED_NAVNODES = "distrelec.taxonomy.navnodes.delete";
    }

    public static class Feedback {
        public static final String ZERO_RESULT_FEEDBACK_MONTHS_AGO = "zero.result.feedback.months.ago";
    }

    public static class InternalLink {
        public static final String INTERNAL_LINK_FORCE_RECALCULATION = "internal.link.force.recalculation";
    }

    public static class Cache {
        public static final String TOP_CATEGORY_TTL_SECONDS = "top.categories.ttlseconds";

        public static final String SSR_CACHE_CONTAINER_NAME = "azure.ssr.cache.container.name";

        public static final String SSR_CACHE_TTL_HOUR = "azure.ssr.cache.ttl.hour";

        public static final String SSR_CACHE_CLEANUP_SEGMENT_SIZE = "azure.ssr.cache.cleanup.segment.size";
    }

    public static class Tracking {
        public static final String GTM_AUTH = "gtm.tag.auth";

        public static final String GTM_COOKIES_WIN = "gtm.tag.cookies_win";

        public static final String GTM_PREVIEW = "gtm.tag.preview";

        public static final String GTM_TAG_ID = "gtm.tag.id";
    }

    public static class Bloomreach {
        public static final String SCRIPT_TOKEN = "bloomreach.api.project.token";
    }

    public class Pim {
        public static final String ATTRIBUTE_PIM_DEFAULT_LANGUAGE = "distrelec.pim.default.language";

    }

    public class SecurityResponseHeaders {
        public static final String ATTRIBUTE_CSP_SAVE_IP = "distrelec.response.header.save.remote.address";

        public static final String ATTRIBUTE_SAVE_REPORT_IN_DB = "distrelec.response.header.save.report.db";

    }
}
