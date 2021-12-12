package tokenizer.state;

import tokenizer.TokenizerContext;

public class EndState implements TokenizerState{
    public static final TokenizerState INSTANCE = new EndState();
    @Override
    public void process(int symbol, TokenizerContext context){
        context.setCurState(INSTANCE);
    }

    private EndState(){};
}
