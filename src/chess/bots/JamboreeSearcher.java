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
	
	private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final double PERCENTAGE_SEQUENTIAL = 0.5; //0.4375;
    private static final int DIVIDE_CUTOFF = 4;
    
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
    	return POOL.invoke(new JamboreeSubTask<M, B>(this.evaluator, board, null, ply, null, 0, -1, -this.evaluator.infty(), this.evaluator.infty(), cutoff, DIVIDE_CUTOFF, false)).move;
    }
    
    static class JamboreeSubTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	List<M> moves;
    	M move;
    	B board;
    	Evaluator<B> e;
    	int depth, alpha, beta;
    	int cutoff, divideCutoff;
    	int l, r;
    	boolean alreadyHaveGoodAlphaBeta;
    	
    	public JamboreeSubTask(Evaluator<B> e, B board, M move, int depth, List<M> moves, int l, int r, int alpha, int beta, int cutoff, int divideCutoff, boolean AlreadyHaveGoodAlphaBeta) {
    		this.e = e;
    		this.move = move;
    		this.board = board;
    		this.depth = depth;
    		this.moves = moves;
    		this.alpha = alpha;
    		this.beta = beta;
    		this.cutoff = cutoff;
    		this.l = l;
    		this.r = r;
    		this.divideCutoff = divideCutoff;
    		this.alreadyHaveGoodAlphaBeta = AlreadyHaveGoodAlphaBeta;
    	}
    	
    	public int size() {
			return this.r - this.l;
		}
    	
		protected BestMove<M> compute() {
			M bestMove = null;
			if(!alreadyHaveGoodAlphaBeta) {
				if(move != null && moves == null) { // If apply move, have to copy board
					this.board = this.board.copy();
				}
				if(this.move != null) {
					this.board.applyMove(this.move);
					this.move = null;
				}

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
		    		int value = new JamboreeSubTask<M, B>(this.e, this.board, null, this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false).compute().negate().value;
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
			
			if(ed - st > this.divideCutoff) {
				JamboreeSubTask<M, B> leftTask = new JamboreeSubTask<M, B>(e, board, this.move, depth, moves, st, st + (ed - st) / 2, alpha, beta, cutoff, divideCutoff, true);
				JamboreeSubTask<M, B> rightTask = new JamboreeSubTask<M, B>(e, board, this.move, depth, moves, st + (ed - st) / 2, ed, alpha, beta, cutoff, divideCutoff, true);
				
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
				//this.board = this.board.copy();
				if(this.move != null) {
					this.board.applyMove(this.move);
					this.move = null;
				}
				
				if(this.moves == null) {
		    		this.moves = this.board.generateMoves();
		    		this.r = this.moves.size();
		    	}
				List<JamboreeSubTask<M, B>> taskList = new ArrayList<JamboreeSubTask<M, B>>();
		    	for (int i = st; i < ed - 1; i++) {
		    		//board.applyMove(this.moves.get(i));
		    		taskList.add(new JamboreeSubTask<M, B>(this.e, this.board, this.moves.get(i), this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false));
		    		taskList.get(i - st).fork();
		    		//board.undoMove();
		    	}
		    	
		    	// do one work yourself
		    	//board.applyMove(this.moves.get(ed - 1));
		    	JamboreeSubTask<M, B> current = new JamboreeSubTask<M, B>(this.e, this.board, this.moves.get(ed - 1), this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false);
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