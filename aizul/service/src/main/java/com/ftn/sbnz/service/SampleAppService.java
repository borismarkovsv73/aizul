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
		// Load a game state specifically designed to test backward chaining
		// This file has rows with 4/5 and 3/5 tiles filled - perfect for backward chaining!
		GameState mockGameState = JsonLoader.loadGameStateFromClasspath("backward_chain_test.json");
		Player currentPlayer = mockGameState.getPlayers().get(0);
		
		System.out.println("=== BACKWARD CHAINING TEST SETUP ===");
		Board testBoard = currentPlayer.getBoard();
		System.out.println("Row 0 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[0]));
		System.out.println("Row 1 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[1]));
		System.out.println("Row 2 (4/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[2]));
		System.out.println("Row 3 (4/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[3]));
		System.out.println("Row 4 (0/5 filled): " + java.util.Arrays.toString(testBoard.getWall()[4]));
		
		System.out.println("\nPattern Lines (preparation rows):");
		for (int i = 0; i < testBoard.getRows().size(); i++) {
			System.out.println("Pattern line " + (i+1) + ": " + testBoard.getRows().get(i));
		}
		
		List<Move> possibleMoves = MoveGenerator.generateAllValidMoves(mockGameState, currentPlayer);

		// PRAVI BACKWARD CHAINING - početak sa ciljem
		KieSession backwardSession = kieContainer.newKieSession("backwardSession");

		// 1. Prvo ubacujemo board stanje da identifikujemo ciljeve
		Board originalBoard = currentPlayer.getBoard();
		backwardSession.insert(originalBoard);
		
		// 2. Ubacujemo sve moguće poteze
		for (Move move : possibleMoves) {
			backwardSession.insert(move);
		}

		// 3. Pokretamo pravila za identifikaciju ciljeva
		int rulesStep1 = backwardSession.fireAllRules();
		log.info("Step 1 - Goal identification: Fired " + rulesStep1 + " rules");

		// 4. Sada pokretamo pravila ponovo da bi našli opravdanja za ciljeve
		int rulesStep2 = backwardSession.fireAllRules();
		log.info("Step 2 - Move justification: Fired " + rulesStep2 + " rules");

		// 5. I konačno apliciramo bonuse
		int rulesStep3 = backwardSession.fireAllRules();
		log.info("Step 3 - Bonus application: Fired " + rulesStep3 + " rules");

		backwardSession.dispose();

		// Sort moves by score (highest first)
		possibleMoves.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));

		System.out.println("=== BACKWARD CHAINING RESULTS ===");
		for (Move move : possibleMoves) {
			GameStatePrinter.printMove(move);
			System.out.println("Final Score: " + move.getScore());
			System.out.println("---");
		}

		return possibleMoves;
	}
}
