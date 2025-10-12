package com.ftn.sbnz.service;

import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.utils.GameStatePrinter;
import com.ftn.sbnz.model.utils.JsonLoader;
import com.ftn.sbnz.model.utils.MoveGenerator;
import com.ftn.sbnz.model.utils.BoardUtils;
import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;


@Service
public class SampleAppService {

	private static Logger log = LoggerFactory.getLogger(SampleAppService.class);

	private final KieContainer kieContainer;

	@Autowired
	public SampleAppService(KieContainer kieContainer) {
		log.info("Initialising a new example session.");
		this.kieContainer = kieContainer;
	}

	public List<Move> forwardChain() throws Exception{
		GameState mockGameState = JsonLoader.loadGameStateFromClasspath("backward_chain_combined_test.json");
		Player currentPlayer = mockGameState.getPlayers().get(0);
		
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);
		
		for (Move move : possibleMoves) {
			KieSession kieSession = kieContainer.newKieSession("forwardSession");

			Board originalBoard = currentPlayer.getBoard();
			Board boardAfterMove = BoardUtils.copyBoard(originalBoard);
			BoardUtils.applyMoveToBoard(boardAfterMove, move);

			kieSession.insert(move);
			kieSession.insert(boardAfterMove);

			int rulesFired = kieSession.fireAllRules();
			log.info("Applied " + rulesFired + " rules for move targeting row " + move.getTargetRow());
			
			kieSession.dispose();
		}

		for (Move move : possibleMoves) {
			GameStatePrinter.printMove(move);
			System.out.println(move.getScore());
		}

		return possibleMoves;
	}

	public List<Move> unifiedBackwardChainTest() throws Exception {
		GameState mockGameState = JsonLoader.loadGameStateFromClasspath("backward_chain_combined_test.json");
		Player currentPlayer = mockGameState.getPlayers().get(0);

		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);
		
		System.out.println("Generated " + possibleMoves.size() + " possible moves for testing");
		
		KieSession unifiedSession = kieContainer.newKieSession("combinedBackwardSession");

		Board testBoard = currentPlayer.getBoard();
		
		unifiedSession.insert(testBoard);
		unifiedSession.insert(mockGameState);
		unifiedSession.insert(currentPlayer);
		
		for (Move move : possibleMoves) {
			move.setScore(0); // Reset scores
			unifiedSession.insert(move);
		}

		int totalRules = unifiedSession.fireAllRules();
		System.out.println("Unified backward chaining fired " + totalRules + " rules total");
		unifiedSession.dispose();

		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));
		
		for (int i = 0; i < Math.min(10, possibleMoves.size()); i++) {
			Move move = possibleMoves.get(i);
			System.out.println(String.format("Rank %2d: %s - Score: %d", 
				(i+1), move.toString(), move.getScore()));
		}
		
		System.out.println("\nTop move analysis:");
		if (!possibleMoves.isEmpty()) {
			Move topMove = possibleMoves.get(0);
			System.out.println("Best move: " + topMove.toString() + " with score: " + topMove.getScore());
		}

		return possibleMoves;
	}

    public Player getClassifiedItem(Player newItem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClassifiedItem'");
    }

}
