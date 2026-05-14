package com.perman.habittracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "habit_logs",
        foreignKeys = @ForeignKey(entity = Habit.class,
                parentColumns = "id",
                childColumns = "habitId",
                onDelete = ForeignKey.CASCADE))
public class HabitLog {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int habitId;
    public String date; // Формат "YYYY-MM-DD"
    public boolean isCompleted;

    // Конструктор
    public HabitLog(int habitId, String date, boolean isCompleted) {
        this.habitId = habitId;
        this.date = date;
        this.isCompleted = isCompleted;
    }
}