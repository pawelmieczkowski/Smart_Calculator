package calculator;

import jdk.jshell.EvalException;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    static Map<String, String> map = new TreeMap<>();


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean exitFlag = false;
        while (!exitFlag) {

            String input;
            if (!(input = scanner.nextLine()).isEmpty()) {
            } else {
                continue;
            }
            if (!validation(input)) {
                continue;
            }

            if (input.matches("/[a-zA-Z]+")) {
                exitFlag = checkCommands(input);
            } else if (input.contains("=")) {
                checkVariable(input);
            } else if (input.matches("[a-zA-Z]+") && !input.contains("[-+=]")) {
                printVariable(input);
            } else if (input.contains("0-9") && !input.contains("[-+=]")) {
                System.out.println(input);
            } else {

                String transformedInput = spaceInput(input);
                String postfixInput = infixToPostfix(transformedInput);
                System.out.println(calculate(postfixInput));
            }
        }

    }


    public static boolean checkCommands(String input) {
        if (input.equals("/exit")) {
            System.out.println("Bye!");
            return true;
        } else if (input.equals(("/help"))) {
            System.out.println("The program calculates the sum of numbers");
        } else if (input.charAt(0) == '/') {
            System.out.println("Unknown command");
        }
        return false;
    }

    public static void checkVariable(String input) {
        String str = input.replaceAll("\\s", "");
        String[] temp = str.split("=");
        if (temp.length > 2) {
            System.out.println("Invalid assignment");
            return;
        }
        if (temp[0].matches(".*[^a-zA-Z].*")) {
            System.out.println("Invalid identifier");
        } else if (temp[1].matches(".*[^\\-0-9].*") && !map.containsKey(temp[1])) {
            System.out.println("Invalid assignment");
        } else {
            if (temp[1].matches("[a-zA-Z]+")) {
                map.put(temp[0], map.get(temp[1]));
            } else {
                map.put(temp[0], temp[1]);
            }
        }
    }

    public static void printVariable(String input) {
        if (map.containsKey(input)) {
            System.out.println(map.get(input));
        } else {
            System.out.println("Unknown variable");
        }
    }

    public static String addition(String sum, String number) {
        BigInteger first = new BigInteger(sum);
        BigInteger second = new BigInteger(number);
        BigInteger result = first.add(second);
        return result.toString();
    }

    public static String subtraction(String sum, String number) {
        BigInteger first = new BigInteger(sum);
        BigInteger second = new BigInteger(number);
        BigInteger result = first.subtract(second);
        return result.toString();
    }

    public static String multiplication(String sum, String number) {
        BigInteger first = new BigInteger(sum);
        BigInteger second = new BigInteger(number);
        BigInteger result = first.multiply(second);
        return result.toString();
    }

    public static String division(String sum, String number) {
        BigInteger first = new BigInteger(sum);
        BigInteger second = new BigInteger(number);
        BigInteger result = first.divide(second);
        return result.toString();
    }

    public static String spaceInput(String input) {
        String result = merge(input);
        result = result.replaceAll(" ", "");
        result = result.replaceAll("\\+", " + ");
        result = result.replaceAll("-", " - ");
        result = result.replaceAll("\\*", " * ");
        result = result.replaceAll("/", " / ");
        result = result.replaceAll("=", " = ");
        result = result.replaceAll("\\(", "( ");
        result = result.replaceAll("\\)", " )");

        return result;
    }

    public static String merge(String input) {
        String result = input.replaceAll(" ", "");
        for (int i = 0; i < result.length(); i++) {
            result = result.replaceAll("\\+\\+", "+");
            result = result.replaceAll("--", "+");
            result = result.replaceAll("\\+-", "-");
        }
        return result;
    }

    public static String infixToPostfix(String input) {
        String[] data = input.split(" ");
        Stack<String> stack = new Stack<>();
        StringBuilder result = new StringBuilder();

        for (String d : data) {
            if (d.matches("[0-9a-zA-Z]+")) {
                result.append(d);
                result.append(" ");
            } else if (d.equals("(")) {
                stack.push(d);
            } else if (d.equals(")")) {
                while (true) {
                    if (!stack.peek().equals("(")) {
                        result.append(stack.pop());
                        result.append(" ");
                    } else {
                        stack.pop();
                        break;
                    }
                }
            } else if (stack.empty() || (stack.peek().equals("("))) {
                stack.push(d);
            } else if ((d.equals("*") || d.equals("/")) && (stack.peek().equals("+") || stack.peek().equals("-"))) {
                stack.push(d);
            } else if ((d.equals("+") || d.equals("-")) && (stack.peek().equals("+") || stack.peek().equals("-") ||
                    stack.peek().equals("*") || stack.peek().equals("/"))) {
                do {
                    result.append(stack.pop());
                    result.append(" ");
                } while (!stack.empty() && (stack.peek().equals("*") || stack.peek().equals("/") || !stack.peek().equals("(")));
                stack.push(d);
            } else if ((d.equals("*") || d.equals("/")) &&
                    (stack.peek().equals("*") || stack.peek().equals("/"))) {
                do {
                    result.append(stack.pop());
                    result.append(" ");
                } while (!stack.empty() && !(stack.peek().equals("-") || !stack.peek().equals("+") || !stack.peek().equals("(")));
                stack.push(d);
            }
        }

        int stackSize = stack.size();
        for (int i = 0; i < stackSize; i++) {
            result.append(stack.pop());
            result.append(" ");
        }
        return result.toString();
    }


    public static BigInteger calculate(String input) {
        String[] arrayInput = input.split(" ");

        for (int i = 0; i < arrayInput.length; i++) {
            String s = arrayInput[i];
            if (s.matches("[a-zA-Z]")) {
                arrayInput[i] = String.valueOf(map.get(s));
            }
        }

        Stack<String> stack = new Stack<>();

        for (String s : arrayInput) {
            if (s.matches("-?[0-9]+")) {
                stack.push(s);
            } else if (!s.equals("")) {
                String second = stack.pop();
                String first = stack.pop();
                if (s.equals("+")) {
                    stack.push(addition(first, second));
                } else if (s.equals("-")) {
                    stack.push(subtraction(first, second));
                } else if (s.equals("*")) {
                    stack.push(multiplication(first, second));
                } else if (s.equals("/")) {
                    stack.push(division(first, second));
                }
            }
        }
        return new BigInteger(stack.pop());
    }

    public static boolean validation(String input) {
        if (input.contains("**")) {
            System.out.println("Invalid expression");
            return false;
        } else if (input.contains("//")) {
            System.out.println("Invalid expression");
            return false;
        }
        int lPar = 0;
        int rPar = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                lPar++;
            } else if (input.charAt(i) == ')') {
                rPar++;
            }
        }
        if (lPar != rPar) {
            System.out.println("Invalid expression");
            return false;
        }
        return true;
    }
}
