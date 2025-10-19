package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.dto.MoveDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<Move>> forwardChain() {
		try {
			List<Move> moves = sampleService.forwardChain();
			return ResponseEntity.ok(moves);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/unified_backward_chain")
	public ResponseEntity<List<Move>> unifiedBackwardChain() {
		try {
			List<Move> moves = sampleService.unifiedBackwardChainTest();
			return ResponseEntity.ok(moves);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/best_move")
	public ResponseEntity<MoveDTO> getBestMove(@RequestBody GameState gameState) {
		try {
			if (gameState == null || gameState.getPlayers() == null || gameState.getPlayers().isEmpty()) {
				return ResponseEntity.badRequest().build();
			}
			
			MoveDTO move = sampleService.getBestMove(gameState);
			if (move == null) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(move);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


}