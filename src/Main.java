import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws Exception {
        //test();
        run();
    }

    private static void test() {

    }

    private static void run() throws Exception {
        // 1. Define decision variables
        ArrayList<String> variables = new ArrayList<>(Arrays.asList("X1", "X2", "X3"));

        // 2. Define the objective function for maximization: Z = 3*X1 + 2*X2 + 5*X3
        ObjectiveFunction objectiveFunction = new ObjectiveFunction(
                Goal.MAX,                      // Type of optimization: Maximization
                variables,                     // Decision variables
                new ArrayList<>(Arrays.asList(3.0, 2.0, 5.0)) // Coefficients for objective function: 3*X1 + 2*X2 + 5*X3
        );

        // 3. Define the constraints
        ArrayList<Restriction> constraints = new ArrayList<>();

        // Constraint 1: X1 + 2*X2 + X3 <= 430
        constraints.add(new Restriction(
                1,                              // ID of the restriction
                Relation.LESS_THAN_EQUAL,       // Relation type: <=
                430.0,                          // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(1.0, 2.0, 1.0)) // Coefficients: 1*X1 + 2*X2 + 1*X3
        ));

        // Constraint 2: 3*X1 + 2*X3 <= 460
        constraints.add(new Restriction(
                2,                              // ID of the restriction
                Relation.LESS_THAN_EQUAL,       // Relation type: <=
                460.0,                          // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(3.0, 0.0, 2.0)) // Coefficients: 3*X1 + 0*X2 + 2*X3
        ));

        // Constraint 3: X1 + 4*X2 <= 420
        constraints.add(new Restriction(
                3,                              // ID of the restriction
                Relation.LESS_THAN_EQUAL,       // Relation type: <=
                420.0,                          // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(1.0, 4.0, 0.0)) // Coefficients: 1*X1 + 4*X2 + 0*X3
        ));


        /*// 1. Define decision variables
        ArrayList<String> variables = new ArrayList<>(Arrays.asList("X1", "X2"));

        // 2. Define the objective function for minimization: Z = 4*X1 + X2
        ObjectiveFunction objectiveFunction = new ObjectiveFunction(
                Goal.MIN,                      // Type of optimization: Minimization
                variables,                     // Decision variables
                new ArrayList<>(Arrays.asList(4.0, 1.0)) // Coefficients for objective function: 4*X1 + X2
        );

        // 3. Define the constraints
        ArrayList<Restriction> constraints = new ArrayList<>();

        // Constraint 1: 3*X1 + X2 = 3
        constraints.add(new Restriction(
                1,                              // ID of the restriction
                Relation.EQUAL,                 // Relation type: =
                3.0,                            // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(3.0, 1.0)) // Coefficients: 3*X1 + 1*X2
        ));

        // Constraint 2: 4*X1 + 3*X2 >= 6
        constraints.add(new Restriction(
                2,                              // ID of the restriction
                Relation.GREATER_THAN_EQUAL,    // Relation type: >=
                6.0,                            // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(4.0, 3.0)) // Coefficients: 4*X1 + 3*X2
        ));

        // Constraint 3: X1 + 2*X2 <= 4
        constraints.add(new Restriction(
                3,                              // ID of the restriction
                Relation.LESS_THAN_EQUAL,       // Relation type: <=
                4.0,                            // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(1.0, 2.0)) // Coefficients: 1*X1 + 2*X2
        ));*/

       /* // 1. Define decision variables
        ArrayList<String> variables = new ArrayList<>(Arrays.asList("X1", "X2", "X3"));

        // 2. Define the objective function for minimization: Z = 160*X1 + 120*X2 + 280*X3
        ObjectiveFunction objectiveFunction = new ObjectiveFunction(
                Goal.MIN,                      // Type of optimization: Minimization
                variables,                     // Decision variables
                new ArrayList<>(Arrays.asList(160.0, 120.0, 280.0)) // Coefficients for objective function: 160*X1 + 120*X2 + 280*X3
        );

        // 3. Define the constraints
        ArrayList<Restriction> constraints = new ArrayList<>();

        // Constraint 1: 2*X1 + X2 + 4*X3 >= 1
        constraints.add(new Restriction(
                1,                              // ID of the restriction
                Relation.GREATER_THAN_EQUAL,    // Relation type: >=
                1.0,                            // Right side of the restriction
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(2.0, 1.0, 4.0)) // Coefficients: 2*X1 + 1*X2 + 4*X3
        ));

        // Constraint 2: 2*X1 + 2*X2 + 2*X3 >= 3/2
        constraints.add(new Restriction(
                2,                              // ID of the restriction
                Relation.GREATER_THAN_EQUAL,    // Relation type: >=
                1.5,                            // Right side of the restriction (3/2 = 1.5)
                variables,                      // Variables involved
                new ArrayList<>(Arrays.asList(2.0, 2.0, 2.0)) // Coefficients: 2*X1 + 2*X2 + 2*X3
        ));*/

        /*// 1. Define las variables de decisión
        ArrayList<String> variables = new ArrayList<>(Arrays.asList("X1", "X2"));

        // 2. Define la función objetivo para minimización: Z = X1 + X2
                ObjectiveFunction objectiveFunction = new ObjectiveFunction(
                        Goal.MIN,                      // Tipo de optimización: Minimización
                        variables,                     // Variables de decisión
                        new ArrayList<>(Arrays.asList(1.0, 1.0)) // Coeficientes de la función objetivo: X1 + X2
                );

        // 3. Define las restricciones
                ArrayList<Restriction> constraints = new ArrayList<>();

        // Restricción 1: 2*X1 + X2 >= 4
                constraints.add(new Restriction(
                        1,                              // ID de la restricción
                        Relation.GREATER_THAN_EQUAL,    // Tipo de relación: >=
                        4.0,                            // Lado derecho de la restricción
                        variables,                      // Variables involucradas
                        new ArrayList<>(Arrays.asList(2.0, 1.0)) // Coeficientes: 2*X1 + 1*X2
                ));

        // Restricción 2: X1 + 7*X2 >= 7
                constraints.add(new Restriction(
                        2,                              // ID de la restricción
                        Relation.GREATER_THAN_EQUAL,    // Tipo de relación: >=
                        7.0,                            // Lado derecho de la restricción
                        variables,                      // Variables involucradas
                        new ArrayList<>(Arrays.asList(1.0, 7.0)) // Coeficientes: X1 + 7*X2
        ));*/


        // 4. Execute the Simplex method
        Quadruple<ArrayList<String>, HashMap<Integer, String>, double[][], HashMap<String, Double>> result = Simplex.executeSimplex(objectiveFunction, constraints);

        // Retrieve the results
        ArrayList<String> allVariables = result.first();          // All variables (including slack or artificial variables), defines the label of each column in order
        HashMap<Integer, String> rowMapping = result.second();    // Mapping of rows to variables
        double[][] simplexTable = result.third();                 // Resulting Simplex table
        HashMap<String, Double> resources = result.fourth();

        Simplex.performSensitivityAnalysis(allVariables, rowMapping, simplexTable, resources);
    }
}
