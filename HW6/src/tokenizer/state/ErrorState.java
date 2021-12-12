package tokenizer.state;

import tokenizer.TokenizerContext;

public class ErrorState implements TokenizerState{
    public static final TokenizerState INSTANCE = new ErrorState();
    @Override
    public void process(int symbol, TokenizerContext context) {
        context.setCurState(INSTANCE);
    }

    private ErrorState(){};
}
