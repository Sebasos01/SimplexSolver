import java.util.ArrayList;

public class ObjectiveFunction {
    private final Goal goal;
    private final ArrayList<String> variables;
    private final ArrayList<Double> coefficients;
    private final static int M = 100;

    public ObjectiveFunction(Goal goal, ArrayList<String> variables, ArrayList<Double> coefficients) throws Exception {
        if (variables.size() != coefficients.size()) {
            throw new Exception("Variables and coefficients must have the same size");
        }
        if (!Util.hasUniqueElements(variables)) {
            throw new Exception("Variables must have only unique elements");
        }
        ArrayList<String> variables1 = (ArrayList<String>) variables.clone();
        Util.sortVariables(variables1);
        variables1.replaceAll(String::toUpperCase);
        this.coefficients = (ArrayList<Double>) coefficients.clone();
        this.variables = variables1;
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }

    public int getNumVars() {
        return coefficients.size();
    }

    public ArrayList<String> getVariables() {
        return variables;
    }

    public double[] getSimplexRow(ArrayList<String> allVariables, ArrayList<double[]> RSimplexRows) throws Exception {
        if (allVariables.size() < variables.size()) {
            throw new Exception("variables is not a subarray of allVariables");
        }
        for (int i = 0; i < variables.size(); i++) {
            if (!allVariables.get(i).equals(variables.get(i))) {
                throw new Exception("variables is not a subarray of allVariables");
            }
        }
        double[] simplexRow = new double[allVariables.size() + 2];
        for (int i = 0; i < variables.size(); i++) {
            simplexRow[i] = (-1)*coefficients.get(i);
        }
        for (int i = variables.size(); i < allVariables.size(); i++) {
            if (allVariables.get(i).matches("^R\\d$")) simplexRow[i] = (goal == Goal.MAX ? 1 : -1)*M;
        }
        for (double[] RSimplexRow : RSimplexRows) {
            for (int i = 0; i < simplexRow.length; i++) {
                simplexRow[i] = simplexRow[i] +  (goal == Goal.MAX ? -1 : 1)*M*RSimplexRow[i];
            }
        }
        return simplexRow;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Append the goal (Maximize or Minimize)
        sb.append(goal == Goal.MAX ? "Maximize" : "Minimize").append(" z = ");

        boolean first = true;

        for (int i = 0; i < coefficients.size(); i++) {
            double coeff = coefficients.get(i);
            String variable = variables.get(i);

            if (coeff != 0) {
                // Determine the sign and append appropriately
                if (!first) {
                    sb.append(coeff > 0 ? " + " : " - ");
                } else if (coeff < 0) {
                    sb.append("-");
                }

                // Append the absolute value of the coefficient with two decimal places if it's not 1
                if (Math.abs(coeff) != 1) {
                    sb.append(String.format("%.3f", Math.abs(coeff)));
                }

                // Append the variable name
                sb.append(variable);

                first = false;
            }
        }

        // If all coefficients are zero, represent the objective function as "0"
        if (first) {
            sb.append("0");
        }

        return sb.toString();
    }

}
