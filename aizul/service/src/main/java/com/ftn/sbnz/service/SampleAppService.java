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

	public Player getClassifiedItem(Player i) {
		KieSession kieSession = kieContainer.newKieSession("playerSession");
		kieSession.insert(i);
		kieSession.fireAllRules();
		kieSession.dispose();
		return i;
	}

	public Move rule_1() throws Exception{
		Board mockBoard = JsonLoader.loadBoardFromClasspath("mockboard.json");
		Move move = JsonLoader.loadMoveFromClasspath("moves.json");
		KieSession kieSession = kieContainer.newKieSession("ruleSession1");
		kieSession.insert(move);
		kieSession.insert(mockBoard);
		kieSession.fireAllRules();
		kieSession.dispose();

		return move;
	}

	public List<Move> rule_2() throws Exception{
		GameState mockGameState = JsonLoader.loadGameStateFromClasspath("boardstate.json");
		Player currentPlayer = mockGameState.getPlayers().get(0);
		
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);
		
		for (Move move : possibleMoves) {
			KieSession kieSession = kieContainer.newKieSession("ruleSession2");

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

	public List<Move> backwardChainTest() throws Exception{
		GameState mockGameState = JsonLoader.loadGameStateFromClasspath("backward_chain_test.json");
		Player currentPlayer = mockGameState.getPlayers().get(0);

		System.out.println("Backward chaining test for player: " + currentPlayer.getId());
		Board testBoard = currentPlayer.getBoard();
		for (int i = 0; i < testBoard.getWall().length; i++) {
			int filled = 0;
			for (int j = 0; j < testBoard.getWall()[i].length; j++) {
				if (testBoard.getWall()[i][j] != null) filled++;
			}
			System.out.println("Row " + i + ": " + filled + "/5 filled");
		}
		
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);

		KieSession backwardSession = kieContainer.newKieSession("backwardSession");
		backwardSession.insert(testBoard);
		backwardSession.insert(mockGameState);
		backwardSession.insert(currentPlayer);
		
		for (Move move : possibleMoves) {
			backwardSession.insert(move);
		}
		
		int totalRules = backwardSession.fireAllRules();
		System.out.println("Total rules fired: " + totalRules);

		org.kie.api.runtime.rule.QueryResults recursiveMoves = 
			backwardSession.getQueryResults("howToCompleteRowsAtDepth");

		System.out.println("\nSolutions found: " + recursiveMoves.size());
		for (org.kie.api.runtime.rule.QueryResultsRow solution : recursiveMoves) {
			Move move = (Move) solution.get("$move");
			Integer depth = (Integer) solution.get("$depth");
			System.out.println("Depth " + depth + ": Row " + move.getTargetRow() + ": " + move.toString() + " move");
		}

		backwardSession.dispose();
		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));

		System.out.println("\n=== FINAL RESULTS ===");
		for (Move move : possibleMoves) {
			System.out.println("[" + move.toString() + "] - Score: " + move.getScore());
		}

		return possibleMoves;
	}
}
