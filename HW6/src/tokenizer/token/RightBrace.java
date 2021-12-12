package tokenizer.token;

import visitor.TokenVisitor;

public class RightBrace extends Brace {
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public String toString() {
        return ")";
    }


}
