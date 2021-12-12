package tokenizer.token;

import visitor.TokenVisitor;

public class LeftBrace extends Brace {
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public String toString() {
        return "(";
    }


}
