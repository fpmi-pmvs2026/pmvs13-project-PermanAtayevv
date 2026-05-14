package com.perman.habittracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.perman.habittracker.R;
import com.perman.habittracker.data.AppDatabase;
import com.perman.habittracker.data.Habit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsFragment extends Fragment {

    private AppDatabase db;
    private ExecutorService executorService;
    private TextView textTotalHabits;
    private TextView textCompletedToday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

        db = AppDatabase.getInstance(getContext());
        executorService = Executors.newSingleThreadExecutor();

        textTotalHabits = root.findViewById(R.id.text_total_habits);
        textCompletedToday = root.findViewById(R.id.text_completed_today);

        // Каждый раз при открытии экрана загружаем свежую статистику
        loadStatistics();

        return root;
    }

    private void loadStatistics() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 1. Считаем, сколько всего привычек создал пользователь
                List<Habit> allHabits = db.habitDao().getAllHabits();
                int totalHabits = allHabits.size();

                // 2. Считаем, сколько привычек выполнено именно сегодня
                List<Integer> completedIds = db.habitDao().getCompletedHabitIdsForToday(today);
                int completedToday = completedIds.size();

                // Возвращаемся в UI-поток, чтобы показать цифры на экране
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textTotalHabits.setText(String.valueOf(totalHabits));
                            textCompletedToday.setText(String.valueOf(completedToday));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}