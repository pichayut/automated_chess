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
    	Testing_Writeup game = new Testing_Writeup();
    	
    	if (false) {
	    	PrintStream out = new PrintStream(new File("./experiment/boards.txt"));
	        game.play(out, STARTING_POSITION);
	        out.close();
    	}
                               
        Scanner in = new Scanner(new File("./experiment/boards.txt"));
        ArrayBoard board= null;
        int count = 0;
        while(in.hasNextLine()) {
        	game.play(System.out, in.nextLine());
        	count += ((JamboreeSearcher) whitePlayer).getCount();
        }
        System.out.println(count);
    }

    public Testing_Writeup() {
        setupWhitePlayer(new JamboreeSearcher<ArrayMove, ArrayBoard>(), 1, 3);
        setupBlackPlayer(new JamboreeSearcher<ArrayMove, ArrayBoard>(), 4, 4);
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
