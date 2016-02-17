# Project 3 (Chess) Write-Up #
--------

## Project Enjoyment ##
- How Was Your Partnership?
  <pre>TODO</pre>
  
- What was your favorite part of the project?
  <pre>TODO</pre>

- What was your least favorite part of the project?
  <pre>TODO</pre>

- How could the project be improved?
  <pre>TODO</pre>

- Did you enjoy the project?
  <pre>TODO</pre>
    
-----

## The Chess Server ##
- When you faced Clamps, what did the code you used do?  Was it just your parallel alphabeta?  Did you do something fancier?
  <pre>TODO</pre>

- Did you enjoy watching your bot play on the server?  Is your bot better at chess than you are?
  <pre>TODO</pre>

- Did your bot compete with anyone else in the class?  Did you win?
  <pre>TODO</pre>

- Did you do any Above and Beyond?  Describe exactly what you implemented.
  <pre>TODO</pre>

## Experiments ##

### Chess Game ###

#### Hypotheses ####
Suppose your bot goes 3-ply deep.  How many game tree nodes do you think
it explores (we're looking for an order of magnitude) if:
 - ...you're using minimax?
    <pre>TODO</pre>
 - ...you're using alphabeta?
    <pre>TODO</pre>

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

<pre>TODO: Fill in the table below</pre>


|      Algorithm     | 1-ply | 2-ply | 3-ply | 4-ply | 5-ply |
| :----------------: |:-----:|:-----:|:-----:|:-----:|:-----:|
|       Minimax      |       |       |       |       |       |
|  Parallel Minimax  |       |       |       |       |       |
|      Alphabeta     |       |       |       |       |       |
|      Jamboree      |       |       |       |       |       |

#### Conclusions ####
How close were your estimates to the actual values?  Did you find any
entry in the table surprising?  Based ONLY on this table, do you feel
like there is a substantial difference between the four algorithms?<pre>
<pre>TODO</pre>

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
<pre>TODO: Do the experiment; discuss the results (possibly with pretty graphs!)</pre>

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
<pre>TODO: Do the experiment; discuss the results (possibly with pretty graphs!)</pre>

#### Comparing The Algorithms ####
Now that you have found an optimal cut-off and an optimal number of processors, 
you should compare the actual run times of your four implementations. You MUST
use Google Compute Engine for this experiment (Remember: when calculating
runtimes using *timing*, the machine matters).  At depth 5, using your optimal 
cut-offs and the optimal number of processors, time all four of your algorithms
for each of the three boards.

Plot your results and discuss anything surprising about your results here.
<pre>TODO: Do the experiment; discuss the results (possibly with pretty graphs!)</pre>

|      Algorithm     | Early Game | Mid Game | End Game |
| :----------------: |:----------:|:--------:|:--------:|
|       Minimax      |            |          |          |
|  Parallel Minimax  |            |          |          |
|      Alphabeta     |            |          |          |
|      Jamboree      |            |          |          |


### Beating Traffic ###
In the last part of the project, you made a very small modification to your bot
to solve a new problem.  We'd like you to think a bit more about the 
formalization of the traffic problem as a graph in this question.  
- To use Minimax to solve this problem, we had to represent it as a game.  
  In particular,  the "states" of the game were "stretches of road" and the
  valid moves were choices of other adjacent "stretches of road".  The
  traffic and distance were factored in using the evaluation function.  If 
  you wanted to use Dijkstra's Algorithm to solve this problem instead of
  Minimax, how would you formulate it as a graph?<pre>
  <pre>TODO</pre>
    **TODO**: Answer this question
    </pre><br>
- These two algorithms DO NOT optimize for the same thing.  (If they did,
  Dijkstra's is always faster; so, there would be no reason to ever use
  Minimax.)  Describe the difference in what each of the algorithms is
  optimizing for.  When will they output different paths?
  <pre>TODO</pre>