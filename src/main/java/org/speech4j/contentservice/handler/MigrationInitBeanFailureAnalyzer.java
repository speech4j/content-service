package org.speech4j.contentservice.handler;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class MigrationInitBeanFailureAnalyzer extends AbstractFailureAnalyzer<BeanCreationException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, BeanCreationException cause) {
        return new FailureAnalysis(getDescription(cause), getAction(cause), cause);
    }

    private String getDescription(BeanCreationException ex) {
        return String.format("The bean [ %s ] could not be injected because of cause: %s",
                ex.getBeanName(),
                ex.getCause());
    }

    private String getAction(BeanCreationException ex) {
        return String.format("Consider creating a bean with name [ %s ] as root problem.",
                ex.getBeanName());
    }
}
