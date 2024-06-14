package com.namics.distrelec.b2b.core.service.eol.service.impl;

import com.namics.distrelec.b2b.core.service.classification.dao.DistProductFeaturesDao;
import com.namics.distrelec.b2b.core.service.eol.service.DistEOLProductsRemovalService;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DefaultDistEOLProductsRemovalService implements DistEOLProductsRemovalService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistEOLProductsRemovalService.class);

    public static final int CHUNK_SIZE = 100;

    @Autowired
    private DistProductDao distProductDao;

    @Autowired
    private StockService stockService;

    @Autowired
    private SessionService sessionService;

    private final TenantAwareThreadFactory tenantAwareThreadFactory = new TenantAwareThreadFactory(Registry.getCurrentTenant());

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistProductFeaturesDao distProductFeaturesDao;

    private ExecutorService executorService;

    @PostConstruct
    public void onCreate() {
        executorService = Executors.newFixedThreadPool(4, tenantAwareThreadFactory);
    }

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }

    @Override
    public void
    removeEOLProducts(int monthsWithReferences, int monthsWithoutReferences, Integer removeCount) {
        List<ProductModel> eolProducts;
        Optional<Integer> remaining = Optional.ofNullable(removeCount);
        do {
            Instant startTime = Instant.now();
            eolProducts = loadEolProducts(monthsWithReferences, monthsWithoutReferences, remaining);
            removeProductData(eolProducts);
            final int removedCount = eolProducts.size();
            remaining = remaining.map(r -> r - removedCount);
            Duration duration = Duration.between(startTime, Instant.now());
            LOG.info("Removed {} products in {}", eolProducts.size(), formatDuration(duration));
        } while (remaining.map(r -> r > 0).orElse(true) && !eolProducts.isEmpty());
    }

    private static String formatDuration(Duration duration) {
        return String.format("%02d:%02d.%03d", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart());
    }

    private List<ProductModel> loadEolProducts(int monthsWithReferences, int monthsWithoutReferences, Optional<Integer> remaining) {
        int toLoad = remaining.map(r -> Math.min(r, CHUNK_SIZE))
                .orElse(CHUNK_SIZE);
        return distProductDao.findEOLProductsForRemoval(getPastDate(monthsWithReferences),
                getPastDate(monthsWithoutReferences),
                toLoad);
    }

    private void removeProductData(List<ProductModel> products) {
        Map<String, ?> sessionMap = sessionService.getCurrentSession().getAllAttributes();
        List<Future<?>> futures = products.stream().map(p -> executorService.submit(() -> {
            sessionService.executeInLocalView(new SessionExecutionBody() {
                @Override
                public void executeWithoutResult() {
                    sessionMap.forEach((key, value) -> sessionService.getCurrentSession().setAttribute(key, value));
                    sessionService.setAttribute("use.fast.algorithms", Boolean.TRUE);
                    // Remove stock levels and price rows
                    modelService.removeAll(stockService.getAllStockLevels(p));
                    Collection<PriceRowModel> prices = p.getEurope1Prices();
                    modelService.removeAll(prices);
                    modelService.removeAll(p.getSalesOrgSpecificProductAttributes());
                    modelService.removeAll(distProductFeaturesDao.findProductFeaturesByProduct(p));
                }
            });
        })).collect(Collectors.toList());
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        modelService.removeAll(products);
    }


    private Date getPastDate(final int months) {
        final LocalDate localDate = LocalDate.now().minusMonths(months).minusDays(1L);
        final ZoneId defaultZoneId = ZoneId.systemDefault();
        return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
    }
}
