package com.example.trying;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {


    public static final String SCORE_EXTRA_NAME = "score";
    public static final String STUDENT_EXTRA_NAME = "name";
    private EditText editName, editScore;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        editName = findViewById(R.id.edit_name);
        editScore = findViewById(R.id.edit_score);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> {
            saveStudent();
        });
    }

    private void saveStudent() {
        String name = editName.getText().toString().trim();
        String scoreStr = editScore.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Введите имя");
            return;
        }

        int score = 0;
        if (!scoreStr.isEmpty()) {
            try {
                score = Integer.parseInt(scoreStr);
            } catch (NumberFormatException e) {
                editScore.setError("Введите число");
                return;
            }
        }

        // временный ID (будет заменен в MainActivity)
        Student student = new Student(-1, name, score);


        Intent resultIntent = new Intent();
        resultIntent.putExtra(SCORE_EXTRA_NAME, student.getScore());
        resultIntent.putExtra(STUDENT_EXTRA_NAME, student.getName());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}