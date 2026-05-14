package com.perman.habittracker;

import org.junit.Test;
import static org.junit.Assert.*;

import com.perman.habittracker.data.Habit;

public class HabitUnitTest {

    @Test
    public void habitCreation_isCorrect() {
        // 1. Создаем тестовые данные (Привычку)
        String testName = "Чтение книги";
        String testDesc = "Читать 20 страниц перед сном";
        String testDate = "2026-05-14";

        Habit habit = new Habit(testName, testDesc, testDate);

        // 2. Проверяем (Assert), что данные внутри объекта совпадают с тем, что мы передали
        assertEquals("Название привычки должно совпадать", testName, habit.name);
        assertEquals("Описание привычки должно совпадать", testDesc, habit.description);
        assertEquals("Дата создания должна совпадать", testDate, habit.createdAt);
    }
}