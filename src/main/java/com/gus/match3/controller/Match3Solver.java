package com.gus.match3.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gus.match3.model.Stage;
import com.gus.match3.model.Swap;

public class Match3Solver {
	public Stage solveStage(char[][]board) {
		Stage stage=new Stage(board);
		if(stage.isSolved()) {
			return stage;
		}else if(stage.isDeadEnd()) {
			return null;
		}else {
			Map<Stage, List<Swap>>stageSwapsMap=new HashMap<>();
			stageSwapsMap.put(stage, new ArrayList<>(stage.getPosibleSwaps()));
			return solveStage(stageSwapsMap);
		}
	}
	
	private Stage solveStage(Map<Stage, List<Swap>>stageSwapsMap) {
		while(stageSwapsMap.size()>0) {
			Map.Entry<Stage, List<Swap>> entry=stageSwapsMap.entrySet().iterator().next();
			Stage currentStage=entry.getKey();
			List<Swap>posibleSwaps=entry.getValue();
			stageSwapsMap.remove(currentStage);
			if(currentStage.isSolved()) {
				return currentStage;
			}else if(!currentStage.isDeadEnd()) {
				for(Swap posibleSwap:posibleSwaps) {
					Stage newStage=new Stage(currentStage, posibleSwap);
					if(!stageSwapsMap.containsKey(newStage)) {
						stageSwapsMap.put(newStage, new ArrayList<>(newStage.getPosibleSwaps()));
					}
				}
			}
		}
		return null;
	}
}
