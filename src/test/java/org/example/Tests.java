package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для тестирования функционала методов класса ExpressionSolver
 * @author ablakanovamin0-svg
 * @version 1.0
 */
public class Tests {

    /**
     * Проверка работы метода evaluate для вычисления простых выражений
     */
    @Test
    void testingSimpleExpressions() {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        try {
            assertEquals(3, expressionSolver.evaluate("1 + 2"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(2 - 1.5, expressionSolver.evaluate("2 - 1.5"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(3.5 /  2.99, expressionSolver.evaluate("3.5 /  2.99"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(5 * 26.5, expressionSolver.evaluate("5 * 26.5"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Проверка работы метода evaluate для вычисления выражений с поддерживаемыми в данном классе функциями
     */
    @Test
    void testingFunctions() {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        try {
            assertEquals(Math.sin(2.564), expressionSolver.evaluate("sin(2.564)"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(Math.cos(-0.44), expressionSolver.evaluate("cos(0.44)"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Проверка работы метода evaluate в случае задания пользователем значений переменных
     */
    @Test
    void testingVaribales() {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        expressionSolver.setVariable("x", 3.14);
        expressionSolver.setVariable("y", 2.0);
        try {
            assertEquals(Math.pow(Math.sin(3.14), 2) + Math.cos(2.0),  expressionSolver.evaluate("sin(x)^2 + cos(y)"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Проверка работы метода evaluate для вычисления сложных выражений
     */
    @Test
    void testingComplexExpressions() {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        try {
            assertEquals(-2 + Math.pow(6.7, 2) / Math.cos(2 + Math.sin(5) * 7 / 5.5), expressionSolver.evaluate("-2 + 6.7^2 / cos(2 + sin(5) * 7 / 5.5)"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     */
    @Test
    void testingExpressions() {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        try {
            assertThrows(ArithmeticException.class, () -> {expressionSolver.evaluate("5 / 0");});
        } catch (Exception e) {

        }
        try {
            assertThrows(Exception.class, () -> {expressionSolver.evaluate("si(5) + 6");});
        } catch (Exception e) {

        }
        try {
            assertThrows(Exception.class, () -> {expressionSolver.evaluate("++ 1 - 7");});
        } catch (Exception e) {

        }
        try {
            assertThrows(Exception.class, () -> {expressionSolver.evaluate("(1 + 7");});
        } catch (Exception e) {

        }
        try {
            assertThrows(Exception.class, () -> {expressionSolver.evaluate("(1 - 7) * 8.9)");});
        } catch (Exception e) {

        }
        try {
            assertThrows(Exception.class, () -> {expressionSolver.evaluate("(cos(65) + 7.67^2) * 8..9)");});
        } catch (Exception e) {

        }
    }
}
