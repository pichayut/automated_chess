package chess.bots;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import chess.board.ArrayMove;
import chess.game.SimpleTimer;

public class DeepeningJamboree<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	
	private static ForkJoinPool POOL = new ForkJoinPool();
    private static final double PERCENTAGE_SEQUENTIAL = 0.5; //0.4375;
    private static final int DIVIDE_CUTOFF = 2;
    private static final double FACTION = 1; //0.65;
    private static SimpleTimer timer;
    private static final int timeAllowPerMove = 10000;
    private static final boolean limitTime = true;
    private static Random rt = new Random();
    
    private static Map<String, List<Tuple<ArrayMove>>> keepMove;
    
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
    	timer = new SimpleTimer(0, 0);
    	keepMove = new ConcurrentHashMap<String, List<Tuple<ArrayMove>>>();
    	timer.start(myTime, opTime);
    	BestMove<M> bestMove = new DeepeningSubTask<M, B>(this.evaluator, board, null, 1, null, 0, -1, -this.evaluator.infty(), this.evaluator.infty(), cutoff, DIVIDE_CUTOFF, false, false, false).compute();
    	int depth = 2;
    	while(depth <= ply) {
    		sortAll();
    		timer.start(myTime, opTime);
    		bestMove = new DeepeningSubTask<M, B>(this.evaluator, board, null, depth, null, 0, -1, -this.evaluator.infty(), this.evaluator.infty(), cutoff, DIVIDE_CUTOFF, false, false, false).compute();
    		depth++;
    	}
    	return bestMove.move;
    }
    
    private void sortAll() {
		for(List<Tuple<ArrayMove>> lst : keepMove.values()) {
			Collections.sort(lst);
		}
	}

	public static <M extends Move<M>> int compareCapture(Tuple<M> m1, Tuple<M> m2) {
    	return Boolean.compare(m2.getMove().isCapture(), m1.getMove().isCapture());
    }
    
    /*private static int makeRandom() {
    	Random rt = new Random();
    	int r =  rt.nextInt(3);
    	if(r == 0) {
    		return -1;
    	} else if (r == 1){
    		return 1;
    	} else {
    		return 0;
    	}
	}*/

	public static class DeepeningSubTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	//List<M> moves;
    	List<Tuple<ArrayMove>> tupleMoves;
    	M move;
    	B board;
    	Evaluator<B> e;
    	int depth, alpha, beta;
    	int cutoff, divideCutoff;
    	int l, r;
    	boolean alreadyHaveGoodAlphaBeta, fromParallel, checkEquals;
    	
    	public DeepeningSubTask(Evaluator<B> e, B board, M move, int depth/*, List<M> moves,*/, List<Tuple<ArrayMove>> tupleMoves
    			, int l, int r, int alpha, int beta, int cutoff, int divideCutoff, boolean alreadyHaveGoodAlphaBeta
    			 	, boolean fromParallel, boolean checkEquals) {
    		this.e = e;
    		this.move = move;
    		this.tupleMoves = tupleMoves;
    		this.board = board;
    		this.depth = depth;
    		//this.moves = moves;
    		this.alpha = alpha;
    		this.beta = beta;
    		this.cutoff = cutoff;
    		this.l = l;
    		this.r = r;
    		this.divideCutoff = divideCutoff;
    		this.alreadyHaveGoodAlphaBeta = alreadyHaveGoodAlphaBeta;
    		this.fromParallel = fromParallel;
    		this.checkEquals = checkEquals;
    	}
    	
    	public int size() {
			return this.r - this.l;
		}
    	
		protected BestMove<M> compute() {
			// exceed time allowed per move
			if(limitTime && timer.stop() >= timeAllowPerMove) {
				return new BestMove<M>(-this.e.infty());
			}
			
			M bestMove = null;
			int indexBest = -1;
			if(!alreadyHaveGoodAlphaBeta) {			// do sequential part
				this.board = this.board.copy();
				if(this.move != null) {
					this.board.applyMove(this.move);
					this.move = null;
				}

				if(this.tupleMoves == null) {
					List<M> tmpMoves;
					if(keepMove.containsKey(this.board.fen())) {
						this.tupleMoves = keepMove.get(this.board.fen());
						//Collections.sort(this.tupleMoves);
					} else {
						tmpMoves = this.board.generateMoves();
			    		this.tupleMoves = new ArrayList<Tuple<ArrayMove>>();
			    		for(int i = 0; i < tmpMoves.size(); i++) {
			    			this.tupleMoves.add(new Tuple<ArrayMove>((ArrayMove) tmpMoves.get(i), 0));
			    		}
			    		Collections.sort(this.tupleMoves, DeepeningJamboree::compareCapture);
			    		keepMove.put(this.board.fen(), this.tupleMoves);
					}
					this.r = this.tupleMoves.size();
		    	}
				
				if(this.depth <= this.cutoff || this.tupleMoves.size() == 0) {
					/*List<M> tmpMoves = new ArrayList<M>();
					for(int i = 0; i < this.tupleMoves.size(); i++) {
						tmpMoves.add((M) this.tupleMoves.get(i).getMove());
					}*/
					return DeepeningSequential.alphaBeta(keepMove, this.e, this.board, this.depth, this.tupleMoves, this.alpha, this.beta);
				}
		    	for (int i = l; i < l + (int) (PERCENTAGE_SEQUENTIAL * this.size()); i++) {
		    		M move = (M) this.tupleMoves.get(i).getMove();
		    		this.board.applyMove(move);
		    		int value = new DeepeningSubTask<M, B>(this.e, this.board, null, this.depth - 1
		    				, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false, false, ThreadLocalRandom.current().nextInt(2) == 1).compute().negate().value;
		    		this.board.undoMove();
		    		if (!checkEquals ? value > alpha : value >= alpha) {
		    			alpha = value;
		    			bestMove = move;
		    			indexBest = i;
		    		}
		    		if (alpha >= beta) {
		    			
		    			// add HH value
		    			if(indexBest != -1) {
		    				this.tupleMoves.get(indexBest).increment(1 << depth);
		    				//Collections.sort(this.tupleMoves);
		    			}
		    			
		    			return new BestMove<M>(bestMove, alpha, indexBest);
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
				DeepeningSubTask<M, B> leftTask = new DeepeningSubTask<M, B>(e, board, this.move, depth, tupleMoves, st, st + (ed - st) / 2, alpha, beta, cutoff, divideCutoff, true, true, false);
				DeepeningSubTask<M, B> rightTask = new DeepeningSubTask<M, B>(e, board, this.move, depth, tupleMoves, st + (ed - st) / 2, ed, alpha, beta, cutoff, divideCutoff, true, true, false);
				
				leftTask.fork();
				BestMove<M> answer = rightTask.compute();
				if(!checkEquals ? answer.value > alpha : answer.value >= alpha) {
					alpha = answer.value;
					bestMove = answer.move;
					indexBest = answer.indexBest;
				}
				BestMove<M> leftAnswer = leftTask.join();
				if(!checkEquals ? leftAnswer.value > alpha : leftAnswer.value >= alpha) {
					alpha = leftAnswer.value;
					bestMove = leftAnswer.move;
					indexBest = leftAnswer.indexBest;
				}
				if(!fromParallel) {
					// add HH value
	    			if(indexBest != -1) {
	    				this.tupleMoves.get(indexBest).increment(1 << depth);
	    				//Collections.sort(this.tupleMoves);
	    			}
				}
				return new BestMove<M>(bestMove, alpha, indexBest);
			} else {
				//this.board = this.board.copy();
				if(this.move != null) {
					this.board.applyMove(this.move);
					this.move = null;
				}
				
				if(this.tupleMoves == null) {
					List<M> tmpMoves;
					if(keepMove.containsKey(this.board.fen())) {
						this.tupleMoves = keepMove.get(this.board.fen());
						//Collections.sort(this.tupleMoves);
					} else {
						tmpMoves = this.board.generateMoves();
						this.tupleMoves = new ArrayList<Tuple<ArrayMove>>();
			    		for(int i = 0; i < tmpMoves.size(); i++) {
			    			this.tupleMoves.add(new Tuple<ArrayMove>((ArrayMove) tmpMoves.get(i), 0));
			    		}
			    		Collections.sort(this.tupleMoves, DeepeningJamboree::compareCapture);
			    		keepMove.put(this.board.fen(), this.tupleMoves);
					}
					this.r = this.tupleMoves.size();
		    	}
				
				List<DeepeningSubTask<M, B>> taskList = new ArrayList<DeepeningSubTask<M, B>>();
				double mid = st + FACTION * (ed - st) / 2;
		    	for (int i = st; i < ed - 1; i++) {
		    		//board.applyMove(this.moves.get(i));
		    		taskList.add(new DeepeningSubTask<M, B>(this.e, this.board, (M) this.tupleMoves.get(i).getMove(), (i < mid) ? this.depth - 1 : this.depth - 2, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false, false, ThreadLocalRandom.current().nextInt(2) == 1));
		    		taskList.get(i - st).fork();
		    		//board.undoMove();
		    	}
		    	
		    	// do one work yourself
		    	//board.applyMove(this.moves.get(ed - 1));
		    	DeepeningSubTask<M, B> current = new DeepeningSubTask<M, B>(this.e, this.board, (M) tupleMoves.get(ed - 1).getMove(), this.depth - 1, null, 0, -1, -this.beta, -this.alpha, this.cutoff, this.divideCutoff, false, false, ThreadLocalRandom.current().nextInt(2) == 1);
		    	int value = current.compute().negate().value;
		    	int r = ThreadLocalRandom.current().nextInt(2);
		    	if (!checkEquals ? value > alpha : value >= alpha) {
		    		alpha = value;
		    		bestMove = (M) this.tupleMoves.get(ed - 1).getMove();
		    		indexBest = ed - 1;
		    	}
		    	if (alpha >= beta) { 
		    		
		    		// add HH value
	    			if(indexBest != -1) {
	    				this.tupleMoves.get(indexBest).increment(1 << depth);
	    				//Collections.sort(this.tupleMoves);
	    			}
	    			
		    		return new BestMove<M>(bestMove, alpha, indexBest);
		    	}
		    	//---------------------
		    	
		    	for(int i = 0; i < taskList.size(); i++) {
		    		value = taskList.get(i).join().negate().value;
		    		if (!checkEquals ? value > alpha : value >= alpha) {
			    		alpha = value;
			    		bestMove = (M) this.tupleMoves.get(i + st).getMove();
			    		indexBest = i + st;
			    	}
			    	if (alpha >= beta) { 
			    		
			    		// add HH value
		    			if(indexBest != -1) {
		    				this.tupleMoves.get(indexBest).increment(1 << depth);
		    				//Collections.sort(this.tupleMoves);
		    			}
			    		
			    		return new BestMove<M>(bestMove, alpha, indexBest);
			    	}
		    	}
		    	
		    	// add HH value
    			if(indexBest != -1) {
    				this.tupleMoves.get(indexBest).increment(1 << depth);
    				//Collections.sort(this.tupleMoves);
    			}
		    	
		    	return new BestMove<M>(bestMove, alpha, indexBest);
			}
		}
    }
}