package com.gus.match3;

import java.util.Stack;

import com.gus.match3.controller.Match3Solver;
import com.gus.match3.model.Stage;
import com.gus.match3.model.Swap;

public class MainMatch3 {
	private static final char[][] REAL_TEST= {
			{' ',' ','a',' ',' ',' ',' '},
			{' ',' ','b',' ',' ',' ',' '},
			{' ',' ','c',' ',' ',' ',' '},
			{' ',' ','d',' ',' ',' ',' '},
			{' ','a','d',' ',' ',' ',' '},
			{' ','a','e',' ',' ',' ',' '},
			{' ','c','f','g','b',' ',' '},
			{' ','b','f','g','c','e','e'},
			{'c','f','g','h','d','h','h'}
	};
	
	public static void main(String[] args) {
		Stage solution=new Match3Solver().solveStage(REAL_TEST);
		if(solution==null) {
			System.out.println("No Solution");
		}else {
			Stack<Stage>solutionPath=new Stack<>();
			while(solution!=null) {
				solutionPath.push(solution);
				solution=solution.getStageFrom();
			}
			do {
				solution=solutionPath.pop();
				Swap swap=solution.getSwapFrom();
				if(swap!=null) {
					System.out.println(swap);
				}
				solution.paint();
			}while(solutionPath.size()>0);
		}
	}
}
