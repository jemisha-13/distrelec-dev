package com.namics.distrelec.b2b.core.service.environment;

public interface RuntimeEnvironmentService {

    boolean isHeadless();

    void setHeadless(boolean headless);

}
