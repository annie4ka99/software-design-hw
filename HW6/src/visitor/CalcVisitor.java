package visitor;

import tokenizer.token.Brace;
import tokenizer.token.Number;
import tokenizer.token.Operation;
import tokenizer.token.Token;

import java.util.List;
import java.util.Stack;

public class CalcVisitor implements TokenVisitor {
    private final List<Token> tokens;
    private final Stack<Integer> stack;

    public CalcVisitor(List<Token> tokens) {
        this.tokens = tokens;
        this.stack=  new Stack<>();
    }

    public int calculate() {
        if (tokens.size() == 0) {
            throw new IllegalArgumentException("empty input sequence");
        }
        for (Token token : tokens) {
            token.accept(this);
        }
        if (stack.size() != 1) {
            throw new RuntimeException("token sequence is incorrect reverse polish notation");
        }
        return stack.pop();
    }

    @Override
    public void visit(Number token) {
        stack.push(Integer.parseInt(token.getStringValue()));
    }

    @Override
    public void visit(Brace token) {
        throw new RuntimeException("there shouldn't be braces in reverse polish notation");
    }

    @Override
    public void visit(Operation token) {
        if (stack.size() < 2) {
            throw new RuntimeException("token sequence is incorrect reverse polish notation");
        }
        Integer b = stack.pop();
        Integer a = stack.pop();
        stack.push(token.performOperation(a, b));
    }
}
