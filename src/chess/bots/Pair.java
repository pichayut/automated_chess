package chess.bots;

import chess.board.ArrayMove;

public class Pair {
	private BestMove<ArrayMove> bestMove;
	private int depth;

	public Pair(BestMove<ArrayMove> bestMove, int depth) {
		this.bestMove = bestMove;
		this.depth = depth;
	}
	
	public BestMove<ArrayMove> getBestMove() {
		return bestMove;
	}
	
	public int getDepth() {
		return depth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pair)) return false;
		Pair o = (Pair) obj;
		return this.bestMove.move.equals(o.bestMove.move) && this.bestMove.value == o.bestMove.value && this.depth == o.depth;
	}
}
