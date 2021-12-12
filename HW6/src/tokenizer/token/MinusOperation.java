package tokenizer.token;

import visitor.TokenVisitor;

public class MinusOperation extends Operation {
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getPriority() {
        return BINARY_SECOND_PRIORITY;
    }

    @Override
    public String toString() {
        return "-";
    }

    @Override
    public int performOperation(int a, int b) {
        return a - b;
    }
}
