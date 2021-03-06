# Project 3 (Chess) Write-Up #
--------

## Project Enjoyment ##
- How Was Your Partnership?
  <pre>"Fantastic," Ter. "Exceptional partnership and friendship <3," Jacob. It was a lot of fun working as a group for the whole quarter.</pre>
  
- What was your favorite part of the project?
  <pre>Watching the bots actually play each other. It was very interesting to see them figure out the best moves to take.</pre>

- What was your least favorite part of the project?
  <pre>The minimax portion since it was decently rudimentary and a little boring.</pre>

- How could the project be improved?
  <pre>After P2 was finished grading, gitlab was extremely slow and would just pend for hours due to the heavy load from P2 running,
  so it would have been nice to know about that before hand so that we could have acted accordingly.</pre>

- Did you enjoy the project?
  <pre>Yes, we did.</pre> 
    
-----

## The Chess Server ##
- When you faced Clamps, what did the code you used do?  Was it just your jamboree?  Did you do something fancier?
  <pre>It was an implementation of JamboreeSearcher with move ordering due to the heuristic methodology. For example,
  we combined the history heuristic with the capturing heuristic. Also, we implemented iterative deepening to help
  speed things up and to find the history for the heuristics. The name of the bot is DeepeningJamboree.</pre>

- Did you enjoy watching your bot play on the server?  Is your bot better at chess than you are?
  <pre>Yes, it was very neat to see it actually play out and work. The bot is much better at chess than we are since you know, it's a computer
  and can think of many, many, many moves ahead while we are just mortal humans.</pre>

- Did your bot compete with anyone else in the class?  Did you win?
  <pre>No.</pre>

- Did you do any Above and Beyond?  Describe exactly what you implemented.
  <pre>Yes, we have fought against flexo and have won some games against him. We have also been able to fight against bender
  and win games against him as well.</pre>

## Experiments ##

### Chess Game ###

#### Hypotheses ####
Suppose your bot goes 3-ply deep.  How many game tree nodes do you think
it explores (we're looking for an order of magnitude) if:
 - ...you're using minimax?
    <pre>The max moves for each piece are Pawn:2, Rook:14, Knight:8, Bishop:14, King:8, and Queen:28, so assuming that each piece can do the
    max number of moves than that is 124 moves total. We then go 3-ply deep so that is 124 moves for each depth so that is approximately
    2 million game tree nodes explored.</pre>
 - ...you're using alphabeta?
    <pre>We suppose that alphabeta explores about half of the nodes so we think it will explore around 1 million game tree nodes.</pre>

#### Results ####
Run an experiment to determine the actual answers for the above.  To run
the experiment, do the following:
1. Run SimpleSearcher against AlphaBetaSearcher and capture the board
   states (fens) during the game.  To do this, you'll want to use code
   similar to the code in the testing folder.
2. Now that you have a list of fens, you can run each bot on each of them
   sequentially.  You'll want to slightly edit your algorithm to record the
   number of nodes you visit along the way.
3. Run the same experiment for 1, 2, 3, 4, and 5 ply. And with all four
   implementations (use ply/2 for the cut-off for the parallel
   implementations).  Make a pretty graph of your results (link to it from
   here) and fill in the table here as well:

<pre>For this table, we used a cut-off of 3 for the two non-parallel algorithms and the recommended ply/2 for the parallel
algorithms. For the board, we only used the starting board for a single game which is why our hypothesis was so far off since
the number of moves that are accessible in the beginning game is much less than the total of moves available. If we used our 
method from before than we would have gotten 20 moves total so 3-ply deep would have gotten 8,000 moves to check which is
very close to the Minimax algorithm's actual number of steps. For our method, we said that AlphaBeta would have gotten about half
of the moves that Minimax got, so we would have guessed that there would have been 4,000 which is very far off of the actual 
value. This must mean that AlphaBeta prunes off much more than what we predicted.</pre>


|      Algorithm     | 1-ply | 2-ply | 3-ply | 4-ply | 5-ply |
| :----------------: |:-----:|:-----:|:-----:|:-----:|:-----:|
|       Minimax      |	21   |  421  |  9323 |206604 |5072213|
|  Parallel Minimax  |  55   |  410  |  9200 |192317 |4252484|
|      Alphabeta     |  42   |  60   |  594  | 2533  | 95475 |
|      Jamboree      |  50   |  90   |  588  | 2370  |100668 |

#### Conclusions ####
How close were your estimates to the actual values?  Did you find any
entry in the table surprising?  Based ONLY on this table, do you feel
like there is a substantial difference between the four algorithms?
<pre>Our values for Minimax were very close to our predicted value if we used the same method that we used for the hypothesis, but
only use the available moves for the starting board which is 20 moves so 3-ply deep gets 8,000 moves which is very close to the 9323
moves from our values. We very interested to see that the parallel algorithms weren't too far off from the non-parallel algorithms, but
this sort of makes since since after a certain time, the parallel algorithms call the non-parallel algorithms.

We believe that there is a substantial difference between the four algorithms since AlphaBeta and Jamboree looked at much less moves than
Minimax and Parallel Minimax which then allows it to run much faster. However, AlphaBeta and Jamboree were very similar to each other and
Minimax and Parallel Minimax were very similar to each other which makes since since these algorithms have the same setup, but all the 
parallel algorithms do is run the multiple moves at the same time.</pre>

### Optimizing Experiments ###
THE EXPERIMENTS IN THIS SECTION WILL TAKE A LONG TIME TO RUN. 
To make this better, you should use Google Compute Engine:
* Run multiple experiments at the same time, but **NOT ON THE SAME MACHINE**.
* Google Compute Engine lets you spin up as many instances as you want.

#### Generating A Sample Of Games ####
Because chess games are very different at the beginning, middle,
and end, you should choose the starting board, a board around the middle
of a game, and a board about 5 moves from the end of the game.  The exact boards
you choose don't matter (although, you shouldn't choose a board already in
checkmate), but they should be different.

#### Sequential Cut-Offs ####
Experimentally determine the best sequential cut-off for both of your
parallel searchers.  You should test this at depth 5.  If you want it
to go more quickly, now is a good time to figure out Google Compute
Engine.   Plot your results and discuss which cut-offs work the best on each of
your three boards.
<pre>To look at our results, go <a href="./src/experiment/P3_Sequential_Cutoff_100.pdf">here</a>. All of the tests were
run on the Google Compute Engine and we ran the test 50 different times. As a summary, we found that there was a local 
minimum for the time taken to play both the early, mid, and end game. The local minimum was at a cut-off of 2, 
so we found that to be the optimal cut-off for both Jamboree and Parallel Minimax.

Also, for this test, we only changed the white player and kept the black player at a depth of 4, cut-off
of 4, and the standard number of processors (we didn't adjust the value at all).</pre>

#### Number Of Processors ####
Now that you have found an optimal cut-off, you should find the optimal
number of processors. You MUST use Google Compute Engine for this
experiment. For the same three boards that you used in the previous 
experiment, at the same depth 5, using your optimal cut-offs, test your
algorithm on a varying number of processors.  You shouldn't need to test all 32
options; instead, do a binary search to find the best number. You can tell the 
ForkJoin framework to only use k processors by giving an argument when
constructing the pool, e.g.,
```java
ForkJoinPool POOL = new ForkJoinPool(k);
```
Plot your results and discuss which number of processors works the best on each
of the three boards.
<pre>To look at our results, go <a href="./src/experiment/P3_Processors_100.pdf">here</a>. All of the tests were
run on the Google Compute Engine and we ran the test 50 different times. As a summary, we found that there 
was a large correlation between number of processors and the time taken to play each of our boards.
For the early game for both Jamboree and Parallel Minimax, we found that 16 processors was optimal.
For the mid game for both Jamboree and Parallel Minimax, we found that 32 processors was optimal.
For the end game for both Jamboree and Parallel Minimax, we found that 16 processors was optimal.
Even though the early and end game could use the 32 processors, we found that it didn't 
drastically change the timing, so we kept it down to 16 processors.

Also, for this test, we only changed the white player and kept the black player at a depth of 4, cut-off
of 4, and the standard number of processors (we didn't adjust the value at all).</pre>

#### Comparing The Algorithms ####
Now that you have found an optimal cut-off and an optimal number of processors, 
you should compare the actual run times of your four implementations. You MUST
use Google Compute Engine for this experiment (Remember: when calculating
runtimes using *timing*, the machine matters).  At depth 5, using your optimal 
cut-offs and the optimal number of processors, time all four of your algorithms
for each of the three boards.

Plot your results and discuss anything surprising about your results here.
<pre>To see this chart in a plot, go <a href="./src/experiment/P3_Runtime.pdf">here</a>. All of the tests were
run on the Google Compute Engine. As a summary, we found that the Jamboree ran much faster than 
the other algorithms which is understandable since it takes the pruning from Alphabeta and 
then computes the results in parallel. For the optimal settings, we used a depth of 5, 
cut-off of 2, and 16 processors for the early and end game and 32 processors for the mid game.

Also, for this test, we only changed the white player and kept the black player at a depth of 4, cut-off
of 4, and the standard number of processors (we didn't adjust the value at all).</pre> 

|      Algorithm     | Early Game (16 processors) | Mid Game (32 processors) | End Game (16 processors) |
| :----------------: |:----------:|:--------:|:--------:|
|       Minimax      |   4874     |  26750   |    420   |
|  Parallel Minimax  |   1598     |   3087   |     60   |
|      Alphabeta     |    321     |   1157   |    125   |
|      Jamboree      |    242     |    822   |     59   |


### Beating Traffic ###
In the last part of the project, you made a very small modification to your bot
to solve a new problem.  We'd like you to think a bit more about the 
formalization of the traffic problem as a graph in this question.  
- To use Minimax to solve this problem, we had to represent it as a game. In
  particular, the "states" of the game were "stretches of road" and the valid
  moves were choices of other adjacent "stretches of road".  The traffic and
  distance were factored in using the evaluation function.  If you wanted to use
  Dijkstra's Algorithm to solve this problem instead of Minimax, how would you
  formulate it as a graph?
  <pre>Each of the nodes would be the location and the edges connecting the nodes would be the roads to 
  the different locations. The weights for each edge would be the traffic or distance from the evaluation
  function. Then, Dijkstra's Algorithm would find the minimum traffic or distance from a start node to every
  other node.</pre>

- These two algorithms DO NOT optimize for the same thing.  (If they did,
  Dijkstra's is always faster; so, there would be no reason to ever use
  Minimax.)  Describe the difference in what each of the algorithms is
  optimizing for.  When will they output different paths?
  <pre>Since we are allowed to have only one value representing edges, Dijkstra's can be used to find 
  the minimum of either traffic, distance, or time from the starting location to the destination. However,
  Minimax has an evaluator that "attempts" to find the "best" path from among many data. Evaluator can 
  return a single value as a weight in the graph by calculating some function that takes more than one arguments
  and return the value represented them. For example, if we want to find the shortest time that also have the
  fewest distance, we cannot do that in Dijkstra's because we cannot store more than one variable on each edge. 
  On the other hand, the evaluator of Minimax can "attempt" to return a value representing that the same shortest
  time who has fewer distance will be evaluated "greater". However, Minimax is slower than Dijkstra's algorithm.
  To sum up, Dijkstra's optimizes in terms of time complexity, but Minimax optimizes in terms of best path
  among of the data (more than one data).</pre>