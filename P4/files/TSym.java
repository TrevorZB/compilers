import java.util.*;

public class TSym {
    private String type;
    
    public TSym(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return type;
    }
}

class FuncTSym extends TSym {
    private LinkedList<String> myFormalTypes;

    public FuncTSym(String type, LinkedList<String> formalTypes) {
        super(type);
        this.myFormalTypes = formalTypes;
    }

    public LinkedList<String> getMyFormalTypes() {
        return this.myFormalTypes;
    }
}

class StructTSym extends TSym {
    private SymTable myStructFields;

    public StructTSym(String type, SymTable structFields) {
        super(type);
        this.myStructFields = structFields;
    }

    public SymTable getMyStructFields() {
        return this.myStructFields;
    }
}
