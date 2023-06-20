public class ReinforcementLearning {
    private static final double EPSILON = 0.0001;

    public ReinforcementLearning() {

    }

    public Policy solveMDP(MDP mdp) {
        Policy policy = initPolicy(mdp.getNumStates(), mdp.getNumActions());

        // value[state] is the expected value of state given policy
        double[] value = new double[mdp.getNumStates()];
        for (int i = 0; i < mdp.getNumStates(); i++) {
            value[i] = 0.0;
        }
        while (true) {
            value = evaluatePolicy(policy, mdp, value);
            Policy temp = improvePolicy(policy, mdp, value);
            if (temp == null) {
                break;
            } else {
                policy = temp;
            }
        }
        return policy;
    }

    /**
     * initialize a policy such that all actions are equally likely
     */
    private Policy initPolicy(int numStates, int numActions) {
        Policy result = new Policy(numStates, numActions);
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                result.set(i, j, 1.0 / numActions);
            }
        }
        return result;
    }

    public double[] evaluatePolicy(Policy policy, MDP mdp, double[] value) {
        // newValue[state] is the next expected value of state given policy
        double[] newValue = new double[mdp.getNumStates()];
        double delta = 0.0;
        do {
            delta = 0.0;
            for (int i = 0; i < mdp.getNumStates(); i++) {
                newValue[i] = computeStateValue(i, mdp, policy, value);
                if (Math.abs(newValue[i] - value[i]) > delta) {
                    delta = Math.abs(newValue[i] - value[i]);
                }
            }
            for (int i = 0; i < mdp.getNumStates(); i++) {
                value[i] = newValue[i];
            }
        } while (delta > EPSILON);
        return value;
    }

    /**
     * Apply dynamic programming as explained in README to compute the next
     * iteration of the expected value of state given policy
     * 
     * @param state  the state to compute its next expected value
     * @param mdp    the Markov Decision Process that models the environment
     * @param policy the current policy of the agent
     * @param values the expected state values at the previous iteration of the
     *               algorithm
     * @return the expected value of state
     */
    private double computeStateValue(int state, MDP mdp, Policy policy, double[] values) {
         // initialize the return value as 0
         double svalue = 0.0;

         for(int action = 0; action < mdp.getNumActions(); action++) {
           double reward = mdp.getReward(state, action);
           for (int nextState = 0; nextState < mdp.getNumStates(); nextState++) {

             reward += mdp.getProb(state, nextState, action) * values[nextState];
           }
           svalue += policy.get(state, action) * reward;
         }
         //added twice, just return normal value
         return svalue;
        }
    
  

    

    private void computeBestActions(MDP mdp, double[] values,
            double[][] actionReward, double[] maxReward,
            int[] numBestActions) {
        for (int state = 0; state < mdp.getNumStates(); state++) {
            maxReward[state] = Double.NEGATIVE_INFINITY;

            for (int action = 0; action < mdp.getNumActions(); action++) {
                double reward = mdp.getReward(state, action);
                for (int j = 0; j < mdp.getNumStates(); j++) {
                    reward += mdp.getProb(state, j, action) * values[j];
                }
                actionReward[state][action] = reward;
                if (reward > maxReward[state]) {
                    maxReward[state] = reward;
                    numBestActions[state] = 0;
                }
                if (reward == maxReward[state]) {
                    numBestActions[state]++;
                }
            }
        }
    }

    public Policy improvePolicy(Policy policy, MDP mdp, double[] values) {
        Policy result = null;
        double[][] actionReward = new double[mdp.getNumStates()][mdp.getNumActions()];
        double[] maxReward = new double[mdp.getNumStates()];
        int[] numBestActions = new int[mdp.getNumStates()];
        // bestAction[state] is the action that has the maximum reward for state
        computeBestActions(mdp, values, actionReward, maxReward, numBestActions);
        Policy newPolicy = new Policy(mdp.getNumStates(), mdp.getNumActions());
        for (int i = 0; i < mdp.getNumStates(); i++) {
            for (int j = 0; j < mdp.getNumActions(); j++) {
                if (actionReward[i][j] == maxReward[i]) {
                    newPolicy.set(i, j, 1.0 / numBestActions[i]);
                }
            }
        }

        // did the policy converge?
        if (!policy.equals(newPolicy)) {
            result = newPolicy;
        }
        return result;
    }
}
