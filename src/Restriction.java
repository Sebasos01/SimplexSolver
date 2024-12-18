import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Restriction {
    private final int id;
    private Relation relation;
    private double rhs;
    private ArrayList<Double> coefficients;
    private final ArrayList<String> variables;
    public static final ArrayList<String> allVariables =  new ArrayList<>();
    private static final HashSet<Integer> ids = new HashSet<>();
    public Restriction(int id, Relation relation, double rhs, ArrayList<String> variables, ArrayList<Double> coefficients) throws Exception {
        if (ids.contains(id)) {
            throw new Exception("Ids must be unique");
        }
        ids.add(id);
        if (variables.size() != coefficients.size()) {
            throw new Exception("Variables and coefficients must have the same size");
        }
        if (!Util.hasUniqueElements(variables)) {
            throw new Exception("Variables must have only unique elements");
        }
        ArrayList<String> variables1 = (ArrayList<String>) variables.clone();
        variables1.replaceAll(String::toUpperCase);
        for (String variable : variables1) {
            addToAllVariables(variable);
        }
        Util.sortVariables(variables1);
        this.id = id;
        this.relation = relation;
        this.rhs = rhs;
        this.variables = variables1;
        this.coefficients = (ArrayList<Double>) coefficients.clone();
    }
    public Relation getRelation() {
        return relation;
    }
    public int getNumVars() {
        return coefficients.size();
    }

    public ArrayList<Double> getCoefficients() {
        return coefficients;
    }

    public ArrayList<String> getVariables() {
        return variables;
    }

    public double getRhs() {
        return rhs;
    }

    public int getId() {
        return id;
    }

    public boolean containsVariable(String regex) {
        for (String variable : variables) {
            if (variable.matches(regex)) return true;
        }
        return false;
    }

    private void setCoefficients(ArrayList<Double> coefficients) throws Exception {
        if (coefficients.size() != this.variables.size()) {
            throw new Exception("Variables and coefficients must have the same size");
        }
        this.coefficients = coefficients;
    }

    private void setRelation(Relation relation) {
        this.relation = relation;
    }

    private void setRhs(double rhs) {
        this.rhs = rhs;
    }

    private void addVariable(String variable, double coefficient) throws Exception {
        if (variables.contains(variable)) {
            throw new Exception("The variable already exists");
        }
        variables.add(variable);
        coefficients.add(coefficient);
        addToAllVariables(variable);
    }

    public Tuple<String, double[]> getSimplexRow() {
        String mainVariable = containsVariable("^R\\d$") ? "R" + id : "S" + id;
        double[] simplexRow = new double[allVariables.size() + 2];
        for (int i = 0; i < allVariables.size(); i++) {
            String variable = allVariables.get(i);
            simplexRow[i] = variables.contains(variable) ? coefficients.get(variables.indexOf(variable)) : 0;
        }
        simplexRow[simplexRow.length - 2] = rhs;
        return new Tuple<>(mainVariable, simplexRow);
    }

    private void multiplyByMinusOne() throws Exception {
        if (relation == Relation.LESS_THAN_EQUAL) {
            setRelation(Relation.GREATER_THAN_EQUAL);
        } else if (relation == Relation.GREATER_THAN_EQUAL) {
            setRelation(Relation.LESS_THAN_EQUAL);
        }
        setRhs((-1)*rhs);
        setCoefficients(coefficients.stream().map(x -> (-1)*x).collect(Collectors.toCollection(ArrayList::new)));
    }

    private static void addToAllVariables(String variable) {
        if (!allVariables.contains(variable)) {
            allVariables.add(variable);
            Util.sortVariables(allVariables);
        }
    }

    public static void transformAndAddExtraVariables(ArrayList<Restriction> restrictions) throws Exception {
        for (Restriction restriction : restrictions) {
            if (restriction.getRhs() < 0) {
                restriction.multiplyByMinusOne();
            }
            if (restriction.getRelation() == Relation.EQUAL) {
                String newVariable = "R" + restriction.getId();
                restriction.addVariable(newVariable, 1);
            } else if (restriction.getRelation() == Relation.LESS_THAN_EQUAL) {
                String newVariable = "S" + restriction.getId();
                restriction.addVariable(newVariable, 1);
            } else if (restriction.getRelation() == Relation.GREATER_THAN_EQUAL) {
                String newVariable1 = "E" + restriction.getId();
                String newVariable2 = "R" + restriction.getId();
                restriction.addVariable(newVariable1, -1);
                restriction.addVariable(newVariable2, 1);
            }
        }
    }

    public static void clearAll() {
        allVariables.clear();
        ids.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Iterate through coefficients and variables to build the left-hand side (LHS) of the restriction
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

        // If all coefficients are zero, represent the LHS as "0"
        if (first) {
            sb.append("0");
        }

        // Append the relation symbol
        switch (relation) {
            case LESS_THAN_EQUAL:
                sb.append(" <= ");
                break;
            case GREATER_THAN_EQUAL:
                sb.append(" >= ");
                break;
            case EQUAL:
                sb.append(" = ");
                break;
            default:
                sb.append(" ? ");
                break;
        }

        // Append the right-hand side (RHS) with two decimal places
        sb.append(String.format("%.2f", rhs));

        return sb.toString();
    }

}
