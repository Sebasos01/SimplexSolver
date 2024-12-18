import java.util.*;
import java.util.stream.Collectors;

public class Simplex {

    public static Quadruple<ArrayList<String>, HashMap<Integer, String>, double[][], HashMap<String, Double>> executeSimplex(ObjectiveFunction objectiveFunction, ArrayList<Restriction> restrictions) throws Exception {
        System.out.println("Función objetivo");
        System.out.println(objectiveFunction);
        System.out.println();
        System.out.println("Restricciones:");
        for (Restriction restriction : restrictions) {
            System.out.println(restriction);
        }
        System.out.println();
        ArrayList<String> restrictionVariables = Restriction.allVariables;
        ArrayList<String> objectiveFunctionVariables = objectiveFunction.getVariables();
        if (objectiveFunctionVariables.size() < restrictionVariables.size()) {
            throw new Exception("All constraint variables must be in those of the objective function");
        }
        for (int i = 0; i < restrictionVariables.size(); i++) {
            if (!restrictionVariables.get(i).equals(objectiveFunctionVariables.get(i))) {
                throw new Exception("All constraint variables must be in those of the objective function");
            }
        }
        Restriction.transformAndAddExtraVariables(restrictions);
        HashMap<String, Double> resources = new HashMap<>();
        for (Restriction restriction : restrictions) {
            if (restriction.getRelation() == Relation.GREATER_THAN_EQUAL) {
                String key = "DE" + restriction.getId();
                double value = restriction.getRhs();
                resources.put(key, value);
            } else if (restriction.getRelation() == Relation.LESS_THAN_EQUAL) {
                String key = "DS" + restriction.getId();
                double value = restriction.getRhs();
                resources.put(key, value);
            } else if (restriction.getRelation() == Relation.EQUAL) {
                String key = "DR" + restriction.getId();
                double value = restriction.getRhs();
                resources.put(key, value);
            }
        }
        restrictionVariables = (ArrayList<String>) restrictionVariables.clone();
        Goal goal = objectiveFunction.getGoal();
        int numAllVars = restrictionVariables.size();
        int numBasics = (int) restrictionVariables.stream()
                .filter(s -> s.matches("^([RS])\\d$"))
                .count();
        double[][] simplexTable = new double[numBasics + 1][numAllVars + 2];
        ArrayList<Tuple<String, double[]>> RSimplexRows = restrictions.stream()
                .filter(r -> r.containsVariable("^R\\d$"))
                .map(Restriction::getSimplexRow)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Tuple<String, double[]>> SSimplexRows = restrictions.stream()
                .filter(r -> r.containsVariable("^S\\d$"))
                .map(Restriction::getSimplexRow)
                .collect(Collectors.toCollection(ArrayList::new));
        simplexTable[0] = objectiveFunction.getSimplexRow(restrictionVariables, RSimplexRows.stream() .map(Tuple::second) .collect(Collectors.toCollection(ArrayList::new)));
        HashMap<Integer, String> rowMapping = new HashMap<>();
        rowMapping.put(0, "Z");
        for (int i = 0; i < RSimplexRows.size(); i++) {
            Tuple<String, double[]> RSimplexRow = RSimplexRows.get(i);
            String mainVariable = RSimplexRow.first();
            int position = i + 1;
            rowMapping.put(position, mainVariable);
            double[] simplexRow = RSimplexRow.second();
            simplexTable[position] = simplexRow;
        }
        for (int i = 0; i < SSimplexRows.size(); i++) {
            Tuple<String, double[]> SSimplexRow = SSimplexRows.get(i);
            String mainVariable = SSimplexRow.first();
            int position = i + 1 + RSimplexRows.size();
            rowMapping.put(position, mainVariable);
            double[] simplexRow = SSimplexRow.second();
            simplexTable[position] = simplexRow;
        }
        printSimplexTable(simplexTable, rowMapping, restrictionVariables);
        if (goal == Goal.MAX) {
            while(anyNegative(simplexTable[0])) {
                int incVar = -1;
                int outVar = -1;
                double min = Double.POSITIVE_INFINITY;
                for (int i = 0; i < simplexTable[0].length - 2; i++) {
                    if (simplexTable[0][i] < min) {
                        min = simplexTable[0][i];
                        incVar = i;
                    }
                }
                min = Double.POSITIVE_INFINITY;
                for (int i = 1; i < simplexTable.length; i++) {
                    double[] row = simplexTable[i];
                    double solution = row[row.length - 2];
                    double divisor = row[incVar];
                    double intersect = divisor != 0 ? solution/divisor : Double.POSITIVE_INFINITY;
                    row[row.length - 1] = intersect;
                    if (intersect < min && intersect >= 0) {
                        min = intersect;
                        outVar = i;
                    }
                }
                printSimplexTable(simplexTable, rowMapping, restrictionVariables);
                System.out.println("Entra la variable " + restrictionVariables.get(incVar));
                System.out.println("Sale la variable " + rowMapping.get(outVar));
                System.out.println();
                rowMapping.put(outVar, restrictionVariables.get(incVar));
                double[] newPivotRow = simplexTable[outVar].clone();
                for (int i = 0; i < newPivotRow.length - 1; i++) {
                    newPivotRow[i] = newPivotRow[i]/simplexTable[outVar][incVar];
                }
                newPivotRow[newPivotRow.length - 1] = 0;
                simplexTable[outVar] = newPivotRow;
                for (int i = 0; i < outVar; i++) {
                    double[] newRow = simplexTable[i].clone();
                    for (int j = 0; j < newRow.length - 1; j++) {
                        newRow[j] = newRow[j] - simplexTable[i][incVar]*newPivotRow[j];
                    }
                    newRow[newRow.length - 1] = 0;
                    simplexTable[i] = newRow;
                }
                for (int i = outVar + 1; i < simplexTable.length; i++) {
                    double[] newRow = simplexTable[i].clone();
                    for (int j = 0; j < newRow.length - 1; j++) {
                        newRow[j] = newRow[j] - simplexTable[i][incVar]*newPivotRow[j];
                    }
                    newRow[newRow.length - 1] = 0;
                    simplexTable[i] = newRow;
                }
                printSimplexTable(simplexTable, rowMapping, restrictionVariables);
            }
        } else if (goal == Goal.MIN) {
            while(anyPositive(simplexTable[0])) {
                int incVar = -1;
                int outVar = -1;
                double max = Double.NEGATIVE_INFINITY;
                for (int i = 0; i < simplexTable[0].length - 2; i++) {
                    if (simplexTable[0][i] > max) {
                        max = simplexTable[0][i];
                        incVar = i;
                    }
                }
                double min = Double.POSITIVE_INFINITY;
                for (int i = 1; i < simplexTable.length; i++) {
                    double[] row = simplexTable[i];
                    double solution = row[row.length - 2];
                    double divisor = row[incVar];
                    double intersect = divisor != 0 ? solution/divisor : Double.POSITIVE_INFINITY;
                    row[row.length - 1] = intersect;
                    if (intersect < min && intersect >= 0) {
                        min = intersect;
                        outVar = i;
                    }
                }
                printSimplexTable(simplexTable, rowMapping, restrictionVariables);
                System.out.println("Entra la variable " + restrictionVariables.get(incVar));
                System.out.println("Sale la variable " + rowMapping.get(outVar));
                System.out.println();
                rowMapping.put(outVar, restrictionVariables.get(incVar));
                double[] newPivotRow = simplexTable[outVar].clone();
                for (int i = 0; i < newPivotRow.length - 1; i++) {
                    newPivotRow[i] = newPivotRow[i]/simplexTable[outVar][incVar];
                }
                newPivotRow[newPivotRow.length - 1] = 0;
                simplexTable[outVar] = newPivotRow;
                for (int i = 0; i < outVar; i++) {
                    double[] newRow = simplexTable[i].clone();
                    for (int j = 0; j < newRow.length - 1; j++) {
                        newRow[j] = newRow[j] - simplexTable[i][incVar]*newPivotRow[j];
                    }
                    newRow[newRow.length - 1] = 0;
                    simplexTable[i] = newRow;
                }
                for (int i = outVar + 1; i < simplexTable.length; i++) {
                    double[] newRow = simplexTable[i].clone();
                    for (int j = 0; j < newRow.length - 1; j++) {
                        newRow[j] = newRow[j] - simplexTable[i][incVar]*newPivotRow[j];
                    }
                    newRow[newRow.length - 1] = 0;
                    simplexTable[i] = newRow;
                }
                printSimplexTable(simplexTable, rowMapping, restrictionVariables);
            }
        }
        Restriction.clearAll();
        return new Quadruple<>(restrictionVariables, rowMapping, simplexTable, resources);
    }

    public static void performSensitivityAnalysis(ArrayList<String> allVariables, HashMap<Integer, String> rowMapping, double[][] simplexTable, HashMap<String, Double> resources) {
        ArrayList<String> dualVariables = new ArrayList<>();
        ArrayList<Double> dualPrices = new ArrayList<>();
        HashSet<Character> excessGuard = new HashSet<>();
        for (int i = 0; i < allVariables.size(); i++) {
            String variable = allVariables.get(i);
            if (variable.matches("^S\\d$")) {
                dualVariables.add("DS" + variable.charAt(variable.length() - 1));
                dualPrices.add(simplexTable[0][i]);
            } else if (variable.matches("^E\\d$")) {
                Character number = variable.charAt(variable.length() - 1);
                dualVariables.add("DE" + number);
                dualPrices.add(simplexTable[0][i]);
                excessGuard.add(number);
            } else if (variable.matches("^R\\d$")) {
                Character number = variable.charAt(variable.length() - 1);
                if (excessGuard.contains(number)) continue;
                dualVariables.add("DR" + number);
                dualPrices.add(simplexTable[0][i]);
                excessGuard.add(number);
            }
        }
        ArrayList<Inequality> inequalities = new ArrayList<>();
        for (int i = 1; i < simplexTable.length; i++) {
            excessGuard.clear();
            double[] row = simplexTable[i];
            double rhs = row[row.length  -2];
            double[] dualVariableCoefficients = new double[dualVariables.size()];
            int k = 0;
            for (int j = 0; j < allVariables.size(); j++) {
                String variable = allVariables.get(j);
                if (variable.matches("^S\\d$")) {
                    dualVariableCoefficients[k] = row[j];
                    k++;
                } else if (variable.matches("^E\\d$")) {
                    dualVariableCoefficients[k] = row[j];
                    k++;
                    excessGuard.add(variable.charAt(variable.length() - 1));
                } else if (variable.matches("^R\\d$")) {
                    if (excessGuard.contains(variable.charAt(variable.length() - 1))) continue;
                    dualVariableCoefficients[k] = row[j];
                    k++;
                }
            }
            inequalities.add(new Inequality(dualVariables, dualVariableCoefficients, -rhs));
        }
        ArrayList<Optional<InequalitySolver.Interval>> feasibilityIntervals = new ArrayList<>();
        for (int i = 0; i < dualVariables.size(); i++) {
            feasibilityIntervals.add(Util.solveInequalityForVariable(dualVariables, inequalities, i));
        }
        System.out.println("Desigualdades");
        for (int i = 0; i < inequalities.size(); i++) {
            System.out.printf("%s = %s%n", rowMapping.get(i  + 1), inequalities.get(i));
        }
        System.out.println();
        System.out.println("Cambios de la disponibilidad de los recursos");
        printDualTable(dualVariables, dualPrices, feasibilityIntervals, resources);

        ArrayList<Inequality> reducedInequalities = generateInequalities(allVariables, rowMapping, simplexTable);
        System.out.println(reducedInequalities);
    }

    private static ArrayList<Inequality> generateInequalities(ArrayList<String> allVariables, HashMap<Integer, String> rowMapping, double[][] simplexTable) {
        ArrayList<Inequality> inequalities = new ArrayList<>();
        int numRows = simplexTable.length;
        int numCols = simplexTable[0].length;

        // Identificar las variables duales (Z1, Z2, Z3, etc.)
        ArrayList<String> dualVariables = new ArrayList<>();
        dualVariables.add("Z1"); // Suponiendo que Z corresponde a Z1
        for (int i = 1; i < numRows; i++) {
            String rowLabel = rowMapping.get(i);
            if (rowLabel.matches("^X\\d+$")) {
                String dualVar = "Z" + rowLabel.substring(1);
                if (!dualVariables.contains(dualVar)) {
                    dualVariables.add(dualVar);
                }
            }
        }

        // Para cada columna (variable)
        for (int j = 0; j < numCols - 2; j++) { // Excluir columnas de Solución e Intersección
            String variable = allVariables.get(j);
            double constant = simplexTable[0][j]; // Elemento superior de la columna
            double[] coefficients = new double[dualVariables.size()];

            // Inicializar los coeficientes
            for (int k = 0; k < coefficients.length; k++) {
                coefficients[k] = 0.0;
            }

            // Para cada fila (incluyendo Z)
            for (int i = 0; i < numRows; i++) {
                String rowLabel = rowMapping.get(i);
                double rowCoeff = 0.0;
                int varIndex = -1;

                if (rowLabel.equals("Z")) {
                    rowCoeff = 1.0;
                    varIndex = dualVariables.indexOf("Z1");
                } else if (rowLabel.matches("^X\\d+$")) {
                    String dualVar = "Z" + rowLabel.substring(1);
                    rowCoeff = 1.0;
                    varIndex = dualVariables.indexOf(dualVar);
                } else {
                    // Otras variables no se consideran
                    continue;
                }

                if (varIndex >= 0) {
                    coefficients[varIndex] += rowCoeff * simplexTable[i][j];
                }
            }

            // Restar el elemento superior de la columna (constante) solo si es una variable Xn
            if (variable.matches("^X\\d+$")) {
                int z1Index = dualVariables.indexOf("Z1");
                if (z1Index >= 0) {
                    coefficients[z1Index] -= constant;
                }
                // Cambiar el signo de la constante al lado derecho
                constant = -constant;
            } else {
                // La constante permanece en cero para variables que no son Xn
                constant = 0.0;
            }

            // Crear la inecuación
            Inequality inequality = new Inequality(dualVariables, coefficients, constant);

            inequalities.add(inequality);
        }

        return inequalities;
    }



    private static boolean anyNegative(double[] simplexRow) {
        for (int i = 0; i < simplexRow.length - 2; i++) {
            if (simplexRow[i] < 0) return true;
        }
        return false;
    }

    private static boolean anyPositive(double[] simplexRow) {
        for (int i = 0; i < simplexRow.length - 2; i++) {
            if (simplexRow[i] > 0) return true;
        }
        return false;
    }

    public static void printDualTable(
            ArrayList<String> dualVariables,
            ArrayList<Double> dualPrices,
            ArrayList<Optional<InequalitySolver.Interval>> feasibilityIntervals,
            HashMap<String, Double> resources) {
        // Encabezado de la tabla
        System.out.printf("| %-13s | %-16s | %-30s | %-6s | %-6s | %-6s |\n",
                "Recurso", "Precio dual ($)", "Intervalo de factibilidad", "Mínima", "Actual", "Máxima");
        System.out.println("|---------------|------------------|--------------------------------|--------|--------|--------|");
        // Recorrer cada índice, asumiendo que todas las listas tienen el mismo tamaño
        for (int i = 0; i < dualVariables.size(); i++) {
            String recurso = "Operación " + dualVariables.get(i).substring(2);
            double precioDual = dualPrices.get(i);
            Optional<InequalitySolver.Interval> intervalo = feasibilityIntervals.get(i);
            double actual = resources.get(dualVariables.get(i));
            // Determinar los valores de mínima y máxima en función del intervalo de factibilidad
            String intervaloFactibilidad;
            double minima, maxima;
            if (intervalo.isPresent()) {
                InequalitySolver.Interval interval = intervalo.get();
                double inferior = interval.lower;
                double superior = interval.upper;
                // Formato del intervalo de factibilidad
                intervaloFactibilidad = interval.toString();
                // Calcular valores mínima y máxima
                minima = actual - Math.abs(inferior);
                maxima = actual + Math.abs(superior);
                // Para intervalos con límite superior infinito
                if (Double.isInfinite(superior)) {
                    intervaloFactibilidad = interval.toString();
                    maxima = Double.POSITIVE_INFINITY;
                }
            } else {
                intervaloFactibilidad = "N/A";
                minima = actual;
                maxima = actual;
            }
            // Imprimir la fila
            System.out.printf("| %-13s | %-16.3f | %-30s | %-6.3f | %-6.3f | %-6s |\n",
                    recurso, precioDual, intervaloFactibilidad, minima, actual,
                    (maxima == Double.POSITIVE_INFINITY ? "Infinity" : String.format("%.3f", maxima)));
        }
    }

    private static void printSimplexTable(double[][] matrix, HashMap<Integer, String> rowMapping, ArrayList<String> restrictionVariables) {
        int numColumns = 1 + restrictionVariables.size() + 2; // 1 for "Básicas", + variables, + "Solución" and "Intersección"
        int numRows = matrix.length;
        int[] columnWidths = new int[numColumns];
        // Initialize columnWidths with header lengths
        // Column 0: "Básicas"
        columnWidths[0] = "Básicas".length();
        for (String variableName : rowMapping.values()) {
            if (variableName.length() > columnWidths[0]) {
                columnWidths[0] = variableName.length();
            }
        }
        // Columns for restrictionVariables
        for (int j = 1; j <= restrictionVariables.size(); j++) {
            int colIndex = j;
            String header = restrictionVariables.get(j - 1);
            columnWidths[colIndex] = header.length();
            for (int i = 0; i < numRows; i++) {
                double element = matrix[i][j - 1];
                String formattedElement = String.format("%.3f", element);
                if (formattedElement.length() > columnWidths[colIndex]) {
                    columnWidths[colIndex] = formattedElement.length();
                }
            }
        }
        // "Solución" column
        int solucionIndex = restrictionVariables.size() + 1;
        columnWidths[solucionIndex] = "Solución".length();
        for (int i = 0; i < numRows; i++) {
            double element = matrix[i][matrix[0].length - 2];
            String formattedElement = String.format("%.3f", element);
            if (formattedElement.length() > columnWidths[solucionIndex]) {
                columnWidths[solucionIndex] = formattedElement.length();
            }
        }
        // "Intersección" column
        int interseccionIndex = restrictionVariables.size() + 2;
        columnWidths[interseccionIndex] = "Intersección".length();
        for (int i = 0; i < numRows; i++) {
            double element = matrix[i][matrix[0].length - 1];
            String formattedElement = String.format("%.3f", element);
            if (formattedElement.length() > columnWidths[interseccionIndex]) {
                columnWidths[interseccionIndex] = formattedElement.length();
            }
        }
        // Print column headers with separators
        System.out.printf("%-" + columnWidths[0] + "s", "Básicas");
        for (int j = 1; j <= restrictionVariables.size(); j++) {
            int colIndex = j;
            String header = restrictionVariables.get(j - 1);
            System.out.printf(" | %-" + columnWidths[colIndex] + "s", header);
        }
        System.out.printf(" | %-" + columnWidths[solucionIndex] + "s", "Solución");
        System.out.printf(" | %-" + columnWidths[interseccionIndex] + "s", "Intersección");
        System.out.println();
        // Print separator line
        int totalWidth = columnWidths[0];
        for (int i = 1; i < numColumns; i++) {
            totalWidth += 3 + columnWidths[i]; // 3 accounts for " | "
        }
        for (int i = 0; i < totalWidth; i++) {
            System.out.print("-");
        }
        System.out.println();
        // Print data rows
        for (int i = 0; i < numRows; i++) {
            String variableName = rowMapping.get(i);
            System.out.printf("%-" + columnWidths[0] + "s", variableName);
            for (int j = 1; j <= restrictionVariables.size(); j++) {
                int colIndex = j - 1; // Matrix columns for variables start from 0
                double element = matrix[i][colIndex];
                String formattedElement = String.format("%.3f", element);
                System.out.printf(" | %" + columnWidths[j] + "s", formattedElement);
            }
            double solucionElement = matrix[i][matrix[0].length - 2];
            String formattedSolucion = String.format("%.3f", solucionElement);
            System.out.printf(" | %" + columnWidths[solucionIndex] + "s", formattedSolucion);
            double interseccionElement = matrix[i][matrix[0].length - 1];
            String formattedInterseccion = String.format("%.3f", interseccionElement);
            System.out.printf(" | %" + columnWidths[interseccionIndex] + "s", formattedInterseccion);
            System.out.println();
        }
        System.out.println();
    }
}
