import java.util.*;

public class SymTable {

    private List<HashMap<String, Sym>> blocks;
    
    public SymTable() {
        this.blocks = new LinkedList<HashMap<String, Sym>>();
        this.blocks.add(new HashMap<String, Sym>());
    }

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

    public void addScope() {
        this.blocks.add(0, new HashMap<String, Sym>());
    }

    public Sym lookupLocal(String name) throws EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        return this.blocks.get(0).get(name);
    }

    public Sym lookupGlobal(String name) throws EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        for (HashMap<String, Sym> block : this.blocks) {
            Sym sym = block.get(name);
            if (sym != null) {
                return sym;
            }
        }
        return null;
    }

    public void removeScope() throws EmptySymTableException {
        if (this.blocks.isEmpty()) {
            throw new EmptySymTableException();
        }
        this.blocks.remove(0);
    }

    public void print() {
        System.out.print("\nSym Table\n");
        for (HashMap<String, Sym> block : this.blocks) {
            System.out.println(block);
        }
        System.out.println("");
    }

}
