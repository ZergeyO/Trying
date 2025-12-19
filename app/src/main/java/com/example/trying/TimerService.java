package com.example.trying;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;

public class TimerService extends Service {

    private static List<Student> students = new ArrayList<>();
    private static MainActivity activity;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        startTimerUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            int studentId = intent.getIntExtra("studentId", -1);

            if (command != null) {
                switch (command) {
                    case "start":
                        startStudentTimer(studentId);
                        break;
                    case "pause":
                        pauseStudentTimer(studentId);
                        break;
                    case "stop":
                        stopStudentTimer(studentId);
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void addStudent(Student student) {
        students.add(student);
    }

    public static void removeStudent(int studentId){
        students.remove(studentId);
    }

    // Получаем студента по ID
    public static Student getStudent(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    // Управление таймерами
    private void startStudentTimer(int studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            student.startTimer();
        }
    }

    private void pauseStudentTimer(int studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            student.pauseTimer();
            updateActivity(studentId, student.getCurrentTime());
        }
    }

    private void stopStudentTimer(int studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            student.stopTimer();
            updateActivity(studentId, 0);
        }
    }

    // Запускаем обновление таймеров
    private void startTimerUpdates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Обновляем всех запущенных студентов
                for (Student student : students) {
                    if (student.isRunning() && activity != null) {
                        updateActivity(student.getId(), student.getCurrentTime());
                    }
                }
                handler.postDelayed(this, 100);
            }
        });
    }

    // Обновляем активность
    private void updateActivity(int studentId, long time) {
        if (activity != null) {
            activity.updateTimerUI(studentId, time);
        }
    }

    // Устанавливаем активность
    public static void setActivity(MainActivity act) {
        activity = act;
    }



    // Очищаем ссылку
    public static void clearActivity() {
        activity = null;
    }
}