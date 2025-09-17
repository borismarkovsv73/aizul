package com.ftn.sbnz.model.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;

public class JsonLoader {
    public static GameState loadGameState(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), GameState.class);
    }

    public static Move loadMove(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Move.class);
    }
    public static Board loadBoard(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Board.class);
    }
}