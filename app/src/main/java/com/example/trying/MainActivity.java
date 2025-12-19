package com.example.trying;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        startService(new Intent(this, TimerService.class));

        for (Student student : students) {
            TimerService.addStudent(student);
        }

        TimerService.setActivity(this);

        adapter = new StudentAdapter(students, this);
        recyclerView.setAdapter(adapter);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });
    }

     public void updateTimerUI(int studentId, long time) {
        runOnUiThread(() -> {
            adapter.updateTimer(studentId, time);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK && data != null) {
            String name = data.getStringExtra(AddStudentActivity.STUDENT_EXTRA_NAME);
            int score = data.getIntExtra(AddStudentActivity.SCORE_EXTRA_NAME, 1);
            Student newStudent = new Student(students.size() -1,name, score);
            adapter.addStudent(newStudent);
            TimerService.addStudent(newStudent);
        }
    }

    @Override
    protected void onDestroy() {
        TimerService.clearActivity();
        stopService(new Intent(this, TimerService.class));
        super.onDestroy();
    }

}