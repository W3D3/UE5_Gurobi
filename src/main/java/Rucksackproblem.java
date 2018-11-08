import gurobi.*;

public class Rucksackproblem {

    private static final double[] zeit = {14, 13, 13, 12, 11, 11, 8, 7, 6, 6, 4, 3};
    private static final double[] gewinn = {570, 520, 530, 410, 340, 300, 250, 240, 200, 220, 130, 110};
    private static final double[] lizensen = {14, 16, 8, 13, 4, 21, 26, 17, 5, 5, 12, 35};
    private static final int cnt = zeit.length;
    private static final double max_zeit = 20 * 3; // 3 Wochen zu je 20 h
    private static final double max_money = 70; // max 70k lizenskosten

    public static void main(String[] args) throws GRBException {

        GRBEnv environment = new GRBEnv("5.2-log.log");
        GRBModel model = new GRBModel(environment);

        //initialize variables
        GRBVar[] x = new GRBVar[cnt];
        for (int i = 0; i < cnt; i++) {
            x[i] = model.addVar(0, 1, 0, GRB.BINARY, "S" + (i + 1));
        }
        model.update();

        //objective
        GRBLinExpr objective = new GRBLinExpr();
        for (int i = 0; i < cnt; i++) {
            objective.addTerm(gewinn[i], x[i]);
        }
        model.setObjective(objective, GRB.MAXIMIZE);

        //subject to
        GRBLinExpr zeitContraint = new GRBLinExpr();
        for (int i = 0; i < cnt; i++) {
            zeitContraint.addTerm(zeit[i], x[i]);
        }
        model.addConstr(zeitContraint, GRB.LESS_EQUAL, max_zeit, "zeitConstraint");

        //solve
        model.optimize();

        model.write("5.2a-software.lp");
        model.write("5.2a-software.sol");

        //b) with max 70 k licencing costs
        //subject to
        GRBLinExpr moneyConstraint = new GRBLinExpr();
        for (int i = 0; i < cnt; i++) {
            moneyConstraint.addTerm(lizensen[i], x[i]);
        }
        model.addConstr(moneyConstraint, GRB.LESS_EQUAL, max_money, "moneyConstraint");

        model.optimize();
        model.write("5.2b-software.lp");
        model.write("5.2b-software.sol");

        model.dispose();
        environment.dispose();
    }
}
