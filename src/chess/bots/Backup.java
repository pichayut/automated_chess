package chess.bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class Backup<M extends Move<M>, B extends Board<M, B>> extends
	                AbstractSearcher<M, B> {
	        	
	private static ForkJoinPool POOL = new ForkJoinPool();
    private static final double PERCENTAGE_SEQUENTIAL = 0.5; //0.4375;
    private static final int DIVIDE_CUTOFF = 2; 
        
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
    	Map<B,List<M>> moveMap = new HashMap();
    	BestMove<M> bestMove = null;
    	for (int depth = 1; depth <= ply; depth++) {
    		bestMove = POOL.invoke(new JamboreeSubTask<M, B>(this.evaluator, board, depth, null, 0, -1, -this.evaluator.infty(), this.evaluator.infty(), cutoff, DIVIDE_CUTOFF, false, moveMap));
    	}
    	return bestMove.move;
    }
    
    public static <M extends Move<M>> int compare(M m1, M m2) {
    	return Boolean.compare(m2.isCapture(), m1.isCapture());
    }
    
    static class JamboreeSubTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	List<M> moves;
    	B board;
    	Evaluator<B> e;
    	int depth, alpha, beta;
    	int cutoff, divideCutoff;
    	int l, r;
    	boolean AlreadyHaveGoodAlphaBeta;
    	Map<B,List<M>> moveMap;
    	
    	public JamboreeSubTask(Evaluator<B> e, B board, int depth, List<M> moves, int l, int r, int alpha, int beta, int cutoff, int divideCutoff, boolean AlreadyHaveGoodAlphaBeta, Map<B,List<M>> moveMap) {
    		this.e = e;
    		this.board = board;
    		this.depth = depth;
    		this.moves = moves;
    		this.alpha = alpha;
    		this.beta = beta;
    		this.cutoff = cutoff;
    		this.l = l;
    		this.r = r;
    		this.divideCutoff = divideCutoff;
    		this.AlreadyHaveGoodAlphaBeta = AlreadyHaveGoodAlphaBeta;
    		this.moveMap = moveMap;
    	}
    	
    	public int size() {
			return this.r - this.l;
		}
    	
		protected BestMove<M> compute() {
			M bestMove = null;
			if(!this.AlreadyHaveGoodAlphaBeta) {
				this.board = this.board.copy();
				if(this.moveMap.containsKey(this.board)) {
					this.moves = this.moveMap.get(this.board);
					this.r = this.moves.size();
				}
				if(this.moves == null) {
		    		this.moves = this.board.generateMoves();
		    		this.r = this.moves.size();
		    	}
				if(!this.moveMap.containsKey(this.board)) {
					this.moveMap.put(this.board, this.moves);
				}
				
				if(this.depth <= this.cutoff || this.moves.size() == 0) {
					return AlphaBetaSearcher.alphaBeta(this.e, this.board, this.depth, this.moves, this.alpha, this.beta);
				}
				int bestIndex = -1;
				B bestBoard = null;
		    	for (int i = this.l; i < this.l + (int) (PERCENTAGE_SEQUENTIAL * this.size()); i++) {
		    		M move = this.moves.get(i);
		    		this.board.applyMove(move);
		    		int value = new JamboreeSubTask<M, B>(this.e, this.board, this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false, this.moveMap).compute().negate().value;
		      		if (value > this.alpha) {
		    			this.alpha = value;
		    			bestMove = move;
		    			bestIndex = i;
		    			bestBoard = this.board.copy();
		    		}
		      		this.board.undoMove();
		    		if (this.alpha >= this.beta) {
		    			if (bestIndex != -1 && bestBoard != null) {
		    				this.moveMap.get(bestBoard).remove(bestIndex);
		    				this.moveMap.get(bestBoard).add(0, bestMove);
		    			}
		    			return new BestMove<M>(bestMove, this.alpha);
		    		}
		    	}
			}

			int st, ed;
			if(!this.AlreadyHaveGoodAlphaBeta) {
				st = this.l + (int) (PERCENTAGE_SEQUENTIAL * this.size());
				ed = this.r;
			} else {
				st = this.l;
				ed = this.r;
			}
			
			if(this.size() > this.divideCutoff) {
				JamboreeSubTask<M, B> leftTask = new JamboreeSubTask<M, B>(this.e, this.board, this.depth, this.moves, st, st + (ed - st) / 2, this.alpha, this.beta, this.cutoff, this.divideCutoff, true, this.moveMap);
				JamboreeSubTask<M, B> rightTask = new JamboreeSubTask<M, B>(this.e, this.board, this.depth, this.moves, st + (ed - st) / 2, ed, this.alpha, this.beta, this.cutoff, this.divideCutoff, true, this.moveMap);
				
				leftTask.fork();
				BestMove<M> answer = rightTask.compute();
				if(answer.value > this.alpha) {
					this.alpha = answer.value;
					bestMove = answer.move;
				}
				BestMove<M> leftAnswer = leftTask.join();
				if(leftAnswer.value > this.alpha) {
					this.alpha = leftAnswer.value;
					bestMove = leftAnswer.move;
				}
				return new BestMove<M>(bestMove, this.alpha);
			} else {
				this.board = this.board.copy();
				if(this.moveMap.containsKey(this.board)) {
					this.moves = this.moveMap.get(this.board);
				}
				if(this.moves == null) {
		    		this.moves = this.board.generateMoves();
		    	}
				this.r = this.moves.size();
				if (!this.moveMap.containsKey(this.board)) {
					this.moveMap.put(this.board, this.moves);
				}
				List<JamboreeSubTask<M, B>> taskList = new ArrayList<JamboreeSubTask<M, B>>();
				double mid = st + (ed - st) / 2;
		    	for (int i = st; i < ed - 1; i++) {
		    		this.board.applyMove(this.moves.get(i));
		    		taskList.add(new JamboreeSubTask<M, B>(this.e, this.board, (i < mid) ? this.depth - 1 : this.depth - 2, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false, this.moveMap));
		    		taskList.get(i - st).fork();
		    		this.board.undoMove();
		    	}
		    	
		    	int bestIndex = -1;
				B bestBoard = null;
		    	// do one work yourself
		    	this.board.applyMove(this.moves.get(ed - 1));
		    	JamboreeSubTask<M, B> current = new JamboreeSubTask<M, B>(this.e, this.board, this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false, this.moveMap);
		    	int value = current.compute().negate().value;
		    	if (value > this.alpha) {
		    		this.alpha = value;
		    		bestMove = this.moves.get(ed - 1);
		    		bestIndex = ed - 1;
		    		bestBoard = this.board.copy();
		    	}
		    	if (this.alpha >= this.beta) { 
		    		return new BestMove<M>(bestMove, this.alpha);
		    	}
		    	//---------------------
		    	
		    	for(int i = 0; i < taskList.size(); i++) {
		    		value = taskList.get(i).join().negate().value;
		    		if (value > this.alpha) {
			    		this.alpha = value;
			    		bestMove = this.moves.get(i + st);
			    		bestIndex = i + st;
			    		//bestBoard = 
			    	}
			    	if (this.alpha >= this.beta) { 
			    		return new BestMove<M>(bestMove, this.alpha);
			    	}
		    	}
		    	return new BestMove<M>(bestMove, this.alpha);
			}
		}
    }
}
