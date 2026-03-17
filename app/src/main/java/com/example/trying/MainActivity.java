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

    private static final int REQUEST_CODE_ADD = 1; //Код запроса. Используется в получении результата из AddStudentActivity
    private RecyclerView recyclerView; //Создание объекта рекуклера
    private StudentAdapter adapter; // Создание объекта адаптера
    private List<Student> students = new ArrayList<>(); // Создание пустого списка из объектов класса Студент

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Привязка к XML файлу MainActivity

        recyclerView = findViewById(R.id.recyclerView); //Привязка к View рекуклера в activity_main.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); /*Задание менеджера компоновки.
        Этот объект (LinearLayoutManager) указывает рекуклеру, что элементы будут располагаться в виде простого списка.*/

        startService(new Intent(this, TimerService.class)); //команда запуска Сервиса TimerService

        for (Student student : students) {
            TimerService.addStudent(student);
        } /*Список студентов в Сервисе и в MainActivity+StudentAdapter различный.
        В связи с этим в список Сервиса копируются изначальные данные из списка MainActivity сразу после вызова
        (конкретно в данной реализации передаётся пустой список, но, при желании, изначальный список может быть заполнен)*/

        TimerService.setActivity(this); //Внутри сервиса хранится ссылка на Activity, в котором будет происходить обновление таймера

        adapter = new StudentAdapter(students, this); //Создание адаптера
        recyclerView.setAdapter(adapter); //Связка адаптера с рекуклером

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        }); /*При нажатии на кнопку "добавить студента" создаётся явный Intent, привязанный к AddStudentActivity
              Происходит вызов AddStudentActivity с ожиданием результата. Обработка результата происходит в onACtivityResult*/
    }

     public void updateTimerUI(int studentId, long time) {
        runOnUiThread(() -> { //функция запуска некоторого действия в основном потоке
            adapter.updateTimer(studentId, time);
        });
    } //Данный метод вызывается из TimerService. Обновление отрисовки происходит в ОСНОВНОМ потоке

    @Override
    /*Три параметра: 1) requestCode - код запроса.
                        Так как MainActivity может выпускать несколько Intent в разные Activity,
                        каждая Activity возвращает код запроса. Таким образом появляется возможность
                        сортировать полученные результаты по самим запросам.
                     2) resultCode - код результата.
                        Код результата определяет общую характеристику самого результата.
                        RESULT_OK подразумевает удоволетворительный результат
                        RESULT_CANCELLED подразумевает отсутствие результата
                        RESULT_FIRST_USER подразумевает первый прогон
                     3) data - данные, необходимые после обработки.
                        Данные опеределяются кодом вызванной активности. В сущности важность представляет
                        не сам Intent, а Extra, заложенная в него. (Как минимум для данной реализации)*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Метод обработки результата с AddStudentActivity
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK && data != null) {
            /*В тело заходит лишь результат, полученный от запроса на добавление студента (REQUEST_CODE_ADD),
            являющийся удоволетворительным (RESULT_OK) и имеющий данные (data != null) */
            String name = data.getStringExtra(AddStudentActivity.STUDENT_EXTRA_NAME); //Вычленение данных об поле name из Extra
            int score = data.getIntExtra(AddStudentActivity.SCORE_EXTRA_NAME, 1); //Вычленение данных об поле score из Extra. По умолчанию 1
            Student newStudent = new Student(students.size() -1,name, score); //Создание нового студента. Его порядковый номер определяется размером списка (добавляется в конец)
            adapter.addStudent(newStudent); //Добавление нового студента как элемента в список MainActivity+StudentAdapter
            TimerService.addStudent(newStudent); //Добавление нового студента как элемента в список TimerService
        }
    }

    @Override
    protected void onDestroy() {
        TimerService.clearActivity(); //Отвязка Сервиса от Activity
        stopService(new Intent(this, TimerService.class)); //Остановка Сервиса
        super.onDestroy();
    }

}