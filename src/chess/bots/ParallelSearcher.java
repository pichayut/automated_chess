package chess.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	
	private static ForkJoinPool POOL = new ForkJoinPool();
	private static final int DIVIDE_CUTOFF = 2;
	
	@SuppressWarnings("serial")
	static class GetBestMoveTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
		int divideCutoff;
		int cutoff;
		int depth;
		int l, r;
		List<M> moves;
		B board;
		Evaluator<B> e;
		boolean hasToSwitch;
		
		public GetBestMoveTask(List<M> moves, B board, int l, int r, Evaluator<B> e, int depth, int cutoff, int divideCutoff, boolean  hasToSwitch) {
			this.moves = moves;
			this.board = board.copy();
			this.cutoff = cutoff;
			this.divideCutoff = divideCutoff;
			this.depth = depth;
			this.hasToSwitch = hasToSwitch;
			this.l = l;
			this.r = r;
			this.e = e;
		}
		
		public int size() {
			return r - l;
		}
		
		protected BestMove<M> compute() {
			if(this.size() <= this.divideCutoff) {
				if(depth <= cutoff) {
		    		return SimpleSearcher.minimax(e, board, depth);
				}
				List<GetBestMoveTask<M, B>> taskList = new ArrayList<GetBestMoveTask<M, B>>();
				for(int i = l; i < r; i++) {
					board.applyMove(moves.get(i));
					List<M> lst = board.generateMoves();
					
					if(lst.isEmpty()) {
			    		if(board.inCheck()) {
			    			return new BestMove<M>(-e.mate() - depth);
			    		} else {
			    			return new BestMove<M>(-e.stalemate());
			    		}
			    	}
					
					taskList.add(new GetBestMoveTask<M, B>(lst, board, 0, lst.size(), e, depth - 1, cutoff, divideCutoff, true));
					board.undoMove();
				}
				int bestValue = Integer.MIN_VALUE;
				for(int i = 1; i < taskList.size(); i++) {
		    		taskList.get(i).fork();
		    	}
				BestMove<M> bestMove = taskList.get(0).compute();
				bestValue = -bestMove.value;
				M best = moves.get(0);
				for(int i = 1; i < taskList.size(); i++) {
					BestMove<M> newBestMove = taskList.get(i).join();
		    		if(-newBestMove.value > bestValue) {
		    			bestValue = -newBestMove.value;
		    			best = moves.get(i);
		    		}
		    	}
		    	return new BestMove<M>(best, bestValue);
			}
			
			GetBestMoveTask<M, B> leftTask = new GetBestMoveTask<M, B>(moves, board, l, l + (r - l) / 2, e, depth, cutoff, divideCutoff, false);
			GetBestMoveTask<M, B> rightTask = new GetBestMoveTask<M, B>(moves, board, l + (r - l) / 2, r, e, depth, cutoff, divideCutoff, false);
			
			leftTask.fork();
			BestMove<M> answer = rightTask.compute();
			BestMove<M> leftAnswer = leftTask.join();
			if(leftAnswer.value > answer.value) {
				answer.move = leftAnswer.move;
				answer.value = leftAnswer.value;
			}
			return answer;
		}
	}
	
    public M getBestMove(B board, int myTime, int opTime) {
    	List<M> moves = new ArrayList<M>();
    	for(M move : board.generateMoves()) {
    		moves.add(move);
    	}
    	
    	if(moves.isEmpty()) {
    		if(board.inCheck()) {
    			return new BestMove<M>(-this.evaluator.mate() - ply).move;
    		} else {
    			return new BestMove<M>(-this.evaluator.stalemate()).move; 
    		}
    	}
    	
    	BestMove<M> bestMove = POOL.invoke(new GetBestMoveTask<M, B>(moves, board, 0, moves.size(), this.evaluator, ply, cutoff, DIVIDE_CUTOFF, false));
    	return bestMove.move;
    }
}