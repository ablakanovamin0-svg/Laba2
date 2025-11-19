package org.example;

import java.util.List;

/**
 * Класс для показа функционала объекта созданного класса ExpressionSolver
 * @author ablakanovamin0-svg
 * @version 1.0
 */
public class Main {
    /**
     * Показ функционала методов класса ExpressionSolver
     */
    static void main() {
        ExpressionSolver expressionSolver = new ExpressionSolver();

        // Установка начальных значений переменных
        expressionSolver.setVariable("x", 3.14);
        expressionSolver.setVariable("y", 2.0);

        try {
            double result = expressionSolver.evaluate("sin(x)^2 + cos(y)");
            System.out.println("Результат: " + result);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // Изменение переменной и повторный расчёт
        expressionSolver.setVariable("x", 1.57);
        try {
            double updatedResult = expressionSolver.evaluate("sin(x)^2 + cos(y)");
            System.out.println("Новый результат: " + updatedResult);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // Показываем доступные переменные
        List<String> vars = expressionSolver.listVariables();
        System.out.println("Переменные: " + vars);
    }
}
