package chess.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class JamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	
	private static ForkJoinPool POOL = new ForkJoinPool();
    private static final double PERCENTAGE_SEQUENTIAL = 0.5;
    
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
    	BestMove<M> bestMove = POOL.invoke(new JamboreeSubTask<M, B>(this.evaluator, board, ply, null, -this.evaluator.infty(), this.evaluator.infty(), cutoff));
        return bestMove.move;
    }
    
    static class JamboreeSubTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	List<M> moves;
    	B board;
    	Evaluator<B> e;
    	int depth, alpha, beta;
    	int cutoff;
    	
    	public JamboreeSubTask(Evaluator<B> e, B board, int depth, List<M> moves, int alpha, int beta, int cutoff) {
    		this.e = e;
    		this.board = board;
    		this.depth = depth;
    		this.moves = moves;
    		this.alpha = alpha;
    		this.beta = beta;
    		this.cutoff = cutoff;
    	}
    	
		protected BestMove<M> compute() {
			if(this.moves == null) {
	    		this.moves = board.generateMoves();
	    	}
			
			if(this.depth <= this.cutoff || this.moves.size() == 0) {
				return AlphaBetaSearcher.alphaBeta(this.e, this.board, this.depth, this.moves, this.alpha, this.beta);
			}
			
			M bestMove = null;
	    	for (int i = 0; i < (int) (PERCENTAGE_SEQUENTIAL * moves.size()); i++) {
	    		M move = this.moves.get(i);
	    		this.board.applyMove(move);
	    		int value = new JamboreeSubTask<M, B>(this.e, this.board, this.depth - 1, null, -this.beta, -this.alpha, this.cutoff).compute().negate().value;
	    		this.board.undoMove();
	    		if (value > alpha) {
	    			alpha = value;
	    			bestMove = move;
	    		}
	    		if (alpha >= beta) {
	    			return new BestMove<M>(bestMove, alpha);
	    		}
	    	}
	    	
	    	List<JamboreeSubTask<M, B>> taskList = new ArrayList<JamboreeSubTask<M, B>>();
	    	for (int i = (int) (PERCENTAGE_SEQUENTIAL * moves.size()); i < moves.size(); i++) {
	    		board.applyMove(this.moves.get(i));
	    		taskList.add(new JamboreeSubTask<M, B>(this.e, this.board.copy(), this.depth - 1, null, -this.beta, -this.alpha, this.cutoff));
	    		if(i < moves.size() - 1) {
	    			taskList.get(i - (int) (PERCENTAGE_SEQUENTIAL * moves.size())).fork();
	    		}
	    		board.undoMove();
	    	}
	    	for(int i = taskList.size() - 1; i >= 0; i--) {
	    		int value;
	    		if(i == taskList.size() - 1) {
	    			value = taskList.get(i).compute().negate().value;
	    		} else {
	    			value = taskList.get(i).join().negate().value;
	    		}
	    		if (value > alpha) {
		    		alpha = value;
		    		bestMove = moves.get(i + (int) (PERCENTAGE_SEQUENTIAL * moves.size()));
		    	}
		    	if (alpha >= beta) {
		    		return new BestMove<M>(bestMove, alpha);
		    	}
	    	}
	    	return new BestMove<M>(bestMove, alpha);
		}
    }
}