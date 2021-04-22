import java.util.*;

/**
 * SymTable
 *
 * This class represents and stores a SymTable to be used with
 * compilation from symbols to types
 */
public class SymTable {

    // stored list of Hashmaps mapping names to types
    private List<HashMap<String, Sym>> blocks;
    
    /**
     * Constructor of this object, initializes the stored list of
     * hash maps with a single map entry
     *
     * @return  (SymTable)  - SymTable instance returned
     */
    public SymTable() {
        this.blocks = new LinkedList<HashMap<String, Sym>>();
        this.blocks.add(new HashMap<String, Sym>());
    }

    /**
     * Adds a declaration to the head map of the stored list of maps
     *
     * @param   (String name)   - name to be added
     * @param   (String Sym)    - Sym object to be added
     */
    public void addDecl(String name, Sym sym) throws DuplicateSymException, EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (sym == null) {
            throw new IllegalArgumentException();
        }
        HashMap<String, Sym> firstBlock = this.blocks.get(0);
        if (firstBlock.containsKey(name)) {
            throw new DuplicateSymException();
        }
        firstBlock.put(name, sym);
    }

    /**
     * Adds a map to the head of the stored list of maps
     *
     */
    public void addScope() {
        this.blocks.add(0, new HashMap<String, Sym>());
    }

    /**
     * Look for a given entry in the head map of the list of maps
     *
     * @param   (String name)   - name to be searched for
     * @return  (String Sym)    - Sym object to be returned if match found
     */
    public Sym lookupLocal(String name) throws EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        return this.blocks.get(0).get(name);
    }

    /**
     * Look for a given entry in all stored maps in the linked list
     *
     * @param   (String name)   - name to be searched for
     * @return  (String Sym)    - Sym object to be returned if match found
     */
    public Sym lookupGlobal(String name) throws EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        for (HashMap<String, Sym> block : this.blocks) {
            Sym sym = block.get(name);
            if (sym != null) {
                return sym; // prioritizes most recent scope explored
            }
        }
        return null;
    }

    /**
     * Remove the head scope from the stored linked list
     *
     */
    public void removeScope() throws EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        this.blocks.remove(0);
    }

    /**
     * Print a string representation of the SymTable
     *
     */
    public void print() {
        System.out.print("\nSym Table\n");
        for (HashMap<String, Sym> block : this.blocks) {
            System.out.println(block);
        }
        System.out.println("");
    }
}