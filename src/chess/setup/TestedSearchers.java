package chess.setup;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.LazySearcher;
import cse332.chess.interfaces.Searcher;

/**
 *  The Searchers that you put in this array will be tested on gitlab-ci
 *  when you commit.  Depending on how many you have, the testing may take
 *  a significant amount of time.  You should use this as a sanity check--but
 *  nothing more.
 */
public class TestedSearchers {
    @SuppressWarnings("unchecked")
    public static final Searcher<ArrayMove, ArrayBoard>[] TESTED_SEARCHERS =
            (Searcher<ArrayMove, ArrayBoard>[])new Searcher[] {
        new LazySearcher<>()
    };
}
