package com.perman.habittracker.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {

    // Методы для привычек
    @Insert
    void insertHabit(Habit habit);

    @Update
    void updateHabit(Habit habit);

    @Delete
    void deleteHabit(Habit habit);

    @Query("SELECT * FROM habits")
    List<Habit> getAllHabits();

    // Методы для отметок выполнения
    @Insert
    void insertLog(HabitLog log);

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    List<HabitLog> getLogsByDate(String date);

    // Получить ID всех привычек, выполненных в конкретный день
    @Query("SELECT habitId FROM habit_logs WHERE date = :date AND isCompleted = 1")
    List<Integer> getCompletedHabitIdsForToday(String date);
}