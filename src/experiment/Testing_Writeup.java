package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;

public class Testing_Writeup {
	public static Searcher<ArrayMove, ArrayBoard> whitePlayer;
    public Searcher<ArrayMove, ArrayBoard> blackPlayer;
    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    private ArrayBoard board;
    
    public static void main(String[] args) throws FileNotFoundException {
    	Scanner in = null;
    	PrintStream output = new PrintStream(new File("./experiment/AlphaBetaSearcherRuntime.txt"));
    		Testing_Writeup game = new Testing_Writeup();
    		//((ParallelSearcher) whitePlayer).numberProcessor(32);
    		in = new Scanner(new File("./experiment/boards.txt"));
            while(in.hasNextLine()) {
            	long startTime = System.currentTimeMillis();
            	game.play(System.out, in.nextLine());
            	output.println((System.currentTimeMillis() - startTime));
            }
    	
    	/*
    	if (true) {
	    	PrintStream out = new PrintStream(new File("./experiment/boards.txt"));
	        game.play(out, STARTING_POSITION);
	        out.close();
    	}
    	*/
        
        in.close();
    }

    public Testing_Writeup() {
        setupWhitePlayer(new AlphaBetaSearcher<ArrayMove, ArrayBoard>(), 5, 2);
        setupBlackPlayer(new AlphaBetaSearcher<ArrayMove, ArrayBoard>(), 4, 4);
    }
    
    public void play(PrintStream out, String currentBoard) {
       this.board = ArrayBoard.FACTORY.create().init(currentBoard);
       Searcher<ArrayMove, ArrayBoard> currentPlayer = this.blackPlayer;
       
       int turn = 0;
       
       /* Note that this code does NOT check for stalemate... */
       //while (!board.inCheck() || board.generateMoves().size() > 0) {
           currentPlayer = currentPlayer.equals(this.whitePlayer) ? this.blackPlayer : this.whitePlayer;
           out.println(board.fen());
           this.board.applyMove(currentPlayer.getBestMove(board, 1000, 1000));
           turn++;
       //}
    }
    
    public Searcher<ArrayMove, ArrayBoard> setupPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        return searcher; 
    }
    public void setupWhitePlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.whitePlayer = setupPlayer(searcher, depth, cutoff);
    }
    public void setupBlackPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.blackPlayer = setupPlayer(searcher, depth, cutoff);
    }
}
