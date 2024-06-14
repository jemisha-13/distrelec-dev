package com.namics.distrelec.b2b.core.deployment;

import java.util.Date;

/**
 * Returns a timestamp when a package is deployed on an environment. It is generated based on build.builddate timestamp
 * and it is not update if the same package is deployed multiple times.
 */
public interface DeploymentTimestampService {

    /**
     * A deployment timestamp.
     */
    Date getTimestamp();
}
