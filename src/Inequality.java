import java.util.ArrayList;

public class Inequality {
    ArrayList<String> dualVariables;
    double[] coefficients; // Coefficients for D1, D2, ..., Dn
    double constant;       // The constant term on the right-hand side

    public Inequality(ArrayList<String> dualVariables, double[] coefficients, double constant) {
        this.dualVariables = dualVariables;
        this.coefficients = coefficients;
        this.constant = constant;
    }

    public double getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coefficients.length; i++) {
            double coeff = coefficients[i];
            if (coeff != 0) {
                // Determine the sign
                if (!sb.isEmpty()) {
                    sb.append(coeff > 0 ? " + " : " - ");
                } else if (coeff < 0) {
                    sb.append("-");
                }
                // Append the absolute value of the coefficient with limited decimals if it's not 1
                if (Math.abs(coeff) != 1) {
                    sb.append(String.format("%.3f", Math.abs(coeff)));
                }
                // Append the variable name
                sb.append(dualVariables.get(i));
            }
        }
        // If all coefficients are zero, represent it as "0"
        if (sb.isEmpty()) {
            sb.append("0");
        }
        // Append the inequality sign and the constant with limited decimals
        sb.append(" >= ").append(String.format("%.3f", constant));
        return sb.toString();
    }

}
