# Project 3 (Chess) Write-Up #
--------

### Who Are You? What Are You Doing Here? ###
* Who was in your group?  (please list all names and cs/uw netids)<pre>
    **TODO**: Answer this question
    </pre><br>
* Who/What was most helpful for this project?  Is there a resource you would 
  recommend to next quarter's students?<pre>
    **TODO**: Answer this question
    </pre><br>

-----

### How Was Your Partnership? ###
* If you DID work in a group:
    - [ ] Did both partners do an equal amount of work?  If not, why not? 
          What happened?<pre>
        **TODO**: Answer this question
    </pre><br>
    
    - [ ] Describe each group member's contributions/responsibilities in the
          project.<pre>
        **TODO**: Answer this question
    </pre><br>
    
    - [ ] Describe at least one good thing and one bad thing about working
          together.<pre>
        **TODO**: Answer this question
    </pre><br>

* If you DID NOT work in a group:
    - [ ] Do you regret working alone?  Why or why not?<pre>
        **TODO**: Answer this question
    </pre><br>
    
    - [ ] Describe at least one good thing and one bad thing about working
          alone.<pre>
        **TODO**: Answer this question
    </pre><br>

-----

### How Was The Project? ###
* Time
    - [ ] How long did the project take?  (approximate in hours)<pre>
        **TODO**: Answer this question
    </pre><br>
    
    - [ ] Do you think the project was too time consuming/difficult?  Just
          right? or too easy?<pre>
        **TODO**: Answer this question
    </pre><br>
    
* Enjoyment
    - [ ] What was your favorite part of the project?  What was your least
          favorite part of the project?<pre>
        **TODO**: Answer this question
    </pre><br>
    
    - [ ] How could the project be improved?<pre>
        **TODO**: Answer this question
    </pre><br>
    
    - [ ] Was there anything in the documentation/handouts that was confusing?
          This project is new; any feedback is useful.<pre>
        **TODO**: Answer this question
    </pre><br>

-----

### The Chess Server and Above and Beyond ###
- [ ] When you faced Clamps, what did the code you used do?  Was it just your 
      parallel alphabeta?  Did you do something
      fancier?<pre>
    **TODO**: Answer this question
    </pre><br>

- [ ] Did you enjoy watching your bot play on the server?  Is your bot better
      at chess than you are?<pre>
    **TODO**: Answer this question
    </pre><br>

- [ ] Did your bot compete with anyone else in the class?  Did you win?<pre>
    **TODO**: Answer this question
    </pre><br>

- [ ] Did you do any Above and Beyond?  Describe exactly what you
      implemented.<pre>
    **TODO**: Answer this question
    </pre><br>
    
-----

### Experiments ###
* Chess Game
 - [ ] Suppose your bot goes 3-ply deep.  How many game tree nodes do you think
       it explores (we're looking for an order of magnitude) if:
        - ...you're using minimax?<pre>
            **TODO**: Answer this question
        </pre><br>
        - ...you're using alphabeta?<pre>
            **TODO**: Answer this question
        </pre><br>

- [ ] Run an experiment to determine the actual answers for the above.  To run
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

        **TODO**: Do the experiment; fill in the table
        
        |      Algorithm     | 1-ply | 2-ply | 3-ply | 4-ply | 5-ply |
        | :----------------: |:-----:|:-----:|:-----:|:-----:|:-----:|
        |       Minimax      |       |       |       |       |       |
        |  Parallel Minimax  |       |       |       |       |       |
        |      Alphabeta     |       |       |       |       |       |
        |      Jamboree      |            |          |          |
    <br>

- [ ] How close were your estimates to the actual values?  Did you find any
      entry in the table surprising?  Based ONLY on this table, do you feel
      like there is a substantial difference between the four algorithms?<pre>
    **TODO**: Answer this question
    </pre><br>

* **THE FOLLOWING EXPERIMENTS WILL TAKE A LONG TIME TO RUN. TO MAKE THIS BETTER...**
    * Use Google Compute Engine!  This will make the experiments go much faster.
    * Run multiple experiments at the same time, but **NOT ON THE SAME MACHINE**.
    * Google Compute Engine lets you spin up as many instances as you want.
    * Again: **TAKE ADVANTAGE OF GOOGLE COMPUTE ENGINE**
<br>

* Sequential Cut-Offs
- [ ] Experimentally determine the best sequential cut-off for both of your
      parallel searchers.  You should test this at depth 5.  If you want it
      to go more quickly, now is a good time to figure out Google Compute
      Engine.  Because chess games are very different at the beginning, middle,
      and end, you should choose the starting board, a board around the middle
      of a game, and a board about 5 moves from the end of the game.  Plot your
      results and discuss which cut-offs work the best here.<pre>
    **TODO**: Do the experiment; create a graph; discuss the results
    </pre><br>

* Number of Processors
- [ ] Now that you have found an optimal cut-off, you should find the optimal
      number of processors.  You MUST use Google Compute Engine for this
      experiment. For the same three boards that you used in the previous 
      experiment, at the same depth 5, using your optimal cut-offs, test your
      algorithm on 1, 2, 3, ..., 32 processors.  You can tell the ForkJoin 
      framework to only use k processors by giving an argument when constructing
      the pool, e.g.,
      ```java
      ForkJoinPool POOL = new ForkJoinPool(k);
      ```
      Plot your results and discuss which number of processors works the best
      here.<pre>
    **TODO**: Do the experiment; create a graph; discuss the results
    </pre><br>

* Comparing the Algorithms
- [ ] Now that you have found an optimal cut-off and an optimal number of
      processors, you should compare the actual run times of the four 
      implementations. You MUST use Google Compute Engine for this
      experiment (Remember: when calculating runtimes using *time*, the machine
      matters).  For the same three boards that you used in the previous
      two experiments, at the same depth 5, using the optimal cut-offs, and
      the optimal number of processors, time all four of your algorithms.
      Plot your results and discuss anything surprising about your results
      here.
     
    **TODO**: Do the experiment; create a graph; fill in the table; discuss

    |      Algorithm     | Early Game | Mid Game | End Game |
    | :----------------: |:----------:|:--------:|:--------:|
    |       Minimax      |            |          |          |
    |  Parallel Minimax  |            |          |          |
    |      Alphabeta     |            |          |          |
    |      Jamboree      |            |          |          |
<br>

* Beating Traffic
In the last part of the project, you made a very small modification to your bot
to solve a new problem.  We'd like you to think a bit more about the 
formalization of the traffic problem as a graph in this question.  
- [ ] To use Minimax to solve this problem, we had to represent it as a game.  
      In particular,  the "states" of the game were "stretches of road" and the
      valid moves were choices of other adjacent "stretches of road".  The
      traffic and distance were factored in using the evaluation function.  If 
      you wanted to use Dijkstra's Algorithm to solve this problem instead of
      Minimax, how would you formulate it as a graph?<pre>
    **TODO**: Answer this question
    </pre><br>
- [ ] Given your formulation of this problem as a graph, which algorithm solves
      the problem more quickly (asymptotically)?  Dijkstra's or Minimax?<pre>
    **TODO**: Answer this question
    </pre><br>
- [ ] Do the two algorithms optimize for the same thing?  Will they output the
      same paths?<pre>
    **TODO**: Answer this question
    </pre><br>