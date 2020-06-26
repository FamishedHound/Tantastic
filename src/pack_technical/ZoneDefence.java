package pack_technical;

import pack_1.ParameterGatherAndSetter;
import pack_boids.Boid_generic;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


//TODO: rename delay2

public class ZoneDefence implements Cloneable {
    private static GameManager manager;

    public ArrayList<Boid_generic> getBoids() {
        return boids;
    }

    private boolean defend = true;
    private ArrayList<Boid_generic> boids;
    private ArrayList<Boid_generic> attackBoids;
    private PApplet parent;
    static int coutner = 0;
    boolean flag = true;
    int DELAY = 200;
    int delay2 = 0;
    Simulation s;


    CollisionHandler handler;
    PatternHandler pattern;

    //timing simulation/real world
    float time = 0;
    long startTime = 0;
    float circumfence;
    private PatrollingScheme patrolling = new PatrollingScheme(0.04f);
    private ArrayList<PVector> waypoints = patrolling.getWaypoints();
    EnviromentalSimulation sim;
    boolean attack = false;
    FlockManager flock;
    int timer = 0;
    ParameterSimulation param;
    ParameterGatherAndSetter output;


    public PrintWriter writer14 = new PrintWriter("output/AttackingAndUpdatingTime.txt");

    public ZoneDefence(BaseManager b, GameManager g, PApplet p, CollisionHandler collision, FlockManager flock, ParameterGatherAndSetter output) throws IOException {
        this.flock = flock;
        this.handler = collision;
        this.parent = p;
        this.manager = g;
        boids = manager.get_team(0);
        attackBoids = manager.get_team(1);
        pattern = new PatternHandler();
        this.output = output;
        waypoints.addAll(output.returnDifficulty());
        patrolling.getWaypointsA().add(new PVector(550, 500));
        patrolling.setup();
    }


    public void run() throws IOException {
        if (pattern.isOnce()) {
            //after sim constructor has completed is the point where the MCTS is running.
            sim = new EnviromentalSimulation(40, 70, 70, 2.0f, 1.2f, 0.9f, "", boids, parent, pattern.getImg().getNewpoints(), attackBoids, handler);
            param = new ParameterSimulation(parent, boids, pattern.getImg().getNewpoints(), sim.getSimulator());
            pattern.setOnce(false);
        }

        if (sim != null) {
            if (param.observe(boids) == 1) {
                sim.setAiToInnerSimulation(param.updateAi());
                output.sendParameters(param.updateAi());
                attack = true;
                writer14.write("I started to attack " + "," + Math.round((System.nanoTime() - startTime) / 1000000) + "," + coutner + "\n");
                writer14.flush();
            }
        }

        for (Boid_generic attackBoid : attackBoids) {
            coutner++;
            if (coutner >= DELAY / 8 && coutner <= DELAY * 2) {
                if (!attack) attackBoid.setToMove(false);
                attackBoid.setStationary();
                delay2++;
            }

            if (delay2 >= 200) {
                pattern.newObservation(boids, coutner);
                if (attackBoids != null && flag && pattern.analyze() == 1) {
                    circumfence = (float) (3.14 * 2 * pattern.getRadius());
                    System.out.println(attackBoids);
                    time = (circumfence / boids.get(0).getVelocity().mag());
                    System.out.println(boids.get(0).getVelocity().mag() + "   " + circumfence + "   " + time + "  " + (float) startTime);
                    flag = false;
                    startTime = System.nanoTime();
                }
            }

            // ATACK MODE
            if (attack) {
                attackBoid.setToMove(true);
                PVector acceleration = attackBoid.getAcceleration();
                PVector velocity = attackBoid.getVelocity();
                PVector location = attackBoid.getLocation();
                velocity.limit(1);

                //System.out.println("Asking for target vector!");
                PVector attackVector = sim.reutrnTargetVecotr();
                sim.updateBoids(boids, attackBoids);

                location.add(velocity.add(acceleration.add(attackVector)));
                acceleration.mult(0);
            } else if (!attack) {
                attackBoid.setStationary();
            }
        }

        for (Boid_generic defenderBoid : boids) {
            if (defend) {
                PVector acceleration = defenderBoid.getAcceleration();
                PVector velocity = defenderBoid.getVelocity();
                //PVector velocity = new PVector(0,0);
                PVector location = defenderBoid.getLocation();
                velocity.limit(1);
                location.add(velocity.add(patrolling.patrol(defenderBoid.getLocation(), defenderBoid)));
                acceleration.mult(0);
            } else {
                defenderBoid.setStationary();
            }
        }
        output.iterations++;
    }

    public PVector attack(Boid_generic b1, int boidType) {
        PVector target = new PVector(0, 0, 0);

        for (Boid_generic b2 : boids) {
            target = PVector.sub(new PVector(550, 500), b1.getLocation());
            if (boidType == 1) target.setMag((float) 0.09);
            if (boidType == 2) target.setMag((float) 0.01);
        }
        return target;
    }

}
