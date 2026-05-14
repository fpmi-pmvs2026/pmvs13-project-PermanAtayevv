package com.perman.habittracker;

import android.Manifest;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // НОВОЕ ПРАВИЛО: Автоматически разрешаем уведомления, чтобы системный диалог не перекрывал экран
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    // Указываем, с какого экрана (Activity) начинать тест
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testBottomNavigationIsDisplayed() {
        // Проверяем, что нижняя панель навигации (меню) видна на экране
        onView(withId(R.id.bottom_nav_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFabClickOpensDialog() {
        // 1. Находим круглую кнопку "+" и кликаем по ней
        onView(withId(R.id.fab_add_habit))
                .perform(click());

        // 2. Проверяем, что после клика на экране появилось поле ввода названия из диалога
        onView(withId(R.id.edit_habit_name))
                .check(matches(isDisplayed()));
    }
}