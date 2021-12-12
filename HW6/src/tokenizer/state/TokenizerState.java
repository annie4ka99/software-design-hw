package tokenizer.state;

import tokenizer.TokenizerContext;

public interface TokenizerState {
    void process(int symbol, TokenizerContext context);
}
