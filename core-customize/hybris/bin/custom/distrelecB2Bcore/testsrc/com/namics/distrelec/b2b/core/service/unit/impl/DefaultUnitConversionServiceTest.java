package com.namics.distrelec.b2b.core.service.unit.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.b2b.core.search.data.Unit;
import com.namics.distrelec.b2b.core.event.DistQuoteEmailEventListener;
import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;

import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultUnitConversionServiceTest {
    protected static final Logger LOG = LogManager.getLogger(DistQuoteEmailEventListener.class);

    @InjectMocks
    private DistUnitConversionService defaultUnitConversionService = new DistUnitConversionService();

    @Mock
    private DistClassificationDao distClassificationDao;

    @Mock
    private ClassificationSystemService classificationSystemService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    private Unit unit1, unit2, unit3, unit4, unit5, unit6, unit7, unit8;

    private Unit unit9, unit10, unit11, unit12, unit13, unit14, unit15, unit16;

    private List<Unit> classificationAttributeUnit;

    @Before
    public void setup() throws Exception {
        unit1 = new Unit();
        unit1.setCode("std.unit.gbph");
        unit1.setSymbol("GB/h");
        unit1.setUnitType("std.unit.gbph");
        unit1.setConversionFactor(1d);

        unit2 = new Unit();
        unit2.setCode("cm3_min");
        unit2.setSymbol("cm³/min");
        unit2.setUnitType("unece.unit.MQH");
        unit2.setConversionFactor(0.00006);

        unit3 = new Unit();
        unit3.setCode("unece.unit.MQH");
        unit3.setSymbol("m³/h");
        unit3.setUnitType("unece.unit.MQH");
        unit3.setConversionFactor(1d);

        unit4 = new Unit();
        unit4.setCode("Bit");
        unit4.setSymbol("Bit");
        unit4.setUnitType("Bit");
        unit4.setConversionFactor(1d);

        unit5 = new Unit();
        unit5.setCode("unece.unit.D36");
        unit5.setSymbol("MBit");
        unit5.setUnitType("Bit");
        unit5.setConversionFactor(1048576d);

        unit6 = new Unit();
        unit6.setCode("unece.unit.MOhm");
        unit6.setSymbol("MOhm");
        unit6.setUnitType("unece.unit.ohm.text");
        unit6.setConversionFactor(1000000d);

        unit7 = new Unit();
        unit7.setCode("unece.unit.ohm.text");
        unit7.setSymbol("Ohm");
        unit7.setUnitType("unece.unit.ohm.text");
        unit7.setConversionFactor(1d);

        unit8 = new Unit();
        unit8.setCode("unece.unit.milOhm");
        unit8.setSymbol("mOhm");
        unit8.setUnitType("unece.unit.ohm.text");
        unit8.setConversionFactor(0.001d);

        unit9 = new Unit();
        unit9.setCode("unece.unit.kOhm");
        unit9.setSymbol("kOhm");
        unit9.setUnitType("unece.unit.ohm.text");
        unit9.setConversionFactor(1000d);

        unit10 = new Unit();
        unit10.setCode("unece.unit.CMT");
        unit10.setSymbol("cm");
        unit10.setUnitType("unece.unit.MTR");
        unit10.setConversionFactor(0.01d);

        unit11 = new Unit();
        unit11.setCode("unece.unit.MTR");
        unit11.setSymbol("m");
        unit11.setUnitType("unece.unit.MTR");
        unit11.setConversionFactor(1d);

        unit12 = new Unit();
        unit12.setCode("unece.unit.KEL");
        unit12.setSymbol("K");
        unit12.setUnitType("unece.unit.KEL");
        unit12.setConversionFactor(1d);

        unit13 = new Unit();
        unit13.setCode("unece.unit.pOhm");
        unit13.setSymbol("pOhm");
        unit13.setUnitType("unece.unit.ohm.text");
        unit13.setConversionFactor(0.000000000001);

        unit14 = new Unit();
        unit14.setCode("unece.unit.MW");
        unit14.setSymbol("MW");
        unit14.setUnitType("unece.unit.WTT");
        unit14.setConversionFactor(1000000d);

        unit15 = new Unit();
        unit15.setCode("unece.unit.mW");
        unit15.setSymbol("mW");
        unit15.setUnitType("unece.unit.WTT");
        unit15.setConversionFactor(0.001);

        unit16 = new Unit();
        unit16.setCode("unece.unit.WTT");
        unit16.setSymbol("W");
        unit16.setUnitType("unece.unit.WTT");
        unit16.setConversionFactor(1d);

        classificationAttributeUnit = List.of(
          unit1, unit2, unit3, unit4,
          unit5, unit6, unit7, unit8,
          unit9, unit10, unit11, unit12,
          unit13, unit14, unit15, unit16);

        when(distClassificationDao.findAttributeUnitsWithUnitGroup()).thenReturn(classificationAttributeUnit);
        when(configuration.getString("unitconversion.regex.before.unit")).thenReturn("(.*(?:^| ))");
        when(configuration.getString("unitconversion.regex.after.unit")).thenReturn("((?:$| ).*)");
        when(configuration.getString("unitconversion.regex.standardcase")).thenReturn("([0-9]+[.]?[0-9]*)[ ]?(?i)(%s)");
        when(configuration.getString("unitconversion.regex.specialcase")).thenReturn("([0-9]+[.]?[0-9]*)[ ]?%s((?i)%s)");
        when(configuration.getInt(any(String.class))).thenReturn(1);
        when(configurationService.getConfiguration()).thenReturn(configuration);

        defaultUnitConversionService.afterPropertiesSet();
    }

    @Test
    public void stringFormat() {
        String converted = defaultUnitConversionService.convertUnitsInText("1k1");
        assertThat(converted).isEqualTo("1100 Ohm");
    }

    @Test
    public void normaliseUnits() {
        String converted = defaultUnitConversionService.convertUnitsInText("100Ohm");
        assertThat(converted).isEqualTo("100 Ohm");
    }

    @Test
    public void convertMilliOhmsUnits() {
        String converted = defaultUnitConversionService.convertUnitsInText("100 mOhm");
        assertThat(converted).isEqualTo("0.1 Ohm");
    }

    @Test
    public void convertMilliOhmsUnits2() {
        String converted = defaultUnitConversionService.convertUnitsInText("100 mohm");
        assertThat(converted).isEqualTo("0.1 Ohm");
    }

    @Test
    public void convertMilliOhmsUnits3() {
        String converted = defaultUnitConversionService.convertUnitsInText("100 mOHM");
        assertThat(converted).isEqualTo("0.1 Ohm");
    }

    @Test
    public void convertMilliWattUnit() {
        String converted = defaultUnitConversionService.convertUnitsInText("100 mW");
        assertThat(converted).isEqualTo("0.1 W");
    }

    @Test
    public void convertMegaWattUnit() {
        String converted = defaultUnitConversionService.convertUnitsInText("1 MW");
        assertThat(converted).isEqualTo("1000000 W");
    }

    @Test
    public void convertMegaOhmsUnits() {
        String converted = defaultUnitConversionService.convertUnitsInText("1 MOhm");
        assertThat(converted).isEqualTo("1000000 Ohm");
    }

    @Test
    public void convertNanoOhmsUnits() {
        String converted = defaultUnitConversionService.convertUnitsInText("1 pOhm");
        assertThat(converted).isEqualTo("0.000000000001 Ohm");
    }

    @Test
    public void convertKiloOhmsUnits() {
        String converted = defaultUnitConversionService.convertUnitsInText("120 kOhm");
        assertThat(converted).isEqualTo("120000 Ohm");
    }

    @Ignore
    @Test
    public void testConvertPerformance() {
        String converted;

        long t1 = System.currentTimeMillis();
        for (int i = 1; i < 1000; i++) {
            converted = defaultUnitConversionService.convertUnitsInText("120 µOhm");
            converted = defaultUnitConversionService.convertUnitsInText("AC-Nennspannung 1100mVAC");
            converted = defaultUnitConversionService.convertUnitsInText("120 µOhm");
            converted = defaultUnitConversionService.convertUnitsInText("100 mOhm");
            defaultUnitConversionService.convertUnitsInText("100Ohm");
        }

        LOG.info("time taken:{}ms", System.currentTimeMillis() - t1);
    }

    @Test
    public void convertCaseInsensitive() {

        String converted = defaultUnitConversionService.convertUnitsInText("12.3 CM");
        assertThat(converted).isEqualTo("0.123 m");
    }

    @Test
    public void convertSpecialCase1() {
        String converted = defaultUnitConversionService.convertUnitsInText("search for 10000 cm³/min in it");
        assertThat(converted).isEqualTo("search for 0.6 m³/h in it");
    }

    @Test
    public void convertSpecialCase2() {
        String converted = defaultUnitConversionService.convertUnitsInText("search for 10000 cm3min in it");
        assertThat(converted).isEqualTo("search for 0.6 m³/h in it");
    }

    @Test
    public void convertSpecialCase3() {
        String converted = defaultUnitConversionService.convertUnitsInText("search for with 2Mbit in it");
        assertThat(converted).isEqualTo("search for with 2097152 Bit in it");
    }

    @Test
    public void convertSpecialCase4() {
        String converted = defaultUnitConversionService.convertUnitsInText("search for 10000 cm3min with 2mbit in it");
        assertThat(converted).isEqualTo("search for 0.6 m³/h with 2097152 Bit in it");
    }

    @Test
    public void convertSpecialCase5() {
        String converted = defaultUnitConversionService.convertUnitsInText("1 kOHm search for 10000 cm3min with 2mbit in it");
        assertThat(converted).isEqualTo("1000 Ohm search for 0.6 m³/h with 2097152 Bit in it");
    }

    @Test
    public void ohmSpecialCase1k3() {
        String converted = defaultUnitConversionService.convertUnitsInText("resistor 1k3");
        assertThat(converted).isEqualTo("resistor 1300 Ohm");
    }

    @Test
    public void ohmSpecialCase1k1() {
        String converted = defaultUnitConversionService.convertUnitsInText("1k1");
        assertThat(converted).isEqualTo("1100 Ohm");
    }

    @Test
    public void ohmSpecialCase3k1() {
        String converted = defaultUnitConversionService.convertUnitsInText("resistor 27k5 after text");
        assertThat(converted).isEqualTo("resistor 27500 Ohm after text");
    }

    @Test
    public void ohmSpecialCaseWithMoreThanOneDigit() {
        String converted = defaultUnitConversionService.convertUnitsInText("resistor 27k55 after text");
        assertThat(converted).isEqualTo("resistor 27k55 after text");
    }

    @Test
    public void testGetUnitBySymbol() {
        Optional<Unit> result = defaultUnitConversionService.getUnitBySymbol("mW");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(unit15);
    }

    @Test
    public void testGetUnitBySymbolNull() {
        Optional<Unit> result = defaultUnitConversionService.getUnitBySymbol(null);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void testGetBaseUnitSymbolForUnitType() {
        String result = defaultUnitConversionService.getBaseUnitSymbolForUnitType(unit10.getUnitType());

        assertThat(unit10.getUnitType()).isEqualTo(unit11.getCode());
        assertThat(result).isEqualTo(unit11.getSymbol());
    }

    @Test
    public void testGetBaseUnitSymbolForUnitTypeNull() {
        String result = defaultUnitConversionService.getBaseUnitSymbolForUnitType(null);

        assertThat(result).isNull();
    }

    @Test
    public void convertToBaseUnit100cmTo1m() {
        BigDecimal result = defaultUnitConversionService.convertToBaseUnit(unit10, "100");

        assertThat(result.equals(BigDecimal.ONE));
    }

    @Test
    public void convertToBaseUnit20cmTo02m() {
        BigDecimal result = defaultUnitConversionService.convertToBaseUnit(unit10, "20");

        assertThat(result.equals(BigDecimal.valueOf(0.2)));
    }

    @Test
    public void convertToBaseUnit100cmTomInvalidNumber() {
        BigDecimal result = defaultUnitConversionService.convertToBaseUnit(unit10, "ABC");

        assertThat(result.equals(BigDecimal.ZERO));
    }

    @Test
    public void convertToBaseUnit100cmTomNullNumber() {
        BigDecimal result = defaultUnitConversionService.convertToBaseUnit(unit10, null);

        assertThat(result.equals(BigDecimal.ZERO));
    }

    @Test
    public void convertToBaseUnit100cmTomNullUnit() {
        BigDecimal result = defaultUnitConversionService.convertToBaseUnit(null, "100");

        assertThat(result.equals(BigDecimal.ZERO));
    }

    @Test
    public void convertToBaseUnit100cmTomNullConversion() {
        Unit emptyUnit = new Unit();
        BigDecimal result = defaultUnitConversionService.convertToBaseUnit(emptyUnit, "100");

        assertThat(result.equals(BigDecimal.ZERO));
    }

}
