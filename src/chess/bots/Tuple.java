package chess.bots;

import cse332.chess.interfaces.Move;

public class Tuple<M extends Move<M>> implements Comparable<Tuple<M>> {
	
	private M move;
	private int val;
	
	public Tuple(M move, int val) {
		this.move = move;
		this.val = val;
	}
	
	public void increment(int d) {
		this.val += d;
	}
	
	public M getMove() {
		return this.move;
	}
	
	public int getVal() {
		return this.val;
	}
	
	public void resetVal() {
		this.val = 0;
	}

	@Override
	public int compareTo(Tuple<M> obj) {
		if(Integer.valueOf(this.val).compareTo(Integer.valueOf(obj.val)) != 0) {
			return -Integer.valueOf(this.val).compareTo(Integer.valueOf(obj.val));
		} else if(Boolean.compare(obj.move.isCapture(), this.move.isCapture()) != 0){
			return Boolean.compare(obj.move.isCapture(), this.move.isCapture());
		} else {
			return Boolean.compare(obj.move.isPromotion(), this.move.isPromotion());
		}
	}
}
