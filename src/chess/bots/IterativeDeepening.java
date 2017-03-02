package chess.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class IterativeDeepening<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B>{
	
	private static ForkJoinPool POOL = new ForkJoinPool();
    private static final double PERCENTAGE_SEQUENTIAL = 0.67; // 0.4375
    private static final int DIVIDE_CUTOFF = 2;
    
	@Override
	public M getBestMove(B board, int myTime, int opTime) {
		int depth = 1;
		BestMove<M> bestMove = null;
		while (depth < ply) {
			bestMove = negaMax(this.evaluator, board, depth, null, -this.evaluator.infty(), this.evaluator.infty(), 1);
			depth++;
		}
		return bestMove.move;
		//return POOL.invoke(new IterativeSubTask<M,B>(this.evaluator, board, ply, null, 0, -1, -this.evaluator.infty(), this.evaluator.infty(), cutoff, DIVIDE_CUTOFF, false)).move;
	}
	
	static <M extends Move<M>, B extends Board<M, B>> BestMove<M> negaMax(Evaluator<B> evaluator, B board, int depth, List<M> moves, int alpha, int beta, int color) {
		int alphaOrig = alpha;
		if (moves == null) {
			moves = board.generateMoves();
		}
		 /*
		TTEntry trans = TranspositionTableLookup(board);
		if (trans != null && trans.depth >= depth) {
			if (trans.flag == EXACT) {
				return new BestMove<M>(trans.value);
			} else if (trans.flag == LOWERBOUND) {
				alpha = max(alpha, trans.value);
			} else if (trans.flag == UPPERBOUND) {
				beta = min(beta, trans.value);
			}
			
			if (alpha >= beta) {
				return new BestMove<M>(trans.value);
			}
		}
		*/
		if (depth == 0 || moves.isEmpty()) {
			return new BestMove<M>(color * evaluator.eval(board));
		}
		
		M bestMove = null;
		for (M move : moves) {
			board.applyMove(move);
    		int value = -negaMax(evaluator, board, depth - 1, null, -beta, -alpha, -color).value;
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
		/*
		trans.value = bestMove;
		if ()
		*/
	}
	
	static class IterativeSubTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
		List<M> moves;
    	B board;
    	Evaluator<B> e;
    	int depth, alpha, beta;
    	int cutoff, divideCutoff;
    	int l, r;
    	boolean alreadyHaveGoodAlphaBeta;
    	
    	public IterativeSubTask(Evaluator<B> e, B board, int depth, List<M> moves, int l, int r, int alpha, int beta, int cutoff, int divideCutoff, boolean alreadyHaveGoodAlphaBeta) {
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
    		this.alreadyHaveGoodAlphaBeta = alreadyHaveGoodAlphaBeta;
    	}
    	
    	public int size() {
			return this.r - this.l;
		}
    	
		protected BestMove<M> compute() {
			M bestMove = null;
			if(!alreadyHaveGoodAlphaBeta) {
				this.board = this.board.copy();
				if(this.moves == null) {
		    		this.moves = this.board.generateMoves();
		    		this.r = this.moves.size();
		    	}
				
				if(this.depth <= this.cutoff || this.moves.size() == 0) {
					return AlphaBetaSearcher.alphaBeta(this.e, this.board, this.depth, this.moves, this.alpha, this.beta);
				}
		    	for (int i = l; i < l + (int) (PERCENTAGE_SEQUENTIAL * this.size()); i++) {
		    		M move = this.moves.get(i);
		    		this.board.applyMove(move);
		    		int value = new IterativeSubTask<M, B>(this.e, this.board, this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false).compute().negate().value;
		    		this.board.undoMove();
		    		if (value > alpha) {
		    			alpha = value;
		    			bestMove = move;
		    		}
		    		if (alpha >= beta) {
		    			return new BestMove<M>(bestMove, alpha);
		    		}
		    	}
			}

			int st, ed;
			if(!alreadyHaveGoodAlphaBeta) {
				st = l + (int) (PERCENTAGE_SEQUENTIAL * this.size());
				ed = r;
			} else {
				st = l;
				ed = r;
			}
			
			if(this.size() > this.divideCutoff) {
				IterativeSubTask<M, B> leftTask = new IterativeSubTask<M, B>(e, board, depth, moves, st, st + (ed - st) / 2, alpha, beta, cutoff, divideCutoff, true);
				IterativeSubTask<M, B> rightTask = new IterativeSubTask<M, B>(e, board, depth, moves, st + (ed - st) / 2, ed, alpha, beta, cutoff, divideCutoff, true);
				
				leftTask.fork();
				BestMove<M> answer = rightTask.compute();
				if(answer.value > alpha) {
					alpha = answer.value;
					bestMove = answer.move;
				}
				BestMove<M> leftAnswer = leftTask.join();
				if(leftAnswer.value > alpha) {
					alpha = leftAnswer.value;
					bestMove = leftAnswer.move;
				}
				return new BestMove<M>(bestMove, alpha);
			} else {		
				this.board = this.board.copy();
				if(this.moves == null) {
		    		this.moves = this.board.generateMoves();
		    		this.r = this.moves.size();
		    	}
				List<IterativeSubTask<M, B>> taskList = new ArrayList<IterativeSubTask<M, B>>();
		    	for (int i = st; i < ed - 1; i++) {
		    		board.applyMove(this.moves.get(i));
		    		taskList.add(new IterativeSubTask<M, B>(this.e, this.board.copy(), this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false));
		    		taskList.get(i - st).fork();
		    		board.undoMove();
		    	}
		    	
		    	// do one work yourself
		    	board.applyMove(this.moves.get(ed - 1));
		    	IterativeSubTask<M, B> current = new IterativeSubTask<M, B>(this.e, this.board, this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false);
		    	int value = current.compute().negate().value;
		    	if (value > alpha) {
		    		alpha = value;
		    		bestMove = this.moves.get(ed - 1);
		    	}
		    	if (alpha >= beta) { 
		    		return new BestMove<M>(bestMove, alpha);
		    	}
		    	//---------------------
		    	
		    	for(int i = 0; i < taskList.size(); i++) {
		    		value = taskList.get(i).join().negate().value;
		    		if (value > alpha) {
			    		alpha = value;
			    		bestMove = moves.get(i + st);
			    	}
			    	if (alpha >= beta) { 
			    		return new BestMove<M>(bestMove, alpha);
			    	}
		    	}
		    	return new BestMove<M>(bestMove, alpha);
			}
		}
	}
}
