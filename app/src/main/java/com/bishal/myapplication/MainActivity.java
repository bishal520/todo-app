package com.bishal.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<ToDoItem> toDoItems = new ArrayList<>();
    private ToDoAdapter toDoAdapter;
    private EditText inputTask;
    private Button addButton;
    private Gson gson = new Gson();
    private Handler handler = new Handler();
    private Runnable updateTaskRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with toDoItems list and the item checked change listener
        toDoAdapter = new ToDoAdapter(toDoItems, new ToDoAdapter.OnItemCheckedChangeListener() {
            @Override
            public void onItemCheckedChanged(ToDoItem item) {
                saveToDoItemsToFile(); // Save tasks to file when an item is checked/unchecked
            }
        });
        recyclerView.setAdapter(toDoAdapter);

        // Initialize EditText and Button
        inputTask = findViewById(R.id.inputTask);
        addButton = findViewById(R.id.addButton);

        // Add a new task when the button is clicked
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskText = inputTask.getText().toString().trim();

                if (!taskText.isEmpty()) {
                    // Add new ToDo item
                    toDoItems.add(new ToDoItem(taskText, false));
                    toDoAdapter.notifyDataSetChanged(); // Notify adapter to update the RecyclerView

                    inputTask.setText(""); // Clear the input field

                    // Save tasks to file
                    saveToDoItemsToFile();
                } else {
                    // Show a toast message if input is empty
                    Toast.makeText(MainActivity.this, "Please enter a task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Start periodic loading
        startPeriodicLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop periodic loading
        stopPeriodicLoading();
        // Save tasks when the activity is destroyed
        saveToDoItemsToFile();
    }

    private void saveToDoItemsToFile() {
        FileOutputStream fos = null;
        BufferedWriter writer = null;
        try {
            fos = openFileOutput("todo.json", MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            String json = gson.toJson(toDoItems);
            writer.write(json);
            Toast.makeText(this, "Tasks saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save tasks", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (writer != null) writer.close();
                if (fos != null) fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadToDoItemsFromFile() {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = openFileInput("todo.json");
            reader = new BufferedReader(new InputStreamReader(fis));
            Type listType = new TypeToken<ArrayList<ToDoItem>>() {}.getType();
            List<ToDoItem> loadedItems = gson.fromJson(reader, listType);
            if (loadedItems != null) {
                toDoItems.clear();
                toDoItems.addAll(loadedItems);
                toDoAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (reader != null) reader.close();
                if (fis != null) fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startPeriodicLoading() {
        updateTaskRunnable = new Runnable() {
            @Override
            public void run() {
                loadToDoItemsFromFile();
                handler.postDelayed(this, 1000); // Schedule next update in 1 second
            }
        };
        handler.post(updateTaskRunnable);
    }

    private void stopPeriodicLoading() {
        if (updateTaskRunnable != null) {
            handler.removeCallbacks(updateTaskRunnable);
        }
    }
}
