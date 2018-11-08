import gurobi.*;

public class Transportproblem {

    private static final int[][] c = {{37, 14, 11}, {27, 9, 12}, {24, 14, 17}};
    private static final int[] a = {900, 500, 1200};
    private static final int anzahlErzeuger = a.length;
    private static final int[] b = {900, 1100, 600};
    private static final int anzahlAbnehmer = 3;

    public static void main(String[] args) throws GRBException {

        GRBEnv environment = new GRBEnv("linopt.log");
        GRBModel model = new GRBModel(environment);

        //initialize variables
        GRBVar[][] x = new GRBVar[anzahlErzeuger][anzahlAbnehmer];
        for (int i = 0; i < anzahlErzeuger; i++) {
            for (int j = 0; j < anzahlAbnehmer; j++) {
                x[i][j] = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.INTEGER, "x" + i + "_" + j);
            }
        }
        model.update();

        //objective
        GRBLinExpr objective = new GRBLinExpr();
        for (int i = 0; i < anzahlErzeuger; i++) {
            for (int j = 0; j < anzahlAbnehmer; j++) {
                objective.addTerm(c[i][j], x[i][j]);
            }
        }
        model.setObjective(objective, GRB.MINIMIZE);

        //subject to
        GRBLinExpr bedarf;
        for (int j = 0; j < anzahlAbnehmer; j++) {
            bedarf = new GRBLinExpr();
            for (int i = 0; i < anzahlErzeuger; i++) {
                bedarf.addTerm(1, x[i][j]);
            }
            model.addConstr(bedarf, GRB.EQUAL, b[j], "bedarf_" + j);
        }

        GRBLinExpr transportieren;
        for (int i = 0; i < anzahlErzeuger; i++) {
            transportieren = new GRBLinExpr();
            for (int j = 0; j < anzahlAbnehmer; j++) {
                transportieren.addTerm(1, x[i][j]);
            }
            model.addConstr(transportieren, GRB.EQUAL, a[i], "transport_" + i);
        }

        //solve
        model.optimize();

        model.write("transportproblem.lp");
        model.write("transportproblem.sol");

        model.dispose();
        environment.dispose();
    }
}
