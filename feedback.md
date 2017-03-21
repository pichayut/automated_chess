# Project 3 (Chess) Feedback #
## CSE 332 Winter 2017 ##

**Team:** Jacob A Gross (jag58) and Pichayut Liamthong (pichl) <br />
**Graded By:** Brandon Vincent (brandv2@cs.washington.edu)
<br>

## Unit Tests ##

**Minimax**  `(4/4)`
> ✓ Passed *depth2* <br>
> ✓ Passed *depth3* <br>

**ParallelMinimax**  `(15/15)`
> ✓ Passed *depth2* <br>
> ✓ Passed *depth3* <br>
> ✓ Passed *depth4* <br>

**AlphaBeta**  `(9/9)`
> ✓ Passed *depth2* <br>
> ✓ Passed *depth3* <br>
> ✓ Passed *depth4* <br>

**Jamboree**  `(20/20)`
> ✓ Passed *depth2* <br>
> ✓ Passed *depth3* <br>
> ✓ Passed *depth4* <br>
> ✓ Passed *depth5* <br>

## Clamps Tests ##

*Score*
`(10.0/8)`


--------

## Miscellaneous ##

`(-3/0)` 
Your ParallelSearcher copies boards in the parent
thread instead of in the child                   
<br />



--------

## Write-Up ##

**Project Enjoyment**
`(3/3)`
Glad your partnership worked well!

**Chess Server**
`(2/2)`

### Experiments ###

**Chess Game**
`(6/6)`

**Sequential Cut-Offs**
`(7/7)`

**Number of Processors**
`(7/7)`

**Comparing the Algorithms**
`(8/8)`

### Traffic ###

**Beating Traffic**
`(2/4)`
Dijkstra's could also account for multiple pieces of data on the edge weights by
combining them into some kind of score -- this is what the evaluator does. We
could calculate edge weights for Dijkstra's by precomputing the evaluator
function at every vertex. At the end of the day, the minimax traffic 
searcher is just condensing multiple factors into a score by calculating
`num. seconds in traffic / total seconds`.
So, your discussion doesn't address the fundamental
differences in how minimax and Dijkstra's optimize the path.


### Above and Beyond ###

**Above and Beyond**
`(EX: 25)`
