package tokenizer.token;

import visitor.TokenVisitor;

public class Number implements Token{
    private final String stringValue;

    public Number(String value) {
        this.stringValue = value;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
