package chess.bots;

public class Pair {
	private String str;
	private int depth;

	public Pair(String str, int depth) {
		this.str = str;
		this.depth = depth;
	}
	
	public String getBoardFen() {
		return str;
	}
	
	public int getDepth() {
		return depth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pair)) return false;
		Pair o = (Pair) obj;
		return this.str.equals(o.str) && this.depth == o.depth;
	}
}
