package com.namics.distrelec.b2b.core.dataimport.processor;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Contains a set of unit tests to test the functionality of {@link LastModifiedErpImportProcessor} in isolation
 *
 * @Author N D Clarke
 */
@UnitTest
public class LastModifiedErpImportProcessorTest extends ServicelayerTransactionalTest{

    private static final int FIRST_ELEMENT = 0;
    private static final int ZERO = 0;

    @Mock
    private PriceRow mockedPriceRow;

    @Mock
    private ValueLine mockedValueLine;

    @Mock
    private HeaderDescriptor mockedHeaderDescriptor;

    @Mock
    private ComposedType mockedComposedType;

    @InjectMocks
    private final LastModifiedErpImportProcessor lastModifiedErpImportProcessor = new LastModifiedErpImportProcessor();

    @Before
    public void setUp() {
        //This call enables Mockito to create the mocks and inject them into the class under test.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDateWhenHeaderDescriptorReturnsNothing() {
        when(mockedHeaderDescriptor.getColumnsByQualifier(Mockito.anyString())).thenReturn(Collections.EMPTY_LIST);

        Date result = lastModifiedErpImportProcessor.getLastModifiedErpDateValueLine(mockedValueLine, mockedHeaderDescriptor);
        assertThat(result).isNull();
        verify(mockedHeaderDescriptor).getColumnsByQualifier(Mockito.anyString());
    }

    @Test
    public void testGetDateButDescriptorDataIsNull() {
        List<AbstractColumnDescriptor> abstractColumnDescriptorList = createAbstractColumnDescriptorList();
        AbstractColumnDescriptor descriptor = abstractColumnDescriptorList.get(FIRST_ELEMENT);

        when(mockedHeaderDescriptor.getColumnsByQualifier(Mockito.anyString())).thenReturn(abstractColumnDescriptorList);

        when(descriptor.getValuePosition()).thenReturn(0);
        when(descriptor.getDescriptorData()).thenReturn(null);

        Date result = lastModifiedErpImportProcessor.getLastModifiedErpDateValueLine(mockedValueLine, mockedHeaderDescriptor);

        assertThat(result).isNull();
    }

    @Test
    public void testGetDateUnderNormalCircumstances() {
        List<AbstractColumnDescriptor> abstractColumnDescriptorList = createAbstractColumnDescriptorList();
        AbstractColumnDescriptor descriptor = abstractColumnDescriptorList.get(FIRST_ELEMENT);

        when(mockedHeaderDescriptor.getColumnsByQualifier(Mockito.anyString())).thenReturn(abstractColumnDescriptorList);

        when(descriptor.getValuePosition()).thenReturn(0);

        when(mockedValueLine.getValueEntry(0))
                .thenReturn(new ValueLine.ValueEntry(0, "05/04/2018"));
        AbstractDescriptor.DescriptorParams mockDescriptorData = mock(AbstractDescriptor.DescriptorParams.class);
        when(mockDescriptorData.getModifier(LastModifiedErpImportProcessor.COLUMN_MODIFIER_DATE_FORMAT))
                .thenReturn("dd/MM/yyyy");
        when(descriptor.getDescriptorData())
                .thenReturn(mockDescriptorData);

        Date result = lastModifiedErpImportProcessor.getLastModifiedErpDateValueLine(mockedValueLine, mockedHeaderDescriptor);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Date.class);
    }

    private List<AbstractColumnDescriptor> createAbstractColumnDescriptorList() {
        AbstractColumnDescriptor mockedAbstractColumnDescriptor = Mockito.mock(AbstractColumnDescriptor.class);
        when(mockedAbstractColumnDescriptor.getQualifier()).thenReturn("qualifier");

        List<AbstractColumnDescriptor> abstractColumnDescriptorList = new ArrayList<>();
        abstractColumnDescriptorList.add(mockedAbstractColumnDescriptor);

        return abstractColumnDescriptorList;
    }

}
