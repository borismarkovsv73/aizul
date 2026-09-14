package com.ftn.sbnz.service.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import com.ftn.sbnz.model.dto.MoveDTO;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.utils.JsonLoader;
import com.ftn.sbnz.service.SampleAppService;

class SampleAppServiceEvaluationTest {

    @Test
    void completeEvaluationReturnsUniqueExplanations() throws Exception {
        KieServices services = KieServices.Factory.get();
        KieContainer container = services.newKieContainer(
            services.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));
        SampleAppService service = new SampleAppService(container);
        GameState state = JsonLoader.loadGameStateFromClasspath("backward_chain_combined_test.json");

        MoveDTO result = service.getBestMove(state);

        assertNotNull(result);
        List<Map<Boolean, String>> explanations = result.getAppliedRules();
        Set<Map<Boolean, String>> uniqueExplanations = new HashSet<>(explanations);
        assertEquals(uniqueExplanations.size(), explanations.size());
    }
}
