/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.docu

import com.namics.distrelec.occ.test.test.groovy.webservicetests.DocumentationSuiteGuard
import com.namics.distrelec.occ.test.test.groovy.webservicetests.docu.TestUtilMethodInvEvent.MethodInvEventType
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Collect summary for tests and delegate to strategies which write summary to files
 */
class BaseWSTestWatcher extends TestWatcher implements TestUtilMethodInvListener {

    def saveStrategies = [new SaveByResourcesWSOutputStrategy(), new DefaultSaveWSOuptutStrategy()]


    @Override
    public void onEvent(TestUtilMethodInvEvent ev) {

        if (summary == null)
            throw new IllegalStateException();

        switch (ev.type) {
            case MethodInvEventType.GET_CONNECTION:
                summary.addRequest(ev.resource, ev.accept, ev.method);
                break;
            case MethodInvEventType.GET_RESPONSE:
                summary.addResponse(ev.response);
                break;
            default:
                break;
        }
    }

    private SummaryOfRunningTest summary = null;

    @Override
    protected void starting(Description desc) {
        if (DocumentationSuiteGuard.isSuiteRunning()) {
            summary = new SummaryOfRunningTest();
            summary.testName = desc.getMethodName();
            TestUtilCustomDelegatingMetaClass.addEventListener(this);
        }
    }

    @Override
    protected void failed(Throwable t, Description desc) {
        if (summary != null) {
            for (SaveWSOutputStrategy saveStrategy : saveStrategies) {
                saveStrategy.saveFailedTest(summary, t)
            }
            TestUtilCustomDelegatingMetaClass.removeEventListener(this);
            summary = null;
        }
    }

    @Override
    protected void succeeded(Description desc) {
        if (summary != null) {
            for (SaveWSOutputStrategy saveStrategy : saveStrategies) {
                saveStrategy.saveSucceededTest(summary);
            }
            TestUtilCustomDelegatingMetaClass.removeEventListener(this);
            summary = null;
        }
    }
}
