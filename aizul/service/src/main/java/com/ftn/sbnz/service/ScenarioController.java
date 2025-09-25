package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.utils.JsonLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@GetMapping("/loadGameState")
	public GameState loadGameState() throws Exception {
		GameState gameState = JsonLoader.loadGameState("service/src/main/resources/boardstate.json");
		return gameState;
	}

	@GetMapping("/rule_1")
	public Move rule_1() throws Exception {
		Move primer = sampleService.rule_1();
		return primer;
	}

	@GetMapping("/rule_2")
	public List<Move> rule_2() throws Exception {
		List<Move> primer = sampleService.rule_2();
		return primer;
	}

	@GetMapping("/backward_chain")
	public List<Move> backwardChain() throws Exception {
		List<Move> moves = sampleService.backwardChainTest();
		return moves;
	}

	@GetMapping("/backward_chain_2")
	public List<Move> backwardChain2() throws Exception {
		List<Move> moves = sampleService.backwardChainTest2();
		return moves;
	}
}