import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a C-- program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Children
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of children, or
// internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode {
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndentation(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    public void nameAnalysis(SymTable s) {
        myDeclList.nameAnalysis(s);
    }

    private DeclListNode myDeclList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    public void nameAnalysis(SymTable s, SymTable ...structTable) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).nameAnalysis(s, structTable);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    public void nameAnalysis(SymTable s) {
        Iterator it = myFormals.iterator();
        try {
            while (it.hasNext()) {
                ((FormalDeclNode)it.next()).nameAnalysis(s);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in FormalDeclNode.print");
            System.exit(-1);
        }
    }

    public LinkedList<String> getFormalTypes() {
        LinkedList<String> formalTypes = new LinkedList<String>();
        Iterator it = myFormals.iterator();
        try {
            while (it.hasNext()) {
                formalTypes.add(((FormalDeclNode)it.next()).getMyType().getType());
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in FormalDeclNode.print");
            System.exit(-1);
        }
        return formalTypes;
    }

    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    public void nameAnalysis(SymTable s) {
        myDeclList.nameAnalysis(s);
        myStmtList.nameAnalysis(s);
    }

    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    public void nameAnalysis(SymTable s) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().nameAnalysis(s);
        }
    }

    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    public void nameAnalysis(SymTable s) {
        Iterator<ExpNode> it = myExps.iterator();
        while (it.hasNext()) {
            it.next().nameAnalysis(s);
        }
    }

    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    abstract public void nameAnalysis(SymTable s, SymTable ...structTable);
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalysis(SymTable s, SymTable ...structTable) {
        boolean typeVoid = false;
        IdNode id = this.myId;
        String name = id.getMyStrVal();

        if (myType.getType() == "void") {
            ErrMsg.fatal(id.getMyLineNum(), id.getMyCharNum(), "Non-function declared void");
            ErrMsg.fatalEncountered = true;
            typeVoid = true;
        }

        try {
            TSym lookup = s.lookupLocal(name);

            if (lookup == null) {
                if (mySize != NOT_STRUCT) {
                    TSym t;
                    IdNode struct_id = ((StructNode)this.myType).getId();
                    if (structTable.length != 0) {
                        t = structTable[0].lookupGlobal(struct_id.getMyStrVal());
                    } else {
                        t = s.lookupGlobal(struct_id.getMyStrVal());
                    }
                    if (t == null) {
                        ErrMsg.fatal(struct_id.getMyLineNum(), struct_id.getMyCharNum(), "Invalid name of struct type");
                        ErrMsg.fatalEncountered = true;
                    } else if (!(t instanceof StructTSym)) {
                        ErrMsg.fatal(struct_id.getMyLineNum(), struct_id.getMyCharNum(), "Invalid name of struct type");
                        ErrMsg.fatalEncountered = true;
                    } else {
                        StructTSym tsym = new StructTSym(((StructNode)this.myType).getId().getMyStrVal(), ((StructTSym)t).getMyStructFields());
                        s.addDecl(name, tsym);
                    }
                } else {
                    TSym tsym = new TSym(this.myType.getType());
                    if (!typeVoid) {
                        s.addDecl(name, tsym);
                    }
                }
            } else {
                ErrMsg.fatal(id.getMyLineNum(), id.getMyCharNum(), "Multiply declared identifier");
                ErrMsg.fatalEncountered = true;
            }
        } catch (DuplicateSymException|EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    public void nameAnalysis(SymTable s, SymTable ...structTable) {
        try {
            IdNode id = this.myId;
            String name = id.getMyStrVal();
            TSym lookup = s.lookupLocal(name);
            if (lookup == null) {
                s.addDecl(name, new FuncTSym(this.myType.getType(), myFormalsList.getFormalTypes()));
            } else {
                ErrMsg.fatal(id.getMyLineNum(), id.getMyCharNum(), "Multiply declared identifier");
                ErrMsg.fatalEncountered = true;
            }
            s.addScope();
            myFormalsList.nameAnalysis(s);
            myBody.nameAnalysis(s);
            s.removeScope();
        } catch (DuplicateSymException|EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    public void nameAnalysis(SymTable s, SymTable ...structTable) {
        try {
            IdNode id = this.myId;
            String name = id.getMyStrVal();
            TSym lookup = s.lookupLocal(name);
            if (lookup == null) {
                s.addDecl(name, new TSym(this.myType.getType()));
            } else {
                ErrMsg.fatal(id.getMyLineNum(), id.getMyCharNum(), "Multiply declared identifier");
                ErrMsg.fatalEncountered = true;
            }
        } catch (DuplicateSymException|EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    public TypeNode getMyType() {
        return myType;
    }

    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("};\n");

    }

    public void nameAnalysis(SymTable s, SymTable ...structTable) {
        IdNode id = this.myId;
        String name = id.getMyStrVal();
        
        try {
            TSym lookup = s.lookupLocal(name);
            if (lookup == null) {
                StructTSym tsym = new StructTSym(name, new SymTable());
                s.addDecl(name, tsym);
                myDeclList.nameAnalysis(tsym.getMyStructFields(), s);
            } else {
                ErrMsg.fatal(id.getMyLineNum(), id.getMyCharNum(), "Multiply declared identifier");
                ErrMsg.fatalEncountered = true;
            }
        } catch (DuplicateSymException|EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private IdNode myId;
    private DeclListNode myDeclList;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    abstract public String getType();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public String getType() {
        return "int";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public String getType() {
        return "bool";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public String getType() {
        return "void";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public String getType() {
        return "struct";
    }

    public IdNode getId() {
        return myId;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    abstract public void nameAnalysis(SymTable s);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    public void nameAnalysis(SymTable s) {
        myAssign.nameAnalysis(s);
    }

    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    public void nameAnalysis(SymTable s) {
        myExp.nameAnalysis(s);
    }

    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    public void nameAnalysis(SymTable s) {
        myExp.nameAnalysis(s);
    }

    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalysis(SymTable s) {
        myExp.nameAnalysis(s);
    }

    // 1 child (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    public void nameAnalysis(SymTable s) {
        myExp.nameAnalysis(s);
    }

    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable s) {
        try {
            myExp.nameAnalysis(s);
            s.addScope();
            myDeclList.nameAnalysis(s);
            myStmtList.nameAnalysis(s);
            s.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
        addIndentation(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable s) {
        try {
            myExp.nameAnalysis(s);
            s.addScope();
            myThenDeclList.nameAnalysis(s);
            myThenStmtList.nameAnalysis(s);
            s.removeScope();
            s.addScope();
            myElseDeclList.nameAnalysis(s);
            myElseStmtList.nameAnalysis(s);
            s.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable s) {
        try {
            myExp.nameAnalysis(s);
            s.addScope();
            myDeclList.nameAnalysis(s);
            myStmtList.nameAnalysis(s);
            s.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
	addIndentation(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    public void nameAnalysis(SymTable s) {
        try {
            myExp.nameAnalysis(s);
            s.addScope();
            myDeclList.nameAnalysis(s);
            myStmtList.nameAnalysis(s);
            s.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    public void nameAnalysis(SymTable s) {
        myCall.nameAnalysis(s);
    }

    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    public void nameAnalysis(SymTable s) {
        if (myExp != null) {
            myExp.nameAnalysis(s);
        }
    }

    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    abstract public void nameAnalysis(SymTable s);
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    public void nameAnalysis(SymTable s) {

    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    public void nameAnalysis(SymTable s) {

    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    public void nameAnalysis(SymTable s) {

    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    public void nameAnalysis(SymTable s) {

    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        String out = myStrVal;
        if (myTSym != null) {
            if (myTSym instanceof FuncTSym) {
                String formals = "(";
                Iterator it = ((FuncTSym)myTSym).getMyFormalTypes().iterator();
                try {
                    while (it.hasNext()) {
                        formals += ((String)it.next()) + ",";
                    }
                } catch (NoSuchElementException ex) {
                    System.err.println("unexpected NoSuchElementException in DeclListNode.print");
                    System.exit(-1);
                }
                if (formals.substring(formals.length() - 1).equals(",")) {
                    formals = formals.substring(0, formals.length() - 1);
                }
                formals += "->" + myTSym.getType() + ")";
                out += formals;
            } else if (myTSym instanceof StructTSym) {
                out += "(" + myTSym.getType() + ")";
            } else {
                out += "(" + myTSym + ")";
            }
        }
        p.print(out);
    }

    public void nameAnalysis(SymTable s) {
        try {
            if (myTSym == null) {
                TSym lookup = s.lookupGlobal(myStrVal);
                if (lookup == null) {
                    myTSym = null;
                    ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
                    ErrMsg.fatalEncountered = true;
                } else {
                    myTSym = lookup;
                }
            }
        } catch (EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }

    public int getMyLineNum() {
        return this.myLineNum;
    }

    public int getMyCharNum() {
        return this.myCharNum;
    }

    public String getMyStrVal() {
        return this.myStrVal;
    }

    public void setMyTSym(TSym tsym) {
        myTSym = tsym;
    }

    public TSym getMyTSym() {
        return myTSym;
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private TSym myTSym;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        myId.unparse(p, 0);
    }

    public void nameAnalysis(SymTable s) {
        IdNode i;
        myLoc.nameAnalysis(s);
        if (myLoc instanceof IdNode) {
            i = (IdNode) myLoc;
        } else {
            i = ((DotAccessExpNode)myLoc).getMyId();
        }
        TSym t = i.getMyTSym();

        try {
            if (t instanceof StructTSym) {
                SymTable st = ((StructTSym)t).getMyStructFields();
                TSym lookup = st.lookupGlobal(myId.getMyStrVal());
                myId.setMyTSym(lookup);
                if (lookup == null) {
                    ErrMsg.fatal(myId.getMyLineNum(), myId.getMyCharNum(), "Invalid struct field name");
                    ErrMsg.fatalEncountered = true;
                }
            } else {
                ErrMsg.fatal(i.getMyLineNum(), i.getMyCharNum(), "Dot-access of non-struct type");
                ErrMsg.fatalEncountered = true;
            }
        }  catch (EmptySymTableException ex) {
            System.err.println("Exception occured during name analysis: " + ex);
            System.exit(-1);
        }
    }


    public IdNode getMyId() {
        return myId;
    }

    private ExpNode myLoc;
    private IdNode myId;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        myLhs.nameAnalysis(s);
        myExp.nameAnalysis(s);
    }

    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        myId.nameAnalysis(s);
        if (myExpList != null) {
            myExpList.nameAnalysis(s);
        }
    }

    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTable s) {
        myExp.nameAnalysis(s);
    }

    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    public void nameAnalysis(SymTable s) {
        myExp1.nameAnalysis(s);
        myExp2.nameAnalysis(s);
    }

    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }

    public void nameAnalysis(SymTable s) {
        super.nameAnalysis(s);
    }
}
