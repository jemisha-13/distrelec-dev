package com.namics.distrelec.b2b.core.integration.azure.comparators;

import com.microsoft.azure.storage.blob.CloudBlob;
import de.hybris.platform.cloud.azure.hotfolder.remote.session.AzureBlobFileInfo;
import de.hybris.platform.cloud.commons.spring.integration.file.comparators.TimestampedObject;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Map;

public class CloudBlockBlobOrderComparator implements Comparator<CloudBlob>, InitializingBean {
    private static final Integer DEFAULT_PRIORITY = NumberUtils.INTEGER_ZERO;
    private Map<String, Integer> prefixPriority;
    private final Comparator<TimestampedObject> comparator;

    public CloudBlockBlobOrderComparator(Comparator<TimestampedObject> comparator) {
        this.comparator = comparator;
    }


    @Override
    public void afterPropertiesSet()
    {
        Assert.notEmpty(prefixPriority);
    }

    @Override
    public int compare(final CloudBlob cloudBlob, final CloudBlob cloudBlobOther)
    {
        // invert priority setting so files with higher priority go first
        int result = getPriority(cloudBlobOther).compareTo(getPriority(cloudBlob));
        if (result == 0)
        {
            result = Long.compare(AzureBlobFileInfo.getModified(cloudBlob), AzureBlobFileInfo.getModified(cloudBlobOther));
        }
        return result;
    }

    /**
     * Retrieves the priority for a file.
     *
     * @param cloudBlob
     *           the cloudBlob to get priority from
     * @return the configured priority, if one exists, otherwise the default priority
     */
    protected Integer getPriority(final CloudBlob cloudBlob)
    {
        for (final Map.Entry<String, Integer> prefix : prefixPriority.entrySet())
        {
            if (cloudBlob.getName().startsWith(prefix.getKey()))
            {
                return prefix.getValue();
            }
        }
        return DEFAULT_PRIORITY;
    }

    /**
     * @param prefixPriority
     *           the prefixPriority to set
     */
    public void setPrefixPriority(final Map<String, Integer> prefixPriority)
    {
        Assert.notEmpty(prefixPriority);
        this.prefixPriority = prefixPriority;
    }

    /**
     *
     * @return prefixPriority
     */
    protected Map<String, Integer> getPrefixPriority()
    {
        return prefixPriority;
    }
}
