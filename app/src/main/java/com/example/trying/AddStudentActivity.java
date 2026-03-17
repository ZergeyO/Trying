package com.example.trying;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    public static final String SCORE_EXTRA_NAME = "score"; //Строковый ключ для получения счёта
    public static final String STUDENT_EXTRA_NAME = "name"; //Строковый ключ для получения имени
    private EditText editName, editScore; //Поля вводимого имени и счёта
    private Button btnSave; //Поле кнопки сохранения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student); //Привязка к XML файлу AddStudent

        editName = findViewById(R.id.edit_name);
        editScore = findViewById(R.id.edit_score);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> {
            saveStudent();
        }); //Операция сохранения студента выведена в отдельный метод
    }

    private void saveStudent() {
        /*В XML файле поля EditText имеют тип данных Text, который необходимо получить(getText()),
         преобразовать в String (toString) и удалить табуляции, лишние пробелы и символы новой строки (trim())*/
        String name = editName.getText().toString().trim();
        String scoreStr = editScore.getText().toString().trim();

        if (name.isEmpty()) { //Если не было введено имя, то выскакивает ошибка
            editName.setError("Введите имя");
            return;
        }

        int score = 0; //Значение счёта.
        if (!scoreStr.isEmpty()) {
            try {
                score = Integer.parseInt(scoreStr); //Преобразование строковой записи счёта в целочисленную
            } catch (NumberFormatException e) {
                editScore.setError("Введите число");
                return;
            }
        }

        // временный ID (будет заменен в MainActivity)
        Student student = new Student(-1, name, score);


        Intent resultIntent = new Intent(); //Итоговый Intent с требуемыми данными
        resultIntent.putExtra(SCORE_EXTRA_NAME, student.getScore()); //Сохранение счёта по строковому ключу в Extra
        resultIntent.putExtra(STUDENT_EXTRA_NAME, student.getName());//Сохранение имени по строковому ключу в Extra
        setResult(RESULT_OK, resultIntent); //Так как удоволетворительным результатом является получение данных о студенте, то код результата Удоволетворительный
        finish();
    }
}