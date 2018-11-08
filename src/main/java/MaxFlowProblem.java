import gurobi.*;

public class MaxFlowProblem {

    private static final int s = 0;
    private static final int t = 7;
    private static final int anzahlKnoten = 8;
    private static final int[][] kanten = {{s, 1, 3},
            {s, 2, 2},
            {s, 3, 4},
            {1, 4, 5},
            {1, 2, 1},
            {2, 3, 2},
            {2, 5, 6},
            {3, 5, 8},
            {3, 6, 1},
            {4, 2, 3},
            {4, t, 1},
            {5, t, 5},
            {6, t, 3}};
    private static final int anzahlKanten = kanten.length;
    private static final int indexAusgangsknoten = 0;
    private static final int indexEingangsknoten = 1;
    private static final int indexGewicht = 2;

    public static void main(String[] args) throws GRBException {
        GRBEnv environment = new GRBEnv("linopt.log");
        GRBModel model = new GRBModel(environment);


        //initialize variables
        GRBVar[] x = new GRBVar[anzahlKanten];
        for (int e = 0; e < anzahlKanten; e++) {
            x[e] = model.addVar(0,
                    kanten[e][indexGewicht],
                    0,
                    GRB.INTEGER,
                    "x_(" + kanten[e][indexAusgangsknoten] + ","
                            + kanten[e][indexEingangsknoten] +
                            ")");
        }
        model.update();

        //objective
        GRBLinExpr objective = new GRBLinExpr();
        for (int e = 0; e < anzahlKanten; e++) {
            if (kanten[e][indexAusgangsknoten] == s) {
                objective.addTerm(1, x[e]);
            }
            if (kanten[e][indexEingangsknoten] == s) {
                objective.addTerm(-1, x[e]);
            }
        }
        model.setObjective(objective, GRB.MAXIMIZE);

        //subject to
        GRBLinExpr linkeSeite;
        GRBLinExpr rechteSeite;

        for (int v = 0; v < anzahlKnoten; v++) {
            linkeSeite = new GRBLinExpr();
            rechteSeite = new GRBLinExpr();

            if (v != s && v != t) {
                for (int e = 0; e < anzahlKanten; e++) {
                    if (kanten[e][indexEingangsknoten] == v) {
                        linkeSeite.addTerm(1, x[e]);
                    }
                }
                for (int e = 0; e < anzahlKanten; e++) {
                    if (kanten[e][indexAusgangsknoten] == v) {
                        rechteSeite.addTerm(1, x[e]);
                    }
                }
                model.addConstr(linkeSeite, GRB.EQUAL, rechteSeite, "constraint" + v);
            }
        }

        //solve
        model.optimize();

        model.write("maxFlowProblem.lp");
        model.write("maxFlowProblem.sol");

        model.dispose();
        environment.dispose();
    }
}
