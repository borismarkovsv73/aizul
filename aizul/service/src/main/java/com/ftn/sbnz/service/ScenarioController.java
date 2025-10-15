package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Move;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scenario")
public class ScenarioController {

	private final SampleAppService sampleService;

	@Autowired
	public ScenarioController(SampleAppService sampleService) {
		this.sampleService = sampleService;
	}

	@GetMapping("/forward_chain")
	public List<Move> forwardChain() throws Exception {
		List<Move> moves = sampleService.forwardChain();
		return moves;
	}

	@GetMapping("/unified_backward_chain")
	public List<Move> unifiedBackwardChain() throws Exception {
		List<Move> moves = sampleService.unifiedBackwardChainTest();
		return moves;
	}
}