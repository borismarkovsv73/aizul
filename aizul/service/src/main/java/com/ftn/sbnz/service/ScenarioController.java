package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Player; // Change to your actual entity import
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/scenario")
public class ScenarioController {

    private static Logger log = LoggerFactory.getLogger(ScenarioController.class);

	private final SampleAppService sampleService;

	@Autowired
	public ScenarioController(SampleAppService sampleService) {
		this.sampleService = sampleService;
	}

	@RequestMapping(value = "/item", method = RequestMethod.GET, produces = "application/json")
	public Player getQuestions() {

		Player newItem = new Player();

		log.debug("Item request received for: " + newItem);

		Player i2 = sampleService.getClassifiedItem(newItem);

		return i2;
	}
}