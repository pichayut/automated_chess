package traffic;

import java.util.List;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import chess.bots.BestMove;

public class AlphaBetaSearcher<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        return alphaBeta(this.evaluator, board, ply, null, -this.evaluator.infty(), this.evaluator.infty()).move;
    }

    public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphaBeta(Evaluator<B> evaluator, B board, int depth, List<M> moves, int alpha, int beta) {
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
    	
    	M bestMove = null;
    	for(M move : moves) {
    		board.applyMove(move);
    		int value = -alphaBeta(evaluator, board, depth - 1, null, -beta, -alpha).value;
    		board.undoMove();
    		if (value > alpha) {
    			alpha = value;
    			bestMove = move;
    		}
    		if (alpha >= beta) {
    			return new BestMove<M>(bestMove, alpha);
    		}
    	}
    	return new BestMove<M>(bestMove, alpha);
    }
}