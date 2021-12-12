import tokenizer.Tokenizer;
import tokenizer.token.Token;
import visitor.CalcVisitor;
import visitor.ParserVisitor;
import visitor.PrintVisitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Tokenizer tokenizer = new Tokenizer(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        List<Token> tokens = tokenizer.getTokens();
        List<Token> reversePolishNotationTokens = new ParserVisitor(tokens).getReversePolishNotation();
        System.out.print("Reverse polish notation: ");
        new PrintVisitor(reversePolishNotationTokens).print();
        System.out.println("Evaluated result: " + new CalcVisitor(reversePolishNotationTokens).calculate());
    }
}
