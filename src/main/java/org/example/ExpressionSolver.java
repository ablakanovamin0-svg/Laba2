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
            } else if (isOperator(currentChar)) { // Обрабатываем операторы
                processOperators(operators, values, currentChar);
            } else if (currentChar == '(') { // Открывающая скобка
                operators.push(currentChar);
            } else if (currentChar == ')') { // Закрывающая скобка
                closeParentheses(operators, values);
            } else if (Character.isLetter(currentChar)) { // Обрабатываем имена переменных и функций
                StringBuilder identifier = new StringBuilder();

                while ((i < expression.length()) && Character.isAlphabetic(expression.charAt(i)))
                    identifier.append(expression.charAt(i++));
                i--;

                String id = identifier.toString();
                usedVariables.add(id);

                values.push(parseNumberOrFunction(id));
            }
        }

        // Применяем оставшиеся операторы
        while (!operators.empty())
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));

        // Просим ввести значение для ещё не заданных переменных
        for (String var : usedVariables) {
            if (!variables.containsKey(var)) {
                promptForVariableValue(var);
            }
        }

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
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    /**
     * Определение приоритета оператора
     * @param op символ оператора
     * @return приоритет заданного оператора
     */
    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        if (op == '^') return 3;
        return 0;
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
        while (!operators.empty() && precedence(operators.peek()) >= precedence(currentOp))
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));
        operators.push(currentOp);
    }

    /**
     * Закрытие скобочной структуры с выполнением необходимых операций
     * @param operators стек операторов
     * @param values стек чисел
     * @throws Exception, если в скобочной структуре имеется ошибка
     */
    private void closeParentheses(Stack<Character> operators, Stack<Double> values) throws Exception {
        while (!operators.empty() && operators.peek() != '(')
            values.push(applyOperation(values.pop(), operators.pop(), values.pop()));

        if (!operators.empty())
            operators.pop();
        else
            throw new Exception("Ошибка в скобочной структуре.");
    }
}