package visitor;

import tokenizer.token.Brace;
import tokenizer.token.Number;
import tokenizer.token.Operation;

public interface TokenVisitor {
    void visit(Number token);
    void visit(Brace token);
    void visit(Operation token);
}
