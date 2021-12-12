package visitor;

import tokenizer.token.Brace;
import tokenizer.token.Number;
import tokenizer.token.Operation;
import tokenizer.token.Token;

import java.util.List;

public class PrintVisitor implements TokenVisitor{
    private final List<Token> tokens;

    public PrintVisitor(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void print() {
        for (Token token : tokens) {
            token.accept(this);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public void visit(Number token) {
        System.out.print(token);
    }

    @Override
    public void visit(Brace token) {
        System.out.print(token);
    }

    @Override
    public void visit(Operation token) {
        System.out.print(token);
    }
}
