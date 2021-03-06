package pack_1;

import pack_AI.AI_manager;
import pack_AI.AI_type;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    //note, a lot of this stuff isnt actually constants, coupled with most things being static
    //and also non-final, this is probably bad OOP. A lot of this should be handled by
    //ParameterGatherAndSetter really

    //new params should be added as a public static field in Constants and a new "case"
    //block added to Constants.setParamsFromProgramArgs to parse the program arguments accordingly
    //the value can then be used in the code wherever desired.

    public static PVector ATTACKER_START_POSITION;

    /**
     * Switch for whether to give the move planner the correct boid parameters
     * or whether it must use the learned boid parameters to simulate the defenders.
     * To use the correct boid parameters pass -a or --perfect-ai in the args.
     */
    public static boolean PERFECT_AI = false;

    public static final AI_type CORRECT_AI_PARAMS = AI_manager.getAi_basic();

    /**
     * Switch for whether to give the move planner the correct waypoints
     * or whether it must use the learned waypoints to simulate the defenders.
     * To use perfect way points pass -w or --perfect-waypoints in the args.
     */
    public static boolean PERFECT_WAYPOINTS = false;

    /**
     * Sets the perfect waypoints for the current run
     */
    public static List<PVector> DEFENDER_BOID_WAYPOINTS;

    /**
     * The time required for waypoints of the defenders to be adequately
     * learned enough for the attacker to start the approach.
     * attacker should be frozen in place before this time.
     * This was set empirically.
     */
    public static final int warmUpTime = 200;

    /**
     * The target for the attack boids.
     * Default to (550, 500). Set as an argument using
     * (-t|--target) x y, for example -t 400 300
     */
    public static PVector TARGET = new PVector(550,500);

    /**
     * File to output results to
     * Set as an argument using (-o|--output) fileName
     * for example -o results
     * If ommited, results will not be written to file
     */
    public static String OUTPUT_FILE = null;

    /**
     * If below is set on the command line then it will pause
     * ZoneDefence thread until MCTS has performed the given
     * number of iterations before refreshing the tree.
     * (-p|--debug-sim-limit) on command line
     */
    public static int DEBUG_SIM_LIMIT = 0;

    /** Distance required for a 'hit' on target to be recognized and its square */
    public static final int HIT_DISTANCE = 10;
    public static final int HIT_DISTANCE_SQ = HIT_DISTANCE * HIT_DISTANCE;


    public static class Boids {
        public static final float MAX_STEER = 0.02f;
        public static final float MAX_SPEED = 1.0f;
        public static final float MAX_ACC_ATTACK = 0.1f;
        public static final float MAX_BOID_ACC = 1.0f;
        public static final float SIZE = 6.0f;
        public static final float MAX_SPEED_ATTACK = 1.75f;
    }

    public static final double SQRT2 = Math.sqrt(2);


    public static void setParamsFromProgramArgs(String[] args) throws IllegalArgumentException {
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "-t":
                case "--target":
                    if(i + 2 >= args.length) {
                        throw new IllegalArgumentException("Command line argument --target requires two parameters, x, and y, for the starting position");
                    }
                    TARGET = new PVector(Float.parseFloat(args[++i]), Float.parseFloat(args[++i]));
                    break;
                case "-o":
                case "--output":
                    if(i + 1 >= args.length) {
                        throw new IllegalArgumentException("Command line argument --out requires one parameter, the file name to output to");
                    }
                    OUTPUT_FILE = args[++i];
                    break;
                case "-p":
                case "--debug-sim-limit":
                    if(i + 1 >= args.length) {
                        throw new IllegalArgumentException("Command line argument --debug-sim-limit requires one parameter, the number of desired iterations");
                    }
                    DEBUG_SIM_LIMIT = Integer.parseInt(args[++i]);
                    break;
                case "-w":
                case "--perfect-waypoints":
                    PERFECT_WAYPOINTS = true;
                    break;
                case "-a":
                case "perfect-ai":
                    PERFECT_AI = true;
                    break;

            }
        }
    }
}
