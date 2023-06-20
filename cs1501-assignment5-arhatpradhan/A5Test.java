public class A5Test {
    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("Usage: java A5Test <filename>");
        } else {
            MDP mdp = new MDP();
            ReinforcementLearning rl = new ReinforcementLearning();
            mdp.load(args[0]);
            double[] value = new double[mdp.getNumStates()];
            for(int i=0; i<mdp.getNumStates(); i++){
                value[i] = 0.0;
            }
            Policy opt = rl.solveMDP(mdp);
            show(mdp, opt, rl.evaluatePolicy(opt, mdp, value));
        }
    }

    /**
     * helper method for showing the value array and current policy
     */
    private static void show(MDP mdp, Policy policy, double[] value) {
        System.out.println("State Values:");
        System.out.println("--------------------------------------");
        System.out.println("State\tValue");
        System.out.println("--------------------------------------");

        for (int i = 0; i < mdp.getNumStates(); i++) {
            System.out.println(i + "\t" + value[i]);
        }
        System.out.println();
        System.out.println(policy);
    }

}
