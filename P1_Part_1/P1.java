/**
 * P1
 *
 * This is a class is used to test the functionality of the SymTable class.
 * Functions tested:
 *       addDecl(String name, String symbol) -- adds entry to current scope
 *       addScope() -- adds a new scope to the linked list
 *       lookupLocal(String name) -- returns corresponding symbol
 *       lookupGlobal(String name) -- returns corresponding global symbol
 *       removeScope() -- removes head map of the linked list
 *       print() -- prints the SymTable in the correct format
 *
 * Outputs messages if something functions incorrectly except for the testing
 * of the print method.
 */
public class P1 {
    private static void printError(String test, String exp, String res) {
        System.out.println("ERROR In " + test);
        System.out.println("Expected Result: " + exp);
        System.out.println("Actual Result: " + res);
        System.out.println("");
    }

    /**
     * The main function used to run the testing suite.
     *
     * @param (parameter args) Not used.
     */
    public static void main(String []args) {
        try {
            // tests for addDecl function
            SymTable S = new SymTable();
            String testName = "addDecl";

            // test EmptySymTableException exception
            String exc = "EmptySymTableException";
            S.removeScope();
            try {
                S.addDecl("x", new Sym("int"));
                P1.printError(testName, exc + " thrown", exc + " not thrown");
            } catch (EmptySymTableException e) {}
            S.addScope();

            // test IllegalArgumentException
            exc = "IllegalArgumentException";
            try {
                S.addDecl(null, new Sym("int"));
                P1.printError(testName, exc + " thrown", exc + " not thrown");
            } catch (IllegalArgumentException e) {}
            try {
                S.addDecl("x", null);
                P1.printError(testName, exc + " thrown", exc + " not thrown");
            } catch (IllegalArgumentException e) {}

            // test DuplicateSymException
            exc = "DuplicateSymException";
            S.addDecl("x", new Sym("int"));
            try {
                S.addDecl("x", new Sym("int"));
                P1.printError(testName, exc + " thrown", exc + " not thrown");
            } catch (DuplicateSymException e) {}

            // test that the mapping was successfully added
            if (S.lookupLocal("x") == null) {
                P1.printError(testName, "int returned", "null returned");
            }


            // tests for addScope
            S = new SymTable();
            testName = "addScope";

            // test new scope is added correctly
            exc = "EmptySymTableException";
            S.removeScope();
            S.addScope();
            try {
                S.removeScope();
            } catch (EmptySymTableException e) {
                P1.printError(testName, exc + " not thrown", exc + " thrown");
            }


            // tests for lookupLocal
            S = new SymTable();
            testName = "lookupLocal";

            // test EmptySymTableException
            exc = "EmptySymTableException";
            S.removeScope();
            try {
                S.lookupLocal("x");
                P1.printError(testName, exc + " not thrown", exc + " thrown");
            } catch (EmptySymTableException e) {}

            // test lookupLocal is finding match if it is present
            S.addScope();
            S.addDecl("x", new Sym("int"));
            if (S.lookupLocal("x") == null) {
                P1.printError(testName, "int returned", "null returned");
            }


            // tests for lookupGlobal
            S = new SymTable();
            testName = "lookupGlobal";

            // test EmptySymTableException
            exc = "EmptySymTableException";
            S.removeScope();
            try {
                S.lookupGlobal("x");
                P1.printError(testName, exc + " not thrown", exc + " thrown");
            } catch (EmptySymTableException e) {}

            // test globalLookup finds entry in second map
            S.addScope();
            S.addDecl("x", new Sym("int"));
            S.addScope();
            if (S.lookupGlobal("x") == null) {
                P1.printError(testName, "int returned", "null returned");
            }

            // test globalLookup prioritizes the first map
            S.addDecl("x", new Sym("double"));
            Sym sym = S.lookupGlobal("x");
            if (sym == null || !sym.getType().equals("double")) {
                P1.printError(testName, "double returned", "null returned");
            }


            // tests for removeScope
            S = new SymTable();
            testName = "removeScope";

            // test EmptySymTableException
            exc = "EmptySymTableException";
            S.removeScope();
            try {
                S.removeScope();
                P1.printError(testName, exc + " thrown", exc + " not thrown");
            } catch (EmptySymTableException e) {}

            // test removeScope successfully removes the first map
            S.addScope();
            S.removeScope();
            try {
                S.removeScope();
                P1.printError(testName, exc + " thrown", exc + " not thrown");
            } catch (EmptySymTableException e) {}


            // tests for print
            S = new SymTable();
            testName = "print";

            // test print function prints out correct display pattern
            S.addScope();
            S.addDecl("x", new Sym("int"));
            S.addDecl("y", new Sym("double"));
            S.addScope();
            S.addDecl("z", new Sym("str"));
            S.print();
        } catch (Exception e) {
            System.out.println("UNKNOWN ERROR OCCURED");
        }
    }
}