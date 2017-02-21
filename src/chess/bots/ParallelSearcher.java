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
	private static final int DIVIDE_CUTOFF = 64;
	
	@SuppressWarnings("serial")
	static class GetBestMoveTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
		int cutoff;
		int depth;
		int l, r;
		List<M> moves;
		B board;
		Evaluator<B> e;
		
		public GetBestMoveTask(List<M> moves, B board, int l, int r, Evaluator<B> e, int depth, int cutoff) {
			this.moves = moves;
			this.board = board;
			this.cutoff = cutoff;
			this.depth = depth;
			this.l = l;
			this.r = r;
			this.e = e;
		}
		
		public int size() {
			return r - l;
		}
		
		protected BestMove<M> compute() {
			if(this.size() <= DIVIDE_CUTOFF) {
				if(depth <= cutoff) {
		    		return SimpleSearcher.minimax(e, board, depth);
				}
				List<GetBestMoveTask<M, B>> taskList = new ArrayList<GetBestMoveTask<M, B>>();
				for(int i = l; i < r; i++) {
					board.applyMove(moves.get(i));
					List<M> lst = board.generateMoves();	
					if(i < r - 1) {
						B copyBoard = board.copy();
						taskList.add(new GetBestMoveTask<M, B>(lst, copyBoard, 0, lst.size(), e, depth - 1, cutoff));
						board.undoMove();
					} else {
						taskList.add(new GetBestMoveTask<M, B>(lst, board, 0, lst.size(), e, depth - 1, cutoff));
					}
				}
				for(int i = 0; i < taskList.size() - 1; i++) {
		    		taskList.get(i).fork();
		    	}
				int bestValue = taskList.get(taskList.size() - 1).compute().negate().value;
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
			
			B copyBoard = board.copy();
			GetBestMoveTask<M, B> leftTask = new GetBestMoveTask<M, B>(moves, copyBoard, l, l + (r - l) / 2, e, depth, cutoff);
			GetBestMoveTask<M, B> rightTask = new GetBestMoveTask<M, B>(moves, board, l + (r - l) / 2, r, e, depth, cutoff);
			
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
    	BestMove<M> bestMove = POOL.invoke(new GetBestMoveTask<M, B>(moves, board, 0, moves.size(), this.evaluator, ply, cutoff));
    	return bestMove.move;
    }
}