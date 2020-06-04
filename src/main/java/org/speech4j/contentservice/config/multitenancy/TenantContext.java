package org.speech4j.contentservice.config.multitenancy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {
    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();
    public static void setCurrentTenant(String tenant) {
        log.debug("Setting of tenant to " + tenant);
        currentTenant.set(tenant);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
