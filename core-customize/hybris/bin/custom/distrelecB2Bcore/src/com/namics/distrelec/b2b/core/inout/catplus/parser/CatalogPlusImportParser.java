package com.namics.distrelec.b2b.core.inout.catplus.parser;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.XMLReader;

import com.namics.distrelec.b2b.core.inout.catplus.model.ArticlePriceType;
import com.namics.distrelec.b2b.core.inout.catplus.model.ArticleType;
import com.namics.distrelec.b2b.core.inout.catplus.model.FeatureType;
import com.namics.distrelec.b2b.core.inout.catplus.model.ObjectFactory;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.enums.ProductTaxGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code CatalogPlusImportParser}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="francesco.bersani@distrelec.com">Francesco Bersani</a>, Distrelec
 * @since Distrelec 1.0
 */
public class CatalogPlusImportParser {

    private static final String CATALOG_PLUS_PRODUCT_CATALOG = "distrelecCatalogPlusProductCatalog";
    private static final Logger LOG = Logger.getLogger(CatalogPlusImportParser.class);
    private final ConcurrentLinkedQueue<ProductModel> PRODUCT_QUEUE = new ConcurrentLinkedQueue<ProductModel>();
    private final ConcurrentLinkedQueue<ProductModel> GARBAGE_CANDIDATES = new ConcurrentLinkedQueue<ProductModel>();
    private static final String EXTERNAL_DTD_LOADING_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    @Autowired
    private ModelService modelService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private CommonI18NService commonI18NService;
    @Autowired
    private DistrelecCodelistService codeListService;
    private boolean running = true;
    private KeyGenerator keyGenerator;
    private final AtomicInteger EL_COUNTER = new AtomicInteger(0);
    private final AtomicInteger PROD_COUNTER = new AtomicInteger(0);
    private List<DistSalesOrgProductModel> salesOrgs;

    /**
     * Parse the XML file given by its name {@code fileName} to produce the Catalog+ items
     * 
     * @param fileName
     *            the name of the file to be parsed
     * @return {@code true} if the XML file parsing finishes successfully, else {@code false}
     * @see {@link #parse(java.io.File)}
     */
    public boolean parse(final String fileName) {
        validateParameterNotNull(fileName, "File name cannot be null");
        return parse(new File(fileName));
    }

    /**
     * Parse the specified XML file to produce the Catalog+ items
     * 
     * @param file
     *            the file to be parsed
     * @return {@code true} if the XML file parsing finishes successfully, else {@code false}
     */
    public boolean parse(final File file) {
        final long time = System.currentTimeMillis();
        validateParameterNotNull(file, "File cannot be null");
        running = true;
        try {
            // Create JAXBContext
            final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            // Create the XML unmarshaller
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            // Install the callback on all ArticleType instances
            unmarshaller.setListener(createUnmarshalListener());
            unmarshaller.setSchema(null);
            // create a new XML parser
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final XMLReader reader = factory.newSAXParser().getXMLReader();
            reader.setFeature(EXTERNAL_DTD_LOADING_FEATURE, false);
            reader.setContentHandler(unmarshaller.getUnmarshallerHandler());

            // Create asynchronous tasks
            final Thread[] asyncTasks = new Thread[2];
            // Create a new asynchronous saver task
            asyncTasks[0] = new Thread(createAsyncSaverTask(), "catplus-saver-task");
            // Create a new asynchronous cleaner task
            asyncTasks[1] = new Thread(createAsyncCleanerTask(), "catplus-cleaner-task");
            // Starts asynchronous tasks
            for (final Thread task : asyncTasks) {
                task.start();
            }

            // Parse the file in the main thread
            reader.parse(file.toURI().toString());
            running = false;
            // Wait for asynchronous tasks termination
            for (final Thread task : asyncTasks) {
                task.join();
            }
            return true;
        } catch (final Exception exp) {
            running = false;
            LOG.warn(exp.getMessage(), exp);
            return false;
        } finally {
            LOG.info("Total number of processed XML Article elements : " + EL_COUNTER.getAndSet(0));
            LOG.info("Total number of successfully processed products : " + PROD_COUNTER.getAndSet(0));
            LOG.info("Total processing time : " + formatTime(System.currentTimeMillis() - time));
        }
    }

    /**
     * Create new {@code ProductModel} and populate it from the specified {@code ArticleType}
     * 
     * @param article
     *            the XML source element
     * @return a new instance of {@code ProductModel}
     */
    private ProductModel createProduct(final ArticleType article) {
        if (article == null) {
            return null;
        }

        final ProductModel product = getModelService().create(ProductModel.class);
        product.setCatalogVersion(catalogService.getCatalogForId(CATALOG_PLUS_PRODUCT_CATALOG).getActiveCatalogVersion());
        product.setCode(getKeyGeneratorViaLookup().generate().toString());
        product.setUnit(unitService.getUnitForCode("PC"));
        product.setCatPlusSupplierAID(article.getSupplierAid());
        product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
        product.setShowUsedLabel(true);
        product.setEurope1PriceFactory_PTG(ProductTaxGroup.valueOf("1"));

        // product.setSalesOrgSpecificProductAttributes(salesOrgs);

        if (article.getArticleDetails() != null) {
            product.setManufacturerAID(article.getArticleDetails().getManufacturerAid());
            product.setDescription(article.getArticleDetails().getShortDescription());
            product.setManufacturerName(article.getArticleDetails().getManufacturerName());
            product.setManufacturerTypeDescription(article.getArticleDetails().getManufacturerTypeDescr());
            product.setName(article.getArticleDetails().getShortDescription());
            product.setSummary(article.getArticleDetails().getLongDescription());
            product.setDeliveryTime(Double.valueOf(article.getArticleDetails().getDeliveryTime()));
            product.setEan(article.getArticleDetails().getEan());
        }

        if (article.getArticleOrderDetails() != null) {
            product.setOrderQuantityInterval(Integer.valueOf(article.getArticleOrderDetails().getQuantityInterval()));
            product.setMinOrderQuantity(Integer.valueOf(article.getArticleOrderDetails().getQuantityMin()));
        }

        if (article.getArticlePriceDetails() != null && article.getArticlePriceDetails().getArticlePrice() != null
                && !article.getArticlePriceDetails().getArticlePrice().isEmpty()) {
            final Collection<PriceRowModel> prices = new ArrayList<PriceRowModel>();

            for (final ArticlePriceType price : article.getArticlePriceDetails().getArticlePrice()) {
                final DistPriceRowModel priceRow = getModelService().create(DistPriceRowModel.class);
                // priceRow.setSpecialPrice(Boolean.TRUE);
                priceRow.setCurrency(getCommonI18NService().getCurrency(price.getCurrency() == null ? "EUR" : price.getCurrency()));
                priceRow.setGiveAwayPrice(Boolean.FALSE);
                priceRow.setNet(Boolean.valueOf(price.getTax() == 0));
                priceRow.setPrice(Double.valueOf(price.getAmount()));
                priceRow.setUnitFactor(Integer.valueOf(1));
                priceRow.setUnit(unitService.getUnitForCode("PC"));
                priceRow.setProduct(product);
                priceRow.setCatalogVersion(product.getCatalogVersion());
                prices.add(priceRow);
            }
            product.setEurope1Prices(prices);
        }

        if (article.getArticleFeatures() != null && !article.getArticleFeatures().getFeatureList().isEmpty()) {
            for (final FeatureType feature : article.getArticleFeatures().getFeatureList()) {
                if ("RoHS".equalsIgnoreCase(feature.getFname())) {
                    product.setRohs(codeListService.getDistrelecRestrictionOfHazardousSubstances("14"));
                }
            }
        }

        return product;
    }

    /**
     * Create a new asynchronous saver task.
     * 
     * @return a new {@code Runnable} task
     */
    private Runnable createAsyncSaverTask() {
        return new Runnable() {

            @Override
            public void run() {
                if (!Registry.hasCurrentTenant()) {
                    Registry.activateMasterTenant();
                }

                final User user = UserManager.getInstance().getUserByLogin("admin");

                SessionContext ctx = null;
                try {
                    ctx = JaloSession.getCurrentSession().createLocalSessionContext();
                    ctx.setUser(user);

                    while (running || !PRODUCT_QUEUE.isEmpty()) {
                        while (running && PRODUCT_QUEUE.isEmpty()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // NOP
                            }
                        }

                        ProductModel product;

                        while ((product = PRODUCT_QUEUE.poll()) != null) {
                            try {
                                getModelService().save(product);
                                PROD_COUNTER.incrementAndGet();
                            } catch (Exception exp) {
                                // NOP
                                LOG.error("Unable to save Catalog+ product with SupplierAID : " + product.getCatPlusSupplierAID(), exp);
                            } finally {
                                GARBAGE_CANDIDATES.offer(product);
                            }
                        }
                    }
                } finally {
                    if (ctx != null) {
                        JaloSession.getCurrentSession().removeLocalSessionContext();
                    }
                }
            }
        };
    }

    /**
     * Create a new asynchronous cleaner task.
     * 
     * @return a new {@code Runnable} task
     */
    private Runnable createAsyncCleanerTask() {
        return new Runnable() {

            @Override
            public void run() {
                if (!Registry.hasCurrentTenant()) {
                    Registry.activateMasterTenant();
                }

                final User user = UserManager.getInstance().getUserByLogin("admin");

                SessionContext ctx = null;
                try {
                    ctx = JaloSession.getCurrentSession().createLocalSessionContext();
                    ctx.setUser(user);

                    while (running || !GARBAGE_CANDIDATES.isEmpty()) {
                        while (running && GARBAGE_CANDIDATES.isEmpty()) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                // NOP
                            }
                        }

                        ProductModel product;

                        while ((product = GARBAGE_CANDIDATES.poll()) != null) {
                            try {
                                getModelService().detach(product);
                            } catch (Exception exp) {
                                // NOP
                            }
                        }
                    }
                } finally {
                    if (ctx != null) {
                        JaloSession.getCurrentSession().removeLocalSessionContext();
                    }
                }
            }
        };
    }

    /**
     * Create a new {@code Unmarshaller.Listener} for the XML parser
     * 
     * @return a new instance of {@code Unmarshaller.Listener}
     */
    private Unmarshaller.Listener createUnmarshalListener() {
        return new Unmarshaller.Listener() {

            @Override
            public void beforeUnmarshal(final Object target, final Object parent) {
                // NOP
            }

            @Override
            public void afterUnmarshal(final Object target, final Object parent) {
                if (target instanceof ArticleType) {
                    try {
                        EL_COUNTER.incrementAndGet();
                        final ArticleType article = (ArticleType) target;
                        final ProductModel product = createProduct(article);
                        if (product != null) {
                            PRODUCT_QUEUE.offer(product);
                        }
                    } catch (final Exception exp) {
                        LOG.warn(exp.getMessage(), exp);
                    }
                }
            }
        };
    }

    /**
     * Format the time amount given in milliseconds
     * 
     * @param millis
     *            the time in milliseconds
     * @return a human readable string representation of the specified time
     */
    public String formatTime(final long millis) {
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis - hours * 3600 * 1000);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis - hours * 3600 * 1000 - minutes * 60 * 1000);
        return String.format("%dh %dmin %dsec", Integer.valueOf((int) hours), Integer.valueOf((int) minutes), Integer.valueOf((int) seconds));
    }

    /* Getters & Setters */

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public KeyGenerator getKeyGeneratorViaLookup() {
        return keyGenerator;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

}
