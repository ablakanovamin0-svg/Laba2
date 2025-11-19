package org.example;

import java.util.*;

public class ExpressionSolver {

    private final Map<String, Double> variables = new HashMap<>();

    public double evaluate(String expression) throws Exception {
        Set<String> usedVariables = new HashSet<>(); // Набор используемых переменных
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (Character.isWhitespace(currentChar)) continue;

            if (Character.isDigit(currentChar)) { // Обрабатываем числа
                StringBuilder number = new StringBuilder();

                while ((i < expression.length()) && Character.isDigit(expression.charAt(i)))
                    number.append(expression.charAt(i++));
                i--;

                values.push(Double.parseDouble(number.toString()));
            } else if (isOperator(currentChar)) {
                processOperators(operators, values, currentChar);
            } else if (currentChar == '(') {
                operators.push(currentChar);
            } else if (currentChar == ')') {
                closeParentheses(operators, values);
            } else if (Character.isLetter(currentChar)) {
                StringBuilder identifier = new StringBuilder();

                while ((i < expression.length()) && Character.isAlphabetic(expression.charAt(i)))
                    identifier.append(expression.charAt(i++));
                i--;

                String id = identifier.toString();
                usedVariables.add(id);

                values.push(parseNumberOrFunction(id));
            }
        }

        while (!operators.empty())
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));

        for (String var : usedVariables) {
            if (!variables.containsKey(var)) {
                promptForVariableValue(var);
            }
        }

        return values.pop();
    }

    public void setVariable(String name, double value) {
        variables.put(name, value);
    }

    public double getVariable(String name) {
        return variables.get(name);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    public List<String> listVariables() {
        return new ArrayList<>(variables.keySet());
    }

    private void promptForVariableValue(String variableName) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите значение для переменной '" + variableName + "': ");
        variables.put(variableName, scanner.nextDouble());
    }

    private double parseNumberOrFunction(String token) throws Exception {
        if (token.matches("-?\\d+(?:\\.\\d+)?")) { // Простое число
            return Double.parseDouble(token);
        } else if (variables.containsKey(token)) { // Значение переменной
            return variables.get(token);
        } else if (token.startsWith("sin") || token.startsWith("cos")) { // Тригонометрические функции
            String varName = token.substring(3, token.length()-1); // Извлекаем аргумент функции
            double value = variables.get(varName);
            return token.startsWith("sin") ? Math.sin(value) : Math.cos(value);
        } else {
            throw new Exception("Не определён элемент: " + token);
        }
    }

    private double applyOperation(double b, char operator, double a) throws Exception {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Деление на ноль!");
                return a / b;
            case '^': return Math.pow(a, b);
            default: throw new IllegalArgumentException("Некорректный оператор: " + operator);
        }
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        if (op == '^') return 3;
        return 0;
    }

    private void processOperators(Stack<Character> operators, Stack<Double> values, char currentOp) throws Exception {
        while (!operators.empty() && precedence(operators.peek()) >= precedence(currentOp))
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));
        operators.push(currentOp);
    }

    private void closeParentheses(Stack<Character> operators, Stack<Double> values) throws Exception {
        while (!operators.empty() && operators.peek() != '(')
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));

        if (!operators.empty())
            operators.pop();
        else
            throw new Exception("Ошибка в скобочной структуре.");
    }
    }
}