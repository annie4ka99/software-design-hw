package tokenizer.token;

import visitor.TokenVisitor;

public abstract class Operation implements Token{
    protected static final int BINARY_FIRST_PRIORITY = 2;
    protected static final int BINARY_SECOND_PRIORITY = 3;

    @Override
    public abstract void accept(TokenVisitor visitor);

    public abstract int getPriority();

    @Override
    public abstract String toString();

    public abstract int performOperation(int a, int b);
}
