package com.ftn.sbnz.service;

import org.apache.tomcat.util.digester.Rules;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.ScoreCardConfiguration.SCORECARD_INPUT_TYPE;
import org.mvel2.asm.ModuleVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ftn.sbnz.model.utils.JsonLoader;
import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.utils.JsonLoader;


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

		//This is an example of a move and a mockBoard where that move has been fired
		Board mockBoard = JsonLoader.loadBoard("service/src/main/resources/mockboard.json");
		Move move = JsonLoader.loadMove("service/src/main/resources/moves.json");
		KieSession kieSession = kieContainer.newKieSession("ruleSession1");
		kieSession.insert(move);
		kieSession.insert(mockBoard);
		kieSession.fireAllRules();
		kieSession.dispose();

		return move;
	}
}
