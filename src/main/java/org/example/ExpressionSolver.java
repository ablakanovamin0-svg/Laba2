package org.example;

import java.util.*;

/**
 * Класс для вычисления выражений
 * @author ablakanovamin0-svg
 * @version 1.0
 */
public class ExpressionSolver {

    private final Map<String, Double> variables = new HashMap<>();

    /**
     * @param expression строка с математическим выражением
     * @return значение математического выражения
     * @throws Exception, если не удалось разобрать выражение
     */
    public double evaluate(String expression) throws Exception {
        Set<String> usedVariables = new HashSet<>();
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        Character prevChar = null;

        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (Character.isWhitespace(currentChar)) continue;

            if (Character.isDigit(currentChar) || currentChar == '.') {
                StringBuilder number = new StringBuilder();
                boolean dotUsed = false;

                while (i < expression.length()) {
                    char c = expression.charAt(i);
                    if (Character.isDigit(c)) {
                        number.append(c);
                        i++;
                    } else if (c == '.' && !dotUsed) {
                        number.append('.');
                        dotUsed = true;
                        i++;
                    } else break;
                }
                i--;
                values.push(Double.parseDouble(number.toString()));
                prevChar = 'n'; // число
                continue;
            }

            if (currentChar == '-') {
                if (prevChar == null || prevChar == '(' || isOperator(prevChar)) {
                    values.push(0.0);
                    operators.push('-');
                    prevChar = '-';
                    continue;
                }
                processOperators(operators, values, currentChar);
                prevChar = '-';
                continue;
            }

            if (isOperator(currentChar)) {
                processOperators(operators, values, currentChar);
                prevChar = currentChar;
                continue;
            }

            if (currentChar == '(') {
                operators.push('(');
                prevChar = '(';
                continue;
            }
            if (currentChar == ')') {
                closeParentheses(operators, values);
                prevChar = ')';
                continue;
            }

            if (Character.isLetter(currentChar)) {
                StringBuilder nameBuilder = new StringBuilder();
                while (i < expression.length() && Character.isLetterOrDigit(expression.charAt(i))) {
                    nameBuilder.append(expression.charAt(i++));
                }
                int j = i;
                while (j < expression.length() && Character.isWhitespace(expression.charAt(j))) j++;
                String token;
                if (j < expression.length() && expression.charAt(j) == '(') {
                    StringBuilder funcBuilder = new StringBuilder();
                    funcBuilder.append(nameBuilder); // имя функции
                    int depth = 0;
                    int k = j;
                    boolean closed = false;
                    while (k < expression.length()) {
                        char c = expression.charAt(k);
                        funcBuilder.append(c);
                        if (c == '(') depth++;
                        else if (c == ')') {
                            depth--;
                            if (depth == 0) {
                                closed = true;
                                k++;
                                break;
                            }
                        }
                        k++;
                    }
                    if (!closed) {
                        throw new Exception("Ошибка: незакрытая скобка в вызове функции после '" + nameBuilder + "'");
                    }
                    token = funcBuilder.toString();
                    i = k - 1;
                } else {
                    token = nameBuilder.toString();
                    i = i - 1;
                }
                if (!token.contains("(")) {
                    usedVariables.add(token);
                }
                values.push(parseNumberOrFunction(token));
                prevChar = 'n';
                continue;
            }
            throw new Exception("Неизвестный символ в выражении: '" + currentChar + "'");
        }
        while (!operators.empty()) {
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));
        }
        for (String var : usedVariables) {
            if (!variables.containsKey(var)) {
                promptForVariableValue(var);
            }
        }
        if (values.isEmpty()) throw new Exception("Пустой результат выражения");
        return values.pop();
    }

    /**
     * Установление значения переменной
     * @param name имя переменной
     * @param value значение переменной
     */
    public void setVariable(String name, double value) {
        variables.put(name, value);
    }

    /**
     * Получение значения переменной
     * @param name имя переменной
     * @return значение переменной
     */
    public double getVariable(String name) {
        return variables.get(name);
    }

    /**
     * Удаление пременной
     * @param name имя переменной
     */
    public void removeVariable(String name) {
        variables.remove(name);
    }

    /**
     * Получение списка всех известных переменных
     * @return список всех известных переменных
     */
    public List<String> listVariables() {
        return new ArrayList<>(variables.keySet());
    }

    /**
     * Получение значения переменной от пользователя
     * @param variableName имя пременной
     */
    private void promptForVariableValue(String variableName) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите значение для переменной '" + variableName + "': ");
        variables.put(variableName, scanner.nextDouble());
    }

    /**
     * Преобразование заданной переменной или функции в число
     * @param token строка, которую необходимо преобразовать в число
     * @return получившеес число
     * @throws Exception, если не преобразовать заданную строку в число
     */
    private double parseNumberOrFunction(String token) throws Exception {
        token = token.trim();
        if (token.matches("-?(?:\\d+\\.\\d*|\\.\\d+|\\d+)")) {
            return Double.parseDouble(token);
        }
        if (!token.contains("(")) {
            if (variables.containsKey(token)) {
                return variables.get(token);
            } else {
                throw new Exception("Не определена переменная: " + token);
            }
        }
        if (token.startsWith("sin(") && token.endsWith(")")) {
            String inner = token.substring(4, token.length() - 1);
            double innerVal = evaluate(inner);
            return Math.sin(innerVal);
        }
        if (token.startsWith("cos(") && token.endsWith(")")) {
            String inner = token.substring(4, token.length() - 1);
            double innerVal = evaluate(inner);
            return Math.cos(innerVal);
        }

        throw new Exception("Неизвестный токен: " + token);
    }

    /**
     * Применение операции над двумя значениями
     * @param b второе число
     * @param operator операция, которое будет проведена над двумя числами
     * @param a первое число
     * @return результат выолненной операции
     * @throws ArithmeticException, если в процессе вычисления выражения происходит деление на нуль
     * @throws IllegalArgumentException, если в заданной пользователем строке введён некорректный оператор
     */
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

    /**
     * Проверяет, является ли символ оператором
     * @param ch символ, проверяемый на принадлежность к поддерживаемым операторам
     * @return true, если символ - оператор, иначе возвращает false
     */
    private boolean isOperator(char ch) {
        return ch == '+' || ch == '*' || ch == '/' || ch == '^';
    }

    /**
     * Определение приоритета оператора
     * @param op символ оператора
     * @return приоритет заданного оператора
     */
    private int precedence(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> 0;
        };
    }

    /**
     * Обработка последовательности операторов согласно применению правил приоритета
     * @param operators стек операторов
     * @param values стек чисел
     * @param currentOp символ текущего оператора
     * @throws Exception, если в процессе вычисления выражения происходит деление на нуль или
     * если в заданной пользователем строке введён некорректный оператор
     */
    private void processOperators(Stack<Character> operators, Stack<Double> values, char currentOp) throws Exception {
        while (!operators.empty() && precedence(operators.peek()) >= precedence(currentOp)) {
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));
        }
        operators.push(currentOp);
    }

    /**
     * Закрытие скобочной структуры с выполнением необходимых операций
     * @param operators стек операторов
     * @param values стек чисел
     * @throws Exception, если в скобочной структуре имеется ошибка
     */
    private void closeParentheses(Stack<Character> operators, Stack<Double> values) throws Exception {
        while (!operators.empty() && operators.peek() != '(') {
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));
        }

        if (operators.empty()) {
            throw new Exception("Ошибка: нет соответствующей открывающей скобки '('");
        }
        operators.pop();
    }
}