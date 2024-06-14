package com.namics.distrelec.b2b.core.service.netmargin.impl;

import com.namics.distrelec.b2b.core.service.netmargin.NetMarginCalculator;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Test;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class NetMarginCalculatorImplTest extends ServicelayerTransactionalTest {

    @Resource
    private NetMarginCalculator netMarginCalculator;

    @Test
    public void testCalculateRank() {
        // Net margin < 0
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.0f))
                .isEqualTo(9);
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.15f))
                .isEqualTo(9);
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.3f))
                .isEqualTo(9);
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.4f))
                .isEqualTo(9);
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.5f))
                .isEqualTo(9);
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.6f))
                .isEqualTo(9);
        assertThat(netMarginCalculator.calculateNetMarginRank(-1.0f, 0.8f))
                .isEqualTo(9);

        // Net margin 0 - 2
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.0f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.15f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.3f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.4f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.5f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.6f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(1.0f, 0.8f))
                .isEqualTo(4);

        // Net margin 2 - 5
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.0f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.15f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.3f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.4f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.5f))
                .isEqualTo(6);
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.6f))
                .isEqualTo(5);
        assertThat(netMarginCalculator.calculateNetMarginRank(3.0f, 0.8f))
                .isEqualTo(3);

        // Net margin 5 - 10
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.0f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.15f))
                .isEqualTo(8);
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.3f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.4f))
                .isEqualTo(6);
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.5f))
                .isEqualTo(5);
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.6f))
                .isEqualTo(4);
        assertThat(netMarginCalculator.calculateNetMarginRank(7.0f, 0.8f))
                .isEqualTo(2);

        // Net margin 10 - 40
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.0f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.15f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.3f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.4f))
                .isEqualTo(5);
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.5f))
                .isEqualTo(4);
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.6f))
                .isEqualTo(3);
        assertThat(netMarginCalculator.calculateNetMarginRank(20.0f, 0.8f))
                .isEqualTo(2);

        // Net margin 40 - 75
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.0f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.15f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.3f))
                .isEqualTo(6);
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.4f))
                .isEqualTo(4);
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.5f))
                .isEqualTo(3);
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.6f))
                .isEqualTo(2);
        assertThat(netMarginCalculator.calculateNetMarginRank(50.0f, 0.8f))
                .isEqualTo(2);

        // Net margin 75 - 150
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.0f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.15f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.3f))
                .isEqualTo(5);
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.4f))
                .isEqualTo(3);
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.5f))
                .isEqualTo(2);
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.6f))
                .isEqualTo(2);
        assertThat(netMarginCalculator.calculateNetMarginRank(100.0f, 0.8f))
                .isEqualTo(2);

        // Net margin 150 - 300
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.0f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.15f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.3f))
                .isEqualTo(4);
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.4f))
                .isEqualTo(3);
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.5f))
                .isEqualTo(2);
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.6f))
                .isEqualTo(1);
        assertThat(netMarginCalculator.calculateNetMarginRank(200.0f, 0.8f))
                .isEqualTo(1);

        // Net margin > 300
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.0f))
                .isEqualTo(7);
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.15f))
                .isEqualTo(6);
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.3f))
                .isEqualTo(3);
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.4f))
                .isEqualTo(2);
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.5f))
                .isEqualTo(1);
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.6f))
                .isEqualTo(1);
        assertThat(netMarginCalculator.calculateNetMarginRank(400.0f, 0.8f))
                .isEqualTo(1);
    }

}
