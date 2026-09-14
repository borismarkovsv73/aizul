package com.ftn.sbnz.service.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

class KjarRulesCompilationTest {

    @Test
    void allConfiguredRuleSessionsCanBeCreated() {
        KieServices services = KieServices.Factory.get();
        KieContainer container = services.newKieContainer(
            services.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));

        assertSessionCanBeCreated(container, "forwardSession");
        assertSessionCanBeCreated(container, "combinedBackwardSession");
        assertSessionCanBeCreated(container, "combinedBackwardSessionEnemy");
    }

    private static void assertSessionCanBeCreated(KieContainer container, String sessionName) {
        KieSession session = container.newKieSession(sessionName);
        assertNotNull(session, "Could not create KIE session " + sessionName);
        session.dispose();
    }
}
