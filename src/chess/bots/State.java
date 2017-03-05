package chess.bots;

import chess.board.ArrayBoard;

public class State {
	
	private ArrayBoard board;
	private int depth;
	
	public State(ArrayBoard board, int depth) {
		this.board = board;
		this.depth = depth;
	}
	
	public ArrayBoard getBoard() {
		return this.board;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof State)) return false;
		State o = (State) obj;
		return this.board.fen().equals(o.board.fen()) && this.depth == o.depth;
	}
	
	@Override
	public int hashCode() {
		return this.board.fen().hashCode() * 31 + this.depth;
	}
}
