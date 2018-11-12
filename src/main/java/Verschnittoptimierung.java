import gurobi.*;

/**
 * Eine Dreherei erhält von ihrem Materiallieferanten Edelstahl stets in Form von 3m langen Stangen.
 * Diese müssen für die verschiedenen Aufträge zunächst in Stücke der Länge L1 = 1m, L2 = 2m, L3 = 1.5m und L4 = 0.9m
 * zersägt werden, von denen 10, 45, 21 und 41 Mengeneinheiten benötigt werden. Wie kann man den Bedarf an unterschiedlich
 * langen Stangen decken und gleichzeitig dafür sorgen, dass der gesamte Materialeinsatz möglichst gering ist?
 */
public class Verschnittoptimierung {


    /**
     * Sinnvolle Lösungen:
     * Muster \ Länge 1m  |  2 m  |  1.5m |  0.9m  | VERSCHNITT
     * 1              3   |   0   |   0   |   0    |  0
     * 2              2   |   0   |   0   |   0    |  1
     * 3              1   |   0   |   0   |   0    |  2
     * 4              0   |   1   |   0   |   0    |  1
     * 5              1   |   1   |   0   |   0    |  0
     * 6              0   |   0   |   2   |   0    |  0
     * 7              0   |   0   |   1   |   0    |  1.5
     * 8              1   |   0   |   1   |   0    |  0.5
     * 9              0   |   0   |   0   |   3    |  0.3
     * 10             0   |   0   |   0   |   2    |  1.2
     * 11             0   |   0   |   0   |   1    |  2.1
     * 12             1   |   0   |   0   |   2    |  0.2
     * 13             2   |   0   |   0   |   1    |  0.1
     * 14             0   |   0   |   1   |   1    |  0.6
     * 15             0   |   1   |   0   |   1    |  0.1
     */

    private static final double[][] schnittmuster = {
            {3, 0, 0, 0, 0},
            {2, 0, 0, 0, 1},
            {1, 0, 0, 0, 2},
            {0, 1, 0, 0, 1},
            {1, 1, 0, 0, 0},
            {0, 0, 2, 0, 0},
            {0, 0, 1, 0, 1.5},
            {1, 0, 1, 0, 0.5},
            {0, 0, 0, 3, 0.3},
            {0, 0, 0, 2, 1.2},
            {0, 0, 0, 1, 2.1},
            {1, 0, 0, 2, 0.2},
            {2, 0, 0, 1, 0.1},
            {0, 0, 1, 1, 0.6},
            {0, 1, 0, 1, 0.1}};

    private static final int[] minProduktion = {10, 45, 21, 41};
    private static final int indexVerschnitt = 4;

    public static void main(String[] args) throws GRBException {

        GRBEnv environment = new GRBEnv("gurobi_log.log");
        environment.set(GRB.DoubleParam.TimeLimit, 60);
        GRBModel model = new GRBModel(environment);

        //initialize variables
        GRBVar[] x = new GRBVar[schnittmuster.length];
        for (int i = 0; i < schnittmuster.length; i++) {
            x[i] = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.INTEGER, "M" + (i + 1));
        }
        model.update();


        for (int j = 0; j < minProduktion.length; j++) {
            GRBLinExpr minAnzahl = new GRBLinExpr();
            for (int i = 0; i < schnittmuster.length; i++) {
                minAnzahl.addTerm(schnittmuster[i][j], x[i]);
            }

            model.addConstr(minAnzahl, GRB.GREATER_EQUAL, minProduktion[j], "minProduction_" + (j + 1));
        }
        model.update();

        GRBLinExpr objective = new GRBLinExpr();

        for (int i = 0; i < schnittmuster.length; i++) {
            objective.addTerm(1, x[i]);
        }
        model.setObjective(objective, GRB.MINIMIZE);


        // solve
        model.optimize();

        model.write("5.3a-verschnittoptimierung.lp");
        model.write("5.3a-verschnittoptimierung.sol");

        //b) mindestens 50 von A
        GRBLinExpr newObjective = new GRBLinExpr();


        // Preis pro Stange 50€, 8 € pro m Verschnitt
        for (int i = 0; i < schnittmuster.length; i++) {
            newObjective.addTerm(50, x[i]);
            newObjective.addTerm(-schnittmuster[i][indexVerschnitt] * 8, x[i]);
        }
        model.setObjective(newObjective, GRB.MINIMIZE);

        //solve
        model.optimize();

        model.write("5.3b-verschnittoptimierung.lp");
        model.write("5.3b-verschnittoptimierung.sol");

        model.dispose();
        environment.dispose();
    }
}
