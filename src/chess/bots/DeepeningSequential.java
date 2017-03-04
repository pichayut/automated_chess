

package chess.bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import chess.board.ArrayMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class DeepeningSequential<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        return alphaBeta(new ConcurrentHashMap<String, List<Tuple<ArrayMove>>>(), this.evaluator, board, ply, null, -this.evaluator.infty(), this.evaluator.infty()).move;
    }
    
    public static <M extends Move<M>> int compareCapture(M m1, M m2) {
    	return Boolean.compare(m2.isCapture(), m1.isCapture());
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphaBeta(Map<String, List<Tuple<ArrayMove>>> keepMove, Evaluator<B> evaluator, B board, int depth, List<Tuple<ArrayMove>> tupleMoves, int alpha, int beta) {
    	if(depth == 0) {
    		return new BestMove<M>(evaluator.eval(board));
    	}
    	
    	if(tupleMoves == null) {
			List<M> tmpMoves;
			if(keepMove.containsKey(board.fen())) {
				tupleMoves = keepMove.get(board.fen());
				//Collections.sort(this.tupleMoves);
			} else {
				tmpMoves = board.generateMoves();
	    		tupleMoves = new ArrayList<Tuple<ArrayMove>>();
	    		for(int i = 0; i < tmpMoves.size(); i++) {
	    			tupleMoves.add(new Tuple<ArrayMove>((ArrayMove) tmpMoves.get(i), 0));
	    		}
	    		Collections.sort(tupleMoves, DeepeningJamboree::compareCapture);
	    		keepMove.put(board.fen(), tupleMoves);
			}
    	}
    	
    	if(tupleMoves.isEmpty()) {
    		if(board.inCheck()) {
    			return new BestMove<M>(-evaluator.mate() - depth);
    		} else {
    			return new BestMove<M>(-evaluator.stalemate());
    		}
    	}
    	
    	M bestMove = null;
    	for(Tuple tuple : tupleMoves) {
    		M move = (M) tuple.getMove();
    		board.applyMove(move);
    		int value = -alphaBeta(keepMove, evaluator, board, depth - 1, null, -beta, -alpha).value;
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