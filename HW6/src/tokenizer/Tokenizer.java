package tokenizer;

import tokenizer.state.EndState;
import tokenizer.state.ErrorState;
import tokenizer.state.StartState;
import tokenizer.state.TokenizerState;
import tokenizer.token.Token;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer implements TokenizerContext {
    private final InputStream is;
    private final List<Token> tokens;

    private TokenizerState curState;
    private int curSymbol;
    private int curPos;
    private StringBuilder buffer;


    public Tokenizer(InputStream is) {
        this.is = is;
        this.tokens = new ArrayList<>();
        curPos = 0;
        buffer = new StringBuilder();
        curState = StartState.INSTANCE;
        next();
    }

    public List<Token> getTokens() {
        while (!(curState == EndState.INSTANCE || curState == ErrorState.INSTANCE)) {
            curState.process(curSymbol, this);
        }
        if (curState == EndState.INSTANCE) {
            return tokens;
        }
        throw new RuntimeException("Unexpected Input:" + (char)curSymbol + "on position " + curPos);
    }

    @Override
    public void addToken(Token token) {
        tokens.add(token);
    }

    @Override
    public void setCurState(TokenizerState state) {
        curState = state;
    }

    @Override
    public String PopBuffer() {
        String res = buffer.toString();
        buffer = new StringBuilder();
        return res;
    }

    @Override
    public void addToBuffer(char symbol) {
        buffer.append(symbol);
    }

    @Override
    public int getEndSymbol() {
        return -1;
    }

    @Override
    public void next() {
        curPos++;
        try {
            curSymbol = is.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
