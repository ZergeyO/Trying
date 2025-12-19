package com.example.trying;

// StudentAdapter.java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> students;
    private Context context;

    public StudentAdapter(List<Student> students, Context context) {
        this.students = students;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTimer, tvScore;
        Button btnStart, btnPause, btnStop;
        Button btnIncrement, btnDecrement;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTimer = itemView.findViewById(R.id.tv_timer);
            tvScore = itemView.findViewById(R.id.tv_score);
            btnStart = itemView.findViewById(R.id.btn_start);
            btnPause = itemView.findViewById(R.id.btn_pause);
            btnStop = itemView.findViewById(R.id.btn_stop);
            btnIncrement = itemView.findViewById(R.id.btn_increment);
            btnDecrement = itemView.findViewById(R.id.btn_decrement);
        }

        public void bind(Student student) {
            tvName.setText(student.getName());
            tvScore.setText(String.valueOf(student.getScore()));

            updateTimerDisplay(student.getCurrentTime());

            // Обработчики кнопок
            btnStart.setOnClickListener(v -> {
                sendCommandToService("start", student.getId());
            });

            btnPause.setOnClickListener(v -> {
                sendCommandToService("pause", student.getId());
            });

            btnStop.setOnClickListener(v -> {
                sendCommandToService("stop", student.getId());
                updateTimerDisplay(0);
            });
            btnIncrement.setOnClickListener(v ->
            {
                student.setScore(student.getScore() + 1);
                notifyItemChanged(getAdapterPosition());
            });

            btnDecrement.setOnClickListener(v ->
            {
                student.setScore(student.getScore() - 1);
                if (student.getScore() <= 0){
                    if(getAdapterPosition() != RecyclerView.NO_POSITION){removeStudent(getAdapterPosition());}
                    notifyItemRemoved(getAdapterPosition());}
                else {notifyItemChanged(getAdapterPosition());}
            });
        }

        private void sendCommandToService(String command, int studentId) {
            Intent intent = new Intent(context, TimerService.class);
            intent.putExtra("command", command);
            intent.putExtra("studentId", studentId);
            context.startService(intent);
        }

        public void updateTimerDisplay(long milliseconds) {
            String time = formatTime(milliseconds);
            tvTimer.setText(time);
        }
    }

    // Метод для обновления времени из Activity
    public void updateTimer(int studentId, long time) {
        // Находим позицию студента
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == studentId) {
                ViewHolder holder = (ViewHolder)
                        ((RecyclerView) ((MainActivity)context).findViewById(R.id.recyclerView))
                                .findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.updateTimerDisplay(time);
                }
                break;
            }
        }
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return String.format("%02d:%02d:%02d",
                hours % 24, minutes % 60, seconds % 60);
    }
    public void removeStudent(int position) {
        Student student = students.get(position);
        Intent stopIntent = new Intent(context, TimerService.class);
        stopIntent.putExtra("command", "stop");
        stopIntent.putExtra("studentId", student.getId());
        context.startService(stopIntent);

        students.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, students.size());

    }
    public void addStudent(Student student) {
        students.add(student);
        notifyItemInserted(students.size() - 1);
    }
}
