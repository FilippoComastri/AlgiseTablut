package it.unibo.ai.didattica.competition.tablut.algise.euristica;

import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class AlgiseBlackHeuristic {
	
	private State state;
	private int initB; // pedine NERE iniziali
	private int initW; // pedine BIANCHE iniziali
	private List<String> camps;
	private List<String> escape;
	private List<String> nearsThrone;
	private List<String> guardsPos;
	private List<String> blackBarrier;
	private String throne;
	
	public AlgiseBlackHeuristic (State state) {
		this.state = state;
		this.initB = 16;
		this.initW = 9; // 8 + KING
		
		this.camps = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9",
				"e9", "f9", "e8");

		this.escape = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9", "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3",
				"i7", "i8");

		this.nearsThrone = Arrays.asList("e4", "e6", "d5", "f5");
		this.throne = "e5";

		this.blackBarrier = Arrays.asList("b3", "b7", "c2", "c8", "g2", "g8", "h3", "h7");

		this.guardsPos = Arrays.asList("a1", "a2", "b1", "h1", "i1", "i2", "i8", "i9", "h9", "b9", "a9", "a8");
	}

}
