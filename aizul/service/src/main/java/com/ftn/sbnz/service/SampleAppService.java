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
		
		System.out.println("\nBackward chaining test with the following board state:");
		Board testBoard = currentPlayer.getBoard();
		System.out.println("Row 0 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[0]));
		System.out.println("Row 1 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[1]));
		System.out.println("Row 2 (4/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[2]));
		System.out.println("Row 3 (4/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[3]));
		System.out.println("Row 4 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[4]));
		
		System.out.println("\nPattern Lines (preparation rows):");
		for (int i = 0; i < testBoard.getRows().size(); i++) {
			System.out.println("Pattern line " + (i+1) + ": " + testBoard.getRows().get(i).toString());
		}
		
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);

		KieSession backwardSession = kieContainer.newKieSession("backwardSession");
		Board originalBoard = currentPlayer.getBoard();
		backwardSession.insert(originalBoard);
		backwardSession.insert(mockGameState);
		backwardSession.insert(currentPlayer);
		
		for (Move move : possibleMoves) {
			backwardSession.insert(move);
		}
		
		backwardSession.insert(originalBoard.getWall());
		
		// 2. Pokretamo sva pravila za backward chaining
		int totalRules = backwardSession.fireAllRules();
		log.info("BACKWARD CHAINING: Fired " + totalRules + " rules total");

		System.out.println("\nCompletable rows:");
		org.kie.api.runtime.rule.QueryResults completableRows = 
			backwardSession.getQueryResults("whichRowsCanBeCompleted");
		
		if (completableRows.size() > 0) {
			System.out.println("Found " + completableRows.size() + " rows that can be completed:");
			for (org.kie.api.runtime.rule.QueryResultsRow row : completableRows) {
				Integer targetRow = (Integer) row.get("$row");
				System.out.println("> Row " + targetRow + " can be completed");
			}
		} else {
			System.out.println("No rows can be completed.");
		}

		System.out.println("\nMoves that can complete rows:");
		org.kie.api.runtime.rule.QueryResults solutions = 
			backwardSession.getQueryResults("howToCompleteRows");
		
		if (solutions.size() > 0) {
			System.out.println("Found " + solutions.size() + " moves that can complete rows:");
			for (org.kie.api.runtime.rule.QueryResultsRow solution : solutions) {
				Move move = (Move) solution.get("$move");
				System.out.println("> Solution: " + move.toString() + " can complete row " + move.getTargetRow());
			}
		} else {
			System.out.println("No moves can complete any rows.");
		}

		backwardSession.dispose();
		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));

		System.out.println("All results:");
		for (Move move : possibleMoves) {
			GameStatePrinter.printMove(move);
			System.out.println("Final Score: " + move.getScore());
			System.out.println("-".repeat(60));
		}

		return possibleMoves;
	}

	public List<Move> backwardChainTest2() throws Exception{
		GameState mockGameState = JsonLoader.loadGameStateFromClasspath("backward_chain_test_col.json");
		Player currentPlayer = mockGameState.getPlayers().get(0);
		
		System.out.println("\nBackward chaining test with the following board state:");
		Board testBoard = currentPlayer.getBoard();
		System.out.println("Row 0 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[0]));
		System.out.println("Row 1 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[1]));
		System.out.println("Row 2 (4/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[2]));
		System.out.println("Row 3 (4/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[3]));
		System.out.println("Row 4 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[4]));
		
		System.out.println("\nPattern Lines (preparation rows):");
		for (int i = 0; i < testBoard.getRows().size(); i++) {
			System.out.println("Pattern line " + (i+1) + ": " + testBoard.getRows().get(i).toString());
		}
		
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);

		KieSession backwardSession = kieContainer.newKieSession("backwardSessionOne");
		Board originalBoard = currentPlayer.getBoard();
		backwardSession.insert(originalBoard);
		backwardSession.insert(mockGameState);
		backwardSession.insert(currentPlayer);
		
		for (Move move : possibleMoves) {
			backwardSession.insert(move);
		}
		
		backwardSession.insert(originalBoard.getWall());
		
		// 2. Pokretamo sva pravila za backward chaining
		int totalRules = backwardSession.fireAllRules();
		log.info("BACKWARD CHAINING: Fired " + totalRules + " rules total");

		System.out.println("\nCompletable columns:");
		org.kie.api.runtime.rule.QueryResults completableCols = 
			backwardSession.getQueryResults("whichColumnsCanBeCompleted");
		
		if (completableCols.size() > 0) {
			System.out.println("Found " + completableCols.size() + " columns that can be completed:");
			for (org.kie.api.runtime.rule.QueryResultsRow row : completableCols) {
				Integer targetCol = (Integer) row.get("$col");
				System.out.println("> Column " + targetCol + " can be completed");
			}
		} else {
			System.out.println("No columns can be completed.");
		}

		System.out.println("\nMoves that can complete columns:");
		org.kie.api.runtime.rule.QueryResults solutions = 
			backwardSession.getQueryResults("howToCompleteColumns");
		
		if (solutions.size() > 0) {
			System.out.println("Found " + solutions.size() + " moves that can complete intended row:");
			for (org.kie.api.runtime.rule.QueryResultsRow solution : solutions) {
				Move move = (Move) solution.get("$move");
				System.out.println("> Solution: " + move.toString() + " can complete row " + move.getTargetRow());
			}
		} else {
			System.out.println("No moves can complete any rows.");
		}

		backwardSession.dispose();
		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));

		System.out.println("All results:");
		for (Move move : possibleMoves) {
			GameStatePrinter.printMove(move);
			System.out.println("Final Score: " + move.getScore());
			System.out.println("-".repeat(60));
		}

		return possibleMoves;
	}
}
