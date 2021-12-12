package tokenizer;

import tokenizer.state.TokenizerState;
import tokenizer.token.Token;

public interface TokenizerContext {
    void addToken(Token token);

    void setCurState(TokenizerState state);

    String PopBuffer();

    void addToBuffer(char symbol);

    int getEndSymbol();

    void next();
}
