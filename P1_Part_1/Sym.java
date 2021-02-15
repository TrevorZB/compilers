/**
 * Sym
 *
 * This class represents a Symbol stored in the SymTable object.
 */
public class Sym {

    // stored type of this Sym
    private String type;

    /**
     * Constructor of this object
     *
     * @param   (String type)   - type of this Sym
     * @return  (Sym)  - Sym instance returned
     */
    public Sym(String type) {
        this.type = type;
    }

    /**
     * Getter for the stored type
     *
     * @return  (String)  - type of this Sym
     */
    public String getType() {
        return this.type;
    }

    /**
     * String representation of this Sym object
     *
     * @return  (String)  - type of this Sym
     */
    public String toString() {
        return this.type;
    }

}