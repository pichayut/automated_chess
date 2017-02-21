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
	private static final int DIVIDE_CUTOFF = 4;
	
	static class GetBestMoveTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
		int divideCutoff;
		int cutoff;
		int depth;
		int l, r;
		List<M> moves;
		B board;
		Evaluator<B> e;
		
		public GetBestMoveTask(List<M> moves, B board, int l, int r, Evaluator<B> e, int depth, int cutoff, int divideCutoff) {
			this.moves = moves;
			this.r = r;
			this.board = board;
			this.cutoff = cutoff;
			this.divideCutoff = divideCutoff;
			this.depth = depth;
			this.l = l;
			this.e = e;
		}
		
		public int size() {
			return r - l;
		}
		
		protected BestMove<M> compute() {
			if(this.size() <= this.divideCutoff) {
				if(moves == null) {
					this.moves = board.generateMoves();
					this.r = this.moves.size();
				} 
				this.board = this.board.copy();
				if(depth <= cutoff || this.size() == 0) {
		    		return SimpleSearcher.minimax(e, board, depth, moves);
				}
				List<GetBestMoveTask<M, B>> taskList = new ArrayList<GetBestMoveTask<M, B>>();
				for(int i = l; i < r - 1; i++) {
					board.applyMove(moves.get(i));
					taskList.add(new GetBestMoveTask<M, B>(null, board.copy(), 0, -1, e, depth - 1, cutoff, divideCutoff));
					taskList.get(i - l).fork();
					board.undoMove();
				}
				board.applyMove(moves.get(r - 1));
				taskList.add(new GetBestMoveTask<M, B>(null, board, 0, -1, e, depth - 1, cutoff, divideCutoff));
				int bestValue = taskList.get(this.size() - 1).compute().negate().value;
				M bestMove = moves.get(r - 1);
				for(int i = 0; i < taskList.size() - 1; i++) {
					int newValue = taskList.get(i).join().negate().value;
		    		if(newValue > bestValue) {
		    			bestValue = newValue;
		    			bestMove = moves.get(l + i);
		    		}
		    	}
		    	return new BestMove<M>(bestMove, bestValue);
			}
			
			GetBestMoveTask<M, B> leftTask = new GetBestMoveTask<M, B>(moves, board, l, l + (r - l) / 2, e, depth, cutoff, divideCutoff);
			GetBestMoveTask<M, B> rightTask = new GetBestMoveTask<M, B>(moves, board, l + (r - l) / 2, r, e, depth, cutoff, divideCutoff);
			
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
    	List<M> moves = board.generateMoves();
    	BestMove<M> bestMove = POOL.invoke(new GetBestMoveTask<M, B>(moves, board, 0, moves.size(), this.evaluator, ply, cutoff, DIVIDE_CUTOFF));
    	return bestMove.move;
    }
}