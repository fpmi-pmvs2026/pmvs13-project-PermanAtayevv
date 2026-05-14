package com.perman.habittracker.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.perman.habittracker.R;
import com.perman.habittracker.data.AppDatabase;
import com.perman.habittracker.data.Habit;
import com.perman.habittracker.network.Quote;
import com.perman.habittracker.network.QuoteApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private AppDatabase db;
    private ExecutorService executorService;

    private RecyclerView recyclerView;
    private HabitAdapter adapter;
    private TextView textQuote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = AppDatabase.getInstance(getContext());
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = root.findViewById(R.id.recycler_view_habits);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HabitAdapter();

        // ---- ДОБАВЛЕННЫЙ БЛОК КОДА ДЛЯ ГАЛОЧКИ ----
        adapter.setOnHabitCheckedListener(new HabitAdapter.OnHabitCheckedListener() {
            @Override
            public void onHabitChecked(Habit habit, boolean isChecked) {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Сохраняем отметку в базу данных в фоновом потоке
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        com.perman.habittracker.data.HabitLog log = new com.perman.habittracker.data.HabitLog(habit.id, today, isChecked);
                        db.habitDao().insertLog(log);

                        if (isChecked && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Отлично! Привычка выполнена.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        // ---- КОНЕЦ ДОБАВЛЕННОГО БЛОКА ----

        recyclerView.setAdapter(adapter);

        // Находим текстовое поле для цитаты
        textQuote = root.findViewById(R.id.text_quote);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Habit habitToDelete = adapter.getHabitAt(position);
                deleteHabit(habitToDelete);
            }
        }).attachToRecyclerView(recyclerView);

        // Загружаем данные
        loadHabits();
        loadQuote(); // Скачиваем цитату!

        FloatingActionButton fabAddHabit = root.findViewById(R.id.fab_add_habit);
        fabAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHabitDialog();
            }
        });

        return root;
    }

    // Загрузка случайной цитаты из интернета
    private void loadQuote() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.forismatic.com/") // Изменили базовый адрес
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuoteApi quoteApi = retrofit.create(QuoteApi.class);
        quoteApi.getRandomQuote().enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String quoteText = response.body().quote.trim();
                    String authorText = response.body().author;

                    // Иногда сервис присылает цитаты без автора
                    if (authorText == null || authorText.trim().isEmpty()) {
                        authorText = "Неизвестный автор";
                    } else {
                        authorText = authorText.trim();
                    }

                    textQuote.setText("\"" + quoteText + "\"\n— " + authorText);
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                textQuote.setText("Сегодня отличный день для новых свершений!");
            }
        });
    }

    private void loadHabits() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Habit> habits = db.habitDao().getAllHabits();
                // Запрашиваем из базы: какие привычки выполнены сегодня?
                List<Integer> completedIds = db.habitDao().getCompletedHabitIdsForToday(today);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setHabits(habits);
                            adapter.setCompletedHabitIds(completedIds); // Передаем галочки в адаптер
                        }
                    });
                }
            }
        });
    }

    private void deleteHabit(Habit habit) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                db.habitDao().deleteHabit(habit);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Привычка удалена!", Toast.LENGTH_SHORT).show();
                            loadHabits();
                        }
                    });
                }
            }
        });
    }

    private void showAddHabitDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_habit, null);
        EditText editName = dialogView.findViewById(R.id.edit_habit_name);
        EditText editDesc = dialogView.findViewById(R.id.edit_habit_desc);

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editName.getText().toString().trim();
                        String desc = editDesc.getText().toString().trim();

                        if (name.isEmpty()) {
                            Toast.makeText(getContext(), "Название не может быть пустым!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveHabitToDatabase(name, desc);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void saveHabitToDatabase(String name, String desc) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Habit newHabit = new Habit(name, desc, currentDate);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                db.habitDao().insertHabit(newHabit);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Привычка добавлена!", Toast.LENGTH_SHORT).show();
                            loadHabits();
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