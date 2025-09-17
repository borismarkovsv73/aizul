package com.ftn.sbnz.model.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import com.ftn.sbnz.model.models.GameState;

public class JsonLoader {
    public static GameState loadGameState(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), GameState.class);
    }
}