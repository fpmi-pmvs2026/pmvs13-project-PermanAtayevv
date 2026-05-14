package com.perman.habittracker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perman.habittracker.R;
import com.perman.habittracker.data.Habit;

import java.util.ArrayList;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList = new ArrayList<>();
    private List<Integer> completedHabitIds = new ArrayList<>(); // Список ID выполненных привычек

    public interface OnHabitCheckedListener {
        void onHabitChecked(Habit habit, boolean isChecked);
    }
    private OnHabitCheckedListener listener;

    public void setOnHabitCheckedListener(OnHabitCheckedListener listener) {
        this.listener = listener;
    }

    public void setHabits(List<Habit> habits) {
        this.habitList = habits;
        notifyDataSetChanged();
    }

    // НОВЫЙ МЕТОД: Передаем адаптеру список выполненных сегодня привычек
    public void setCompletedHabitIds(List<Integer> completedIds) {
        this.completedHabitIds = completedIds;
        notifyDataSetChanged();
    }

    public Habit getHabitAt(int position) {
        return habitList.get(position);
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.textName.setText(habit.name);

        if (habit.description != null && !habit.description.isEmpty()) {
            holder.textDesc.setText(habit.description);
            holder.textDesc.setVisibility(View.VISIBLE);
        } else {
            holder.textDesc.setVisibility(View.GONE);
        }

        // Отключаем слушатель, чтобы не было ложных срабатываний при прокрутке
        holder.checkBox.setOnCheckedChangeListener(null);

        // НОВАЯ ЛОГИКА: Ставим галочку, если ID этой привычки есть в списке выполненных
        holder.checkBox.setChecked(completedHabitIds.contains(habit.id));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onHabitChecked(habit, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDesc;
        CheckBox checkBox;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_habit_name);
            textDesc = itemView.findViewById(R.id.text_habit_desc);
            checkBox = itemView.findViewById(R.id.checkbox_completed);
        }
    }
}