package tokenizer.token;

import visitor.TokenVisitor;

public abstract class Brace implements Token {
    @Override
    public abstract void accept(TokenVisitor visitor);

    public abstract boolean isOpen();

    @Override
    public abstract String toString();
}
