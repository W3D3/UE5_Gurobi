import gurobi.*;

/**
 * 5.1 Eine Firma fertigt 3 verschiedene Produkte A, B und C. Es stehen 4 Maschinen M1, M2, M3 und M4 zur Verfügung.
 * Zur Fertigung der Produkte werden (pro Stück) unter- schiedliche Bearbeitungszeiten (in Minuten) auf den einzelnen
 * Maschinen benötigt, die in der folgenden Tabelle angeführt sind. (siehe Blatt 5)
 */
public class Produktionsoptimierung {

    public static void main(String[] args) throws GRBException {

        GRBEnv environment = new GRBEnv("gurobi_log.log");
        environment.set(GRB.DoubleParam.TimeLimit, 60);
        GRBModel model = new GRBModel(environment);

        System.out.println();
        //initialize variables
        GRBVar x1, x2, x3;
        x1 = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.INTEGER, "x1");
        x2 = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.INTEGER, "x2");
        x3 = model.addVar(0, Double.POSITIVE_INFINITY, 0, GRB.INTEGER, "x3");
        model.update();

        //objective
        GRBLinExpr objective = new GRBLinExpr();
        objective.addTerm(2, x1);
        objective.addTerm(3, x2);
        objective.addTerm(5, x3);
        model.setObjective(objective, GRB.MAXIMIZE);

        //subject to
        GRBLinExpr maschinenConstraint1 = new GRBLinExpr();
        maschinenConstraint1.addTerm(1, x2);
        maschinenConstraint1.addTerm(5, x3);
        model.addConstr(maschinenConstraint1, GRB.LESS_EQUAL, 9 * 60, "Max_Maschinenstunden_M1");

        GRBLinExpr maschinenConstraintB = new GRBLinExpr();
        maschinenConstraintB.addTerm(2, x1);
        maschinenConstraintB.addTerm(2, x2);
        maschinenConstraintB.addTerm(6, x3);
        model.addConstr(maschinenConstraintB, GRB.LESS_EQUAL, 9 * 60, "Max_Maschinenstunden_M2");

        GRBLinExpr maschinenConstraintC = new GRBLinExpr();
        maschinenConstraintC.addTerm(3, x1);
        maschinenConstraintC.addTerm(1, x2);
        maschinenConstraintC.addTerm(0, x3);
        model.addConstr(maschinenConstraintC, GRB.LESS_EQUAL, 9 * 60, "Max_Maschinenstunden_M3");

        GRBLinExpr maschinenConstraint4 = new GRBLinExpr();
        maschinenConstraint4.addTerm(5, x1);
        maschinenConstraint4.addTerm(4, x2);
        maschinenConstraint4.addTerm(1, x3);
        model.addConstr(maschinenConstraint4, GRB.LESS_EQUAL, 9 * 60, "Max_Maschinenstunden_M3");

        //solve
        model.optimize();

        model.write("5.1a-produktionsoptimierung.lp");
        model.write("5.1a-produktionsoptimierung.sol");

        //b) mindestens 50 von A
        GRBLinExpr minAConstraint = new GRBLinExpr();
        minAConstraint.addTerm(1, x1);
        model.addConstr(minAConstraint, GRB.GREATER_EQUAL, 50, "Min50A");

        //solve
        model.optimize();

        model.write("5.1b-produktionsoptimierung.lp");
        model.write("5.1b-produktionsoptimierung.sol");

        model.dispose();
        environment.dispose();
    }
}
