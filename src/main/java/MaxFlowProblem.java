import gurobi.*;

public class MaxFlowProblem {

    private static final int s = 1;
    private static final int t = 10;
    private static final int anzahlKnoten = 10;
    private static final int[][] kanten = {
            {s, 2, 10},
            {s, 3, 2},
            {s, 4, 3},
            {2, 3, 2},
            {2, 5, 6},
            {2, 6, 3},
            {3, 6, 2},
            {3, 7, 7},
            {4, 3, 4},
            {4, 6, 2},
            {4, 7, 7},
            {5, 3, 5},
            {5, 6, 2},
            {5, 8, 4},
            {6, 8, 2},
            {6, 9, 2},
            {6, 7, 3},
            {7, 9, 8},
            {8, 9, 1},
            {8, t, 5},
            {9, t, 12}};
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

        for (int v = 1; v <= anzahlKnoten; v++) {
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

        model.write("5.3a-flow.lp");
        model.write("5.3a-flow.sol");


        //b)
        int[] knotenLimit = {-1, 10, 7, 5, 7, 6, 7, 6, 10, -1};

        GRBLinExpr knoten;
        GRBLinExpr limit;

        for (int v = 0; v < knotenLimit.length; v++) {
            knoten = new GRBLinExpr();
            limit = new GRBLinExpr();

            if (knotenLimit[v] > 0) {
                for (int e = 0; e < anzahlKanten; e++) {
                    if (kanten[e][indexEingangsknoten] == v) {
                        knoten.addTerm(1, x[e]);
                    }
                }
                limit.addConstant(knotenLimit[v]);
                model.addConstr(knoten, GRB.LESS_EQUAL, limit, "limit_for_" + (v + 1));
            }
        }
        model.update();

        //solve
        model.optimize();

        model.write("5.3b-flow.lp");
        model.write("5.3b-flow.sol");

        model.dispose();
        environment.dispose();
    }
}
