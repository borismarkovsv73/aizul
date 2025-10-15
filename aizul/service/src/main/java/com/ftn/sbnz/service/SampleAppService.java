package com.ftn.sbnz.service;

import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.utils.JsonLoader;
import com.ftn.sbnz.model.utils.MoveGenerator;
import com.ftn.sbnz.model.utils.BoardUtils;
import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.dto.MoveDTO;


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
			kieSession.insert(mockGameState);
			kieSession.insert(currentPlayer);

			kieSession.fireAllRules();			
			kieSession.dispose();
		}

		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));
		
		for (int i = 0; i < Math.min(10, possibleMoves.size()); i++) {
			Move move = possibleMoves.get(i);
			System.out.println(String.format("Rank %2d: %s - Score: %d", 
				(i+1), move.toString(), move.getScore()));
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

	public MoveDTO getBestMove(GameState gameState) throws Exception {
		Player currentPlayer = gameState.getPlayers().get(0);
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(gameState, currentPlayer);
		
		for (Move move : possibleMoves) {
			move.setScore(0);
		}
		
		// 1. Apply forward chaining for tactical analysis
		int forwardRulesFired = 0;
		for (Move move : possibleMoves) {
			KieSession forwardSession = kieContainer.newKieSession("forwardSession");

			Board originalBoard = currentPlayer.getBoard();
			Board boardAfterMove = BoardUtils.copyBoard(originalBoard);
			BoardUtils.applyMoveToBoard(boardAfterMove, move);

			forwardSession.insert(move);
			forwardSession.insert(boardAfterMove);
			forwardSession.insert(gameState);
			forwardSession.insert(currentPlayer);

			forwardRulesFired += forwardSession.fireAllRules();
			forwardSession.dispose();
		}
		
		// 2. Apply backward chaining for strategic analysis
		KieSession backwardSession = kieContainer.newKieSession("combinedBackwardSession");
		Board currentBoard = currentPlayer.getBoard();
		backwardSession.insert(currentBoard);
		backwardSession.insert(gameState);
		backwardSession.insert(currentPlayer);
		
		for (Move move : possibleMoves) {
			backwardSession.insert(move);
		}

		int backwardRules = backwardSession.fireAllRules();
		backwardSession.dispose();
		
		// Sort and return best move
		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));
		
		System.out.println("AI Analysis: " + possibleMoves.size() + " moves, " + forwardRulesFired + " forward rules, " + backwardRules + " backward rules");
		
		if (!possibleMoves.isEmpty()) {
			Move bestMove = possibleMoves.get(0);
			System.out.println("Best move: " + bestMove.toString() + " (Score: " + bestMove.getScore() + ")");
			return new MoveDTO(bestMove, bestMove.toString());
		}
		
		return null;
	}

}
