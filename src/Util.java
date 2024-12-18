import java.util.*;

public class Util {

    public static boolean hasUniqueElements(ArrayList<String> list) {
        HashSet<String> set = new HashSet<>(list);
        return set.size() == list.size();
    }

    public static void sortVariables(ArrayList<String> variables) {
        // Custom sorting logic to sort the variables
        variables.sort((v1, v2) -> {
            // Extract the type (X, S, E, R, or others) and number from each variable
            String type1 = v1.replaceAll("\\d", "");
            String type2 = v2.replaceAll("\\d", "");
            int number1 = Integer.parseInt(v1.replaceAll("\\D", "0"));
            int number2 = Integer.parseInt(v2.replaceAll("\\D", "0"));
            // Define the order of types
            String order = "XERS";
            // Compare types based on their position in the order string
            int typeComparison = Integer.compare(order.indexOf(type1), order.indexOf(type2));
            if (typeComparison != 0) {
                return typeComparison;
            }
            // If types are the same, compare numbers
            return Integer.compare(number1, number2);
        });
    }

    public static Optional<InequalitySolver.Interval> solveInequalityForVariable(ArrayList<String> variables, List<Inequality> inequalities, int variableToSolve) {
        List<Inequality> modifiedInequalities = setVariablesToZero(variables, inequalities, variableToSolve);
        InequalitySolver solver = new InequalitySolver(modifiedInequalities, variableToSolve);
        InequalitySolver.Interval interval = solver.solve();
        return Optional.ofNullable(interval);
    }

    /**
     * Sets all variables except the one to solve for to zero.
     *
     * @param inequalities    The original list of inequalities.
     * @param variableToSolve The index of the variable to solve for.
     * @return A new list of inequalities with other variables set to zero.
     */
    private static List<Inequality> setVariablesToZero(ArrayList<String> variables, List<Inequality> inequalities, int variableToSolve) {
        List<Inequality> modified = new ArrayList<>();
        for (Inequality ineq : inequalities) {
            double[] newCoefficients = new double[ineq.coefficients.length];
            double newConstant = ineq.constant;
            for (int i = 0; i < ineq.coefficients.length; i++) {
                if (i == variableToSolve) {
                    newCoefficients[i] = ineq.coefficients[i];
                } else {
                    // Set D_i = 0, so a_i * D_i = 0; subtract from constant if needed
                    newConstant += ineq.coefficients[i] * 0; // Since D_i = 0
                    newCoefficients[i] = 0;
                }
            }
            modified.add(new Inequality(variables, newCoefficients, newConstant));
        }

        return modified;
    }
}
