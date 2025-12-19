package com.example.trying;

public class Student {
    private int id;
    private String name;
    private int score;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    private long startTime = 0;

    public Student(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public void startTimer() {
        if (!isRunning) {
            isRunning = true;
            startTime = System.currentTimeMillis();
        }
    }

    public void pauseTimer() {
        if (isRunning) {
            elapsedTime += System.currentTimeMillis() - startTime;
            isRunning = false;
        }
    }

    public void stopTimer() {
        isRunning = false;
        elapsedTime = 0;
    }

    public long getCurrentTime() {
        if (isRunning) {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        }
        return elapsedTime;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getScore() {return score;}

    public void setScore(int score) {this.score = score;}
    public boolean isRunning() { return isRunning; }
}