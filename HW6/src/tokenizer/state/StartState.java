package tokenizer.state;

import tokenizer.TokenizerContext;
import tokenizer.token.*;

public class StartState implements TokenizerState {
    public static final TokenizerState INSTANCE = new StartState();

    private StartState(){};
    @Override
    public void process(int symbol, TokenizerContext context){
        switch (symbol) {
            case '+':
                context.addToken(new PlusOperation());
                break;
            case '-':
                context.addToken(new MinusOperation());
                break;
            case '/':
                context.addToken(new DivOperation());
                break;
            case '*':
                context.addToken(new MulOperation());
                break;
            case '(':
                context.addToken(new LeftBrace());
                break;
            case ')':
                context.addToken(new RightBrace());
                break;
            default:
                if (symbol == context.getEndSymbol()) {
                    context.setCurState(EndState.INSTANCE);
                    return;
                }
                char charSymbol = (char)symbol;
                if (Character.isDigit(charSymbol)) {
                    context.setCurState(NumberState.INSTANCE);
                    return;
                }
                if (!Character.isWhitespace((char)symbol)) {
                    context.setCurState(ErrorState.INSTANCE);
                    return;
                }
        }
        context.next();
        context.setCurState(INSTANCE);
    }
}
