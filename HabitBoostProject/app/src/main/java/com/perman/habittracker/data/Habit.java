package com.perman.habittracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public String createdAt;

    // Конструктор
    public Habit(String name, String description, String createdAt) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
}