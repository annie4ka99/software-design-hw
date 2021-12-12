package tokenizer.token;

import visitor.TokenVisitor;

public interface Token {
    public void accept(TokenVisitor visitor);
}



