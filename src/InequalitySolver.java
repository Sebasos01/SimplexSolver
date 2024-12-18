import java.util.ArrayList;
import java.util.List;

public class InequalitySolver {
    private final List<Inequality> inequalities;
    private final int variableIndex; // 0-based index of the variable to solve for

    public InequalitySolver(List<Inequality> inequalities, int variableIndex) {
        this.inequalities = inequalities;
        this.variableIndex = variableIndex;
    }

    public Interval solve() {
        double lowerBound = Double.NEGATIVE_INFINITY;
        double upperBound = Double.POSITIVE_INFINITY;

        for (Inequality ineq : inequalities) {
            double coefficient = ineq.coefficients[variableIndex];
            double constant = ineq.constant;

            // Since all other variables are set to zero, inequality simplifies to:
            // coefficient * Dj >= constant
            if (coefficient == 0) {
                if (constant > 0) {
                    // 0 * Dj >= constant => 0 >= constant
                    // If constant > 0, no solution
                    return null;
                }
                // If constant <= 0, inequality is always true; no impact on bounds
                continue;
            }

            double boundary = constant / coefficient;

            if (coefficient > 0) {
                // Dj >= boundary
                lowerBound = Math.max(lowerBound, boundary);
            } else {
                // coefficient < 0
                // Dj <= boundary
                upperBound = Math.min(upperBound, boundary);
            }
        }

        if (lowerBound > upperBound) {
            // No feasible interval
            return null;
        }

        return new Interval(lowerBound, upperBound);
    }

    public static class Interval {
        double lower;
        double upper;

        public Interval(double lower, double upper) {
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public String toString() {
            return (lower <= upper
                    ? String.format("[%.3f, %.3f]", lower, upper)
                    : " has no feasible solution");
        }
    }
}
