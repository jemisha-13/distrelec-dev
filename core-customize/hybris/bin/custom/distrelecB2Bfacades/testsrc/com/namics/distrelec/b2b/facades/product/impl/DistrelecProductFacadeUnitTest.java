package com.namics.distrelec.b2b.facades.product.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService;
import com.namics.distrelec.b2b.facades.product.data.ProductEnhancementData;

import de.hybris.bootstrap.annotations.UnitTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistrelecProductFacadeUnitTest {

    @InjectMocks
    private DefaultDistrelecProductFacade distrelecProductFacade;

    @Mock
    private DistWishlistService distWishlistService;

    private List<String> productCodes = new ArrayList<>(List.of("30143506", "30143505", "30143504", "30143503", "30143502"));

    private Map<String, List<String>> inShoppingList = Map.of("30143506", List.of("shop list 1A", "shop list 1B"),
                                                              "30143503", List.of("shop list 2A"));

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEnhancementsForProducts() {
        when(distWishlistService.productsInShoppingList(productCodes)).thenReturn(inShoppingList);

        List<ProductEnhancementData> enhancementsForProducts = distrelecProductFacade.getEnhancementsForProducts(productCodes, 10);

        assertEquals(enhancementsForProducts.size(), productCodes.size());
        assertEquals(enhancementsForProducts.get(0).isInShoppingList(), true);
        assertEquals(enhancementsForProducts.get(1).isInShoppingList(), false);
        assertEquals(enhancementsForProducts.get(2).isInShoppingList(), false);
        assertEquals(enhancementsForProducts.get(3).isInShoppingList(), true);
        assertEquals(enhancementsForProducts.get(4).isInShoppingList(), false);
    }

    @Test
    public void testGetEnhancementsForProductsLimitProducts() {
        when(distWishlistService.productsInShoppingList(productCodes)).thenReturn(inShoppingList);

        List<ProductEnhancementData> enhancementsForProducts = distrelecProductFacade.getEnhancementsForProducts(productCodes, 3);

        assertEquals(enhancementsForProducts.size(), 3);
    }
}
