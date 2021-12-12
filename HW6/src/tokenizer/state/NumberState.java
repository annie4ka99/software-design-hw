package tokenizer.state;
import tokenizer.TokenizerContext;
import tokenizer.token.Number;
;

public class NumberState implements TokenizerState{
    public static final TokenizerState INSTANCE = new NumberState();

    @Override
    public void process(int symbol, TokenizerContext context){
        if (Character.isDigit(symbol)) {
            context.addToBuffer((char)symbol);
            context.next();
            context.setCurState(INSTANCE);
        } else {
            String buffer = context.PopBuffer();
            context.addToken(new Number(buffer));
            context.setCurState(StartState.INSTANCE);
        }
    }
    private NumberState(){};
}
