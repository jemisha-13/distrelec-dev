package com.namics.distrelec.b2b.core.dataimport.processor;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.impex.jalo.imp.ImpExImportReader;
import de.hybris.platform.impex.jalo.imp.MultiThreadedImpExImportReader;
import de.hybris.platform.impex.jalo.imp.MultiThreadedImportProcessor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.impex.jalo.imp.ValueLine.ValueEntry;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.ComposedType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Only update hybris Item if the lastModifiedErp date from the impex is newer then the one stored in the database. Otherwise skip / ignore
 * the line. Im there is no lastModifiedErp column in the impex file or the lastModifiedErp value cannot be parsed, continue normal impex
 * import process.
 *
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class LastModifiedErpImportProcessor extends MultiThreadedImportProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(LastModifiedErpImportProcessor.class);

    public static final String COLUMN_DESCRIPTOR_LAST_MODIFIED_ERP = "lastModifiedErp";
    public static final String COLUMN_MODIFIER_DATE_FORMAT = "dateformat";
    public static final String COLUMN_IS_UPDATE_BLOCKED = "erpUpdateBlocked";
    private static final String IGNORE = "<ignore>";

    @Override
    public void init(ImpExImportReader reader) {
        if(reader instanceof MultiThreadedImpExImportReader){
            MultiThreadedImpExImportReader multiThreadedReader = (MultiThreadedImpExImportReader)reader;
            setMaxThreads(multiThreadedReader.getMaxThreads());
        }else{
            //This should never occur, but set to single thread just in case it does
            setMaxThreads(1);
        }
        super.init(reader);
    }

    @Override
    protected void processUpdateLine(final Item item, final ValueLine valueLine, final HeaderDescriptor headerDescriptor, final ComposedType targetType)
            throws ImpExException {
        try {
            final Date lastModifiedErpImpex = getLastModifiedErpDateValueLine(valueLine, headerDescriptor);
            final Date lastModifiedErpItem = (Date) item.getAttribute(COLUMN_DESCRIPTOR_LAST_MODIFIED_ERP);
            Boolean isLineUpdateBlocked = (Boolean) item.getAttribute(COLUMN_IS_UPDATE_BLOCKED);


            if (lastModifiedErpItem == null || lastModifiedErpImpex == null || lastModifiedErpItem.before(lastModifiedErpImpex)) {
                if (BooleanUtils.isTrue(isLineUpdateBlocked)) {
                    skipLine(item, valueLine, isLineUpdateBlocked);
                } else {
                    super.processUpdateLine(item, valueLine, headerDescriptor, targetType);
                }
            } else {
                skipLine(item, valueLine, isLineUpdateBlocked);
            }
        } catch (final JaloInvalidParameterException | JaloSecurityException e) {
            LOG.error("Could not read attribute lastModifiedErp on item", e);
        }
    }

    private void skipLine(Item item, ValueLine valueLine, Boolean isLineUpdateBlocked) {
        if(BooleanUtils.isTrue(isLineUpdateBlocked)){
            LOG.info("Item type {} with PK {} was blocked for update", item.getComposedType().getCode(), item.getPK());
        }
        valueLine.resolve(item, Collections.emptyList());
    }

    /**
     * Get the last modified erp date from the impex line
     *
     * @param valueLine
     *            impex line
     * @param headerDescriptor
     *            impex header
     * @return the last modified erp date form impex line
     */
    public Date getLastModifiedErpDateValueLine(final ValueLine valueLine, final HeaderDescriptor headerDescriptor) {
        final Collection<AbstractColumnDescriptor> descriptors = headerDescriptor.getColumnsByQualifier(COLUMN_DESCRIPTOR_LAST_MODIFIED_ERP);
        if (CollectionUtils.isNotEmpty(descriptors)) {
            final AbstractColumnDescriptor descriptor = descriptors.iterator().next();
            final int position = descriptor.getValuePosition();
            if (descriptor.getDescriptorData() != null) {
                final String dateFormatString = descriptor.getDescriptorData().getModifier(COLUMN_MODIFIER_DATE_FORMAT);
                if (dateFormatString != null) {
                    final DateFormat dateFormat = new SimpleDateFormat(dateFormatString, Locale.ROOT);
                    final String dateString = getOriginalValueFromValueEntry(valueLine.getValueEntry(position));

                    try {
                        if (dateString != null) {
                            return dateFormat.parse(dateString);
                        }
                    } catch (final ParseException e) {
                        LOG.error("Could not parse date", e);
                    }
                }
            }
        }

        return null;
    }

    /**
     * in multiple passes, the string value can start with an <ignore> for resolved values. e.g. "<ignore>20080812093716"
     *
     * @param valueEntry
     * @return the original csv line value
     */
    private String getOriginalValueFromValueEntry(final ValueEntry valueEntry) {
        if (valueEntry.isIgnore()) {
            return StringUtils.removeStart(valueEntry.getCellValue(), IGNORE);
        }

        return valueEntry.getCellValue();
    }
}
