package com.example.trying;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;

public class TimerService extends Service {

    /*Кратко о потоках и сопутствующем:
      Поток (Thread) - отдельная линия выполнения внутри программы.
            При этом у каждого потока есть свой стек и свои локальные переменные, поэтому он может выполнять задачи независимо от других.
            Это позволяет программе делать несколько вещей одновременно.
      Сообщение (Message) - объект, по которому определяется указание для выполнения у потока. Создать
            сообщение через конструктор нельзя.
      Петля (Looper) - Бесконечный цикл СООБЩЕНИЙ (Message), крутящихся внутри потока. Лупер управляет
             сообщениями, их очередью, приоритетами и выполнением. У каждого потока может быть только один Лупер.
      Обработчик (Handler) - Прослойка между Лупером и очередью сообщений. Только Hadler имеет возможность добавлять
              сообщения (а, следственно, указания системе) в очередь сообщений в Лупере. Handler привязывается к Луперу и
              может оборачивать переменные типа Runnable в сообщения, для добавления в очередь (т.е. Handler
              способен создавать сообщение)
      Runnable - класс Java. Интерфейс, то есть имеет один единственный ВИРТУАЛЬНЫЙ метод run()
              который мы обязуемся перегрузить. Особенностью данного интерфейса является возможность добавлять
              объекты данного класса в поток.*/

    private static List<Student> students = new ArrayList<>(); //Список студентов
    private static MainActivity activity; //Активность, где происходит обновление таймера в списке
    private Handler handler; //Создание Handler объекта

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper()); //getMainLooper() возвращает лупер, привязанный к главному потоку приложения
        /*Созданный хендлер привзывается к луперу ГЛАВНОГО потока, то есть к пользовательскому интерфейсу.*/
        startTimerUpdates(); //Запуск обновления таймеров сразу после вызова
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) { //Сервису передаётся команда через некоторый Intent
            String command = intent.getStringExtra("command"); //Определение команды запуска
            int studentId = intent.getIntExtra("studentId", -1); //Определение положения студента в списке. Без номера - -1, следствеенно, отсутствие в списке и отсутсвие обработки

            if (command != null) {
                switch (command) { //Определение параметра запуска и следующих действий
                    case "start":
                        startStudentTimer(studentId);
                        break; //запуск
                    case "pause":
                        pauseStudentTimer(studentId);
                        break; //пауза
                    case "stop":
                        stopStudentTimer(studentId);
                        break; //остановка таймера
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    } //Команда привязки сервиса. Так как наш Сервис не привязывается, функция просто возвращает нуль

    public static void addStudent(Student student) {
        students.add(student);
    } //Добавление студента в локальный список студентов Сервиса

    public static Student getStudent(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    } //Получение объекта студента по уникальному номеру

    private void startStudentTimer(int studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            student.startTimer(); //Запуск таймера у конкретного студента. См. Student
        }
    }

    private void pauseStudentTimer(int studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            student.pauseTimer();//Пауза таймера у конкретного студента. См. Student
            updateActivity(studentId, student.getCurrentTime()); //Обновление данных в MainActivity
        }
    }

    private void stopStudentTimer(int studentId) {
        Student student = getStudent(studentId);
        if (student != null) {
            student.stopTimer();//Стоп таймера у конкретного студента. См. Student
            updateActivity(studentId, 0); //Обновление данных в MainActivity
        }
    }

    private void startTimerUpdates() { //Метод, запускающий бесконечный (до стопа программы) обновление таймеров
        handler.post(new Runnable() { //Добавление в очередь сообщений описанной ниже Runnable
            @Override
            public void run() {
                for (Student student : students) { //Для каждого студента в локальном списке студентов Сервиса
                    if (student.isRunning() && activity != null) {
                        updateActivity(student.getId(), student.getCurrentTime());
                    }
                }
                handler.postDelayed(this, 100); //Создание сообщения с той же Runnable через 100 милиссекунд. А
            }
            /*Алгоритм рекурсивный. Каждые 100 миллисекунд будет выполняться действие updateActivity()
            и то же самое действие будет добавляться в очередь через 100 миллисекунд. */
        });
        /*Runnable реализован в виде АНОНИМНОГО КЛАССА. Мы явно не создаём объект класса Runnable
        а просто перегружаем его единственный метод - run(). Java сама разворачивает объект и передаёт его
        в метод хендлера post().*/
    }

    private void updateActivity(int studentId, long time) { //Обновление в Activity
        if (activity != null) {
            activity.updateTimerUI(studentId, time);
        }
    }

    public static void setActivity(MainActivity act) {
        activity = act;
    } //Привязка к Activity

    public static void clearActivity() {
        activity = null;
    } //Отвязка от Activity
}