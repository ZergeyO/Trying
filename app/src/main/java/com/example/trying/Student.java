package com.example.trying;

public class Student {
    private int id; //Уникальный номер
    private String name; //Имя
    private int score; //Счёт
    private long elapsedTime = 0; //Время, от ПОСЛЕДНЕГО НАЧАЛА замера до текущего момента
    private boolean isRunning = false; //Запущен ли таймер
    private long startTime = 0; //Время начала замера

    public Student(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    /*System.currentTimeMillis() возвращает текущее время в миллисекундах с начала эпохи Unix (1 января 1970 года, 00:00:00 GMT)*/
    public void startTimer() {
        if (!isRunning) {
            isRunning = true;
            startTime = System.currentTimeMillis(); //Задача точки начала отсчёта
        }
    }

    public void pauseTimer() {
        if (isRunning) {
            elapsedTime += System.currentTimeMillis() - startTime; /*Прошедшее время измеряется как разница между текущим временем
            (временем нажатия на кнопку паузы) и временем начала отсчёта*/
            isRunning = false; //Так как таймер на паузе, то таймер больше не бежит
        }/*Так как таймер остановился, а возобновит его StartTimer(), то стартовое время ИЗМЕНИТСЯ.
         Следственно elapsedTime является переменной, хранящей время даже во время пауз (накопленное время до паузы)*/
    }

    public void stopTimer() {
        isRunning = false;
        elapsedTime = 0; //обнуление накопленного времени
    }

    public long getCurrentTime() {
        if (isRunning) {
            return elapsedTime + (System.currentTimeMillis() - startTime); /*Возвращается время между от последней точкой старта
            до текущего времени, плюс накопленного время от предыдущих запусков*/
        }
        return elapsedTime; /*Если таймер не запущен, то он либо на паузе, и необходимо демонстрировать накопленное время,
        либо он выключен и время равно нулю.*/
    }

    //Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public int getScore() {return score;}

    //Сеттеры
    public void setScore(int score) {this.score = score;}
    public boolean isRunning() { return isRunning; }
}