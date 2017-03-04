

import java.util.List;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        return minimax(this.evaluator, board, ply, null).move;
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth, List<M> moves) {
    	if(depth == 0) {
    		return new BestMove<M>(evaluator.eval(board));
    	}
    	
    	if(moves == null) {
    		moves = board.generateMoves();
    	}
    	
    	if(moves.isEmpty()) {
    		if(board.inCheck()) {
    			return new BestMove<M>(-evaluator.mate() - depth);
    		} else {
    			return new BestMove<M>(-evaluator.stalemate());
    		}
    	}
    	
    	int bestValue = Integer.MIN_VALUE;
    	M bestMove = null;
    	for(M move : moves) {
    		board.applyMove(move);
    		int value = -minimax(evaluator, board, depth - 1, null).value;
    		board.undoMove();
    		if(value > bestValue) {
    			bestValue = value;
    			bestMove = move;
    		}
    	}
    	return new BestMove<M>(bestMove, bestValue);
    }
}