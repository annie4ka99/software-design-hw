package visitor;

import tokenizer.token.*;
import tokenizer.token.Number;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserVisitor implements TokenVisitor {
    private final List<Token> inputTokens;
    private final List<Token> outputTokens;
    private final Stack<Token> tokenStack;

    public ParserVisitor(List<Token> tokens) {
        this.inputTokens = tokens;
        this.outputTokens = new ArrayList<>();
        this.tokenStack = new Stack<>();
    }

    public List<Token> getReversePolishNotation() {
        for (Token token : inputTokens) {
            token.accept(this);
        }
        while (!tokenStack.isEmpty()) {
            Token token = tokenStack.pop();
            if (!(token instanceof Operation)) {
                throw new RuntimeException("Incorrect input sequence");
            }
            outputTokens.add(token);
        }
        return outputTokens;
    }

    @Override
    public void visit(Number token) {
        outputTokens.add(token);
    }

    @Override
    public void visit(Brace braceToken) {
        if (braceToken.isOpen()) {
            tokenStack.push(braceToken);
        } else {
            Token token = null;
            while (!tokenStack.isEmpty() &&
                    !((token = tokenStack.pop()) instanceof Brace && ((Brace)token).isOpen())) {
                outputTokens.add(token);
            }
            if (!(token instanceof LeftBrace)) {
                throw new RuntimeException("Incorrect input sequence");
            }
        }
    }

    @Override
    public void visit(Operation operationToken) {
        Token token;
        while (!tokenStack.isEmpty() && (token = tokenStack.peek()) instanceof Operation &&
                ((Operation)token).getPriority() <= operationToken.getPriority()) {
            outputTokens.add(tokenStack.pop());
        }
        tokenStack.push(operationToken);
    }
}
