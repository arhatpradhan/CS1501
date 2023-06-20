# CS 1501 – Algorithms and Data Structures II - Bonus Assignment

Due: Tuesday December 20th @ 4:59pm on GradeScope

**There is no late submission deadline for this assignment.**

## Overview

Purpose: The purpose of this assignment is to practice dynamic programming in AI applications.

Reinforcement Learning is a type of Machine Learning in which an agent learns an optimal policy by getting reward signals from the typically non-deterministic environment. We can use dynamic programming to compute an optimal policy given the specification of the environment as detailed below.

## Details

In Reinforcement Learning, the environment is modeled as a Markov Decision Process (MDP), which consists of the following components:

- A set of states
- A set of agent actions
- The transition probabilities, that is, the probability of moving from one state to another by taking an action
- The reward function, which maps a reward value (possibly zero or negative) for each pair of state and action

The class `MDP` models an MDP and allows for loading it from a file with a particular structure as specified in the comments before the `load` method in  `MDP.java`. There are four example MDP files in this repository: 

- `mdp-small.txt` models a 2x2 grid 
- `mdp-medium.txt` models a 3x3 grid
- `mdp-large.txt` models a 4x4 grid

The rewards and transition probabilities sets the problem as finding the shortest path to the goal square, which is at the bottom right corner of the grid. Each action results in a cost of 1 (or a reward of -1) until the agent reaches the goal square, at which the rewards are 0 and the agent stays at the goal state once it reaches it (check the rewards and transition probabilities at state 3, 8, and 15, in the three files, respectively).

- `mdp-volcano.txt` models a 3x4 grid with a volcano at square 2 and 6 (with a reward of -50), a safe place across at state 3 (with a reward of 20), and another safe place with a smaller reward at square 8 with a reward of 2. This file is adapted from https://www.youtube.com/watch?v=9g32v7bK3Co.

In all these environments, the agent is trying to learn a policy to navigate the environment to get the maximum reward possible. For example, the agent tries to reach the goal states as fast as possible in the first three files, and tries to reach the high-reward safe space without falling into the volcanos for the fourth file. The agent policy can be represented as a function that maps each (state, action) pair into a probability. The probability represents the likelihood of taking an action at a given state. The `Policy` class encapsulates agent policies.

Each state has a value that is equal to the expected reward the agent can get when starting at the state. The state value can be computed as the sum over all possible actions of the probability of taking the action (which depends on the agent's policy) * the expected reward from the action. The expected reward from an action equals the immediate reward (from the reward function) + the sum over all the states reachable by taking that action of the transition probability * value of next state.

To find an optimal policy, we can use dynamic programming as follows. 

- Step 0: Start with an initial policy (e.g., all actions are equally likely) and an initial state values (e.g., all zeros) (Check `initPolicy` in `ReinforcementLearning.java`)
- Step 1: Compute the state values as explained in the paragraph above using the initial state values (Check `evaluatePolicy` and `computeStateValue` in `ReinforcementLearning.java`)
- Step 2: Update the state values (Check `evaluatePolicy` in `ReinforcementLearning.java`)
- Repeat Step 1 and 2 until convergence (i.e., the largest difference in state values is below some small value (`EPSILON` in `ReinforcementLearning.java`)) (Check `evaluatePolicy` in `ReinforcementLearning.java`)
- Step 3: Modify the agent policy so that the agent takes the action that gives the largest reward at each state with probability 1 (if there are more than one action with the same maximum reward value, assign them equal probabilities) (Check `improvePolicy` and `computeBestActions` in `ReinforcementLearning.java`)
- Step 4: Repeat Step 1-3 until the policy converges, that is the best action(s) at each state are the same between iterations

The above algorithm has been developed for you in the `solveMDP` method in `ReinforcementLearning.java`.

## Task

Your task is to write the definition of the `computeStateValue` as specified in `ReinforcementLearning.java`. Use the pseudo-code provided for you in the file as guidance.

```java
 /**
     * Apply dynamic programming as explained in README to compute the next 
     * iteration of the expected value of state given policy
     * 
     * @param state the state to compute its next expected value
     * @param mdp the Markov Decision Process that models the environment
     * @param policy the current policy of the agent
     * @param values the expected state values at the previous iteration of the algorithm
     * @return the expected value of state
     */
    private double computeStateValue(int state, MDP mdp, Policy policy, double[] values) {
      //TODO Write the definition of this method
       return -1.0;
    }
```

After writing this method, test your implementation using `A5Test` as follows:

```
java A5Test mdp-small.txt
java A5Test mdp-medium.txt
java A5Test mdp-large.txt
java A5Test mdp-volcano.txt
```

The expected outputs are provided for you in the files `small-out.txt`, `medium-out.txt`, `large-out.txt`, and `volcano-out.txt`.

## Submission Requirements

You are allowed to change the following file ONLY.

1.	`ReinforcementLearning.java`

The idea from your submission is that the autograder can compile and run your programs from the command line WITHOUT ANY additional files or changes, so be sure to test it thoroughly before submitting it. If the autograder cannot compile or run your submitted code it will be graded as if the program does not work.

Note: If you use an IDE such as NetBeans, Eclipse, or IntelliJ, to develop your programs, make sure they will compile and run on the command-line before submitting – this may require some modifications to your program (such as removing some package information).

## Rubrics

__*Please note that if an autograder is available, its score will be used as a guidance for the TA, not as an official final score*__.

Please also note that the autograder rubrics are the definitive rubrics for the assignment. There is no partial credit for this assignment.

