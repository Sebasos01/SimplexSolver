# Simplex-Based Linear Optimization Solver

This repository provides a basic framework for formulating and solving linear optimization problems using the Simplex method. The code showcases how to define objectives, constraints (inequalities/restrictions), and then solve for optimal solutions with sensitivity analysis. It also includes support for dual variables and intervals derived from inequalities.

## Overview of the Main Components

1. **Goal.java**  
   Defines the optimization direction: `MAX` for maximization or `MIN` for minimization.

2. **Inequality.java**  
   Represents a single linear inequality of the form:
   ```
   (coeff1 * var1 + coeff2 * var2 + ... ) >= constant
   ```
   Internally, it stores the variables, their coefficients, and a constant term. The `toString()` method formats the inequality for readable output.

3. **InequalitySolver.java**  
   Given a set of inequalities and focusing on one variable (with others assumed zero), `InequalitySolver` determines the feasible interval of that variable. It returns either a valid interval or `null` if no feasible solution exists.

4. **Main.java**  
   The entry point of the application. Here, you can:
   - Define decision variables.
   - Set up the objective function (with its coefficients and optimization direction).
   - Create constraints as `Restriction` objects.
   - Run the Simplex solver and perform sensitivity analysis.
   
   Change the commented sections to test different problem instances.

5. **ObjectiveFunction.java**  
   Specifies the objective function of the optimization problem. It sets whether to maximize or minimize a linear expression given by variable coefficients. This is used when constructing the Simplex tableau.

6. **Quadruple.java**  
   A simple record-like structure to store four related values.

7. **Relation.java**  
   Enumerates the type of constraint relation: `EQUAL`, `LESS_THAN_EQUAL`, or `GREATER_THAN_EQUAL`.

8. **Restriction.java**  
   Defines a single constraint (or “restriction”) in the linear problem. It holds:
   - A unique ID
   - A relation type (`=`, `<=`, `>=`)
   - Right-hand side value (RHS)
   - Lists of variables and their coefficients  
   
   This class also handles adding slack, surplus, and artificial variables as needed for the Simplex method.

9. **Simplex.java**  
   Implements the core Simplex algorithm steps:
   - Converts the given problem into a Simplex tableau.
   - Iteratively performs pivot operations to move towards the optimal solution.
   - Prints intermediate tableaus and final solutions.
   - Performs sensitivity analysis on constraints and resources once the optimal solution is found.

10. **Tuple.java**  
    A simple record-like structure to store a pair of values.

11. **Util.java**  
    Utility functions for:
    - Checking uniqueness of variables.
    - Sorting variables in a consistent order.
    - Handling substitutions and solving simplified inequalities for dual analysis.

## Running the Code

To run the code, open a Java environment (such as an IDE or command line), ensure the files are in a proper package and directory structure if needed, and run the `Main.java` file. You can adjust the defined variables, objective function, and constraints in `Main.java` to test different linear optimization scenarios.

## What This Code Demonstrates

- **Formulating a Linear Program (LP):** How to represent decision variables, the objective function, and constraints.
- **Solving with Simplex:** How to convert an LP into a tableau and use pivoting rules to reach an optimal solution.
- **Reading Results and Sensitivity Analysis:** Once an optimal solution is found, how to interpret dual variables, price ranges, and feasibility intervals.

This code is primarily for demonstration and instructional purposes. For production use, consider more robust implementations or libraries.