package com.akumine.smartclass.classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.util.Constant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddClassActivity extends AppCompatActivity implements View.OnClickListener {

    String[] list = {"Select your number", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
    private EditText className;
    private EditText classDesc;
    private Spinner spinnerMember;
    private Button btnAddClass;

    private String uid;

    public static void start(Context context, String uid) {
        Intent intent = new Intent(context, AddClassActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Class");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        uid = intent.getStringExtra(Constant.EXTRA_USER_ID);

        className = (EditText) findViewById(R.id.class_name);
        classDesc = (EditText) findViewById(R.id.class_desc);
        spinnerMember = (Spinner) findViewById(R.id.spinner_member);
        btnAddClass = (Button) findViewById(R.id.add_class_btn);
        btnAddClass.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMember.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_class_btn) {
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            String id = UUID.randomUUID().toString();
            String name = className.getText().toString();
            String desc = classDesc.getText().toString();
            String created = dateFormat.format(calendar.getTime());
            String modify = created;

            if (isDetailsValidated(name, desc, spinnerMember.getSelectedItemId())) {
                String selected = String.valueOf(spinnerMember.getSelectedItem());

                Classes classes = new Classes(id, name, desc, uid, created, modify, "0", selected);

                DatabaseReference tableClass = FirebaseDatabase.getInstance().getReference().child(Classes.DB_CLASS);
                tableClass.child(id).setValue(classes);

                Toast.makeText(AddClassActivity.this, "Class Successfully Added", Toast.LENGTH_SHORT).show();
                finish();
            }


        }
    }

    private boolean isDetailsValidated(String name, String desc, long selectedItemId) {
        View viewToFocus = null;

        if (name.isEmpty()) {
            className.setError(Constant.ERROR_CLASS_NAME_EMPTY);
            viewToFocus = className;
        } else {
            className.setError(null);
        }

        if (desc.isEmpty()) {
            classDesc.setError(Constant.ERROR_CLASS_DESC_EMPTY);
            viewToFocus = classDesc;
        } else {
            classDesc.setError(null);
        }

        if (viewToFocus != null) {
            viewToFocus.requestFocus();
            return false;
        }

        // check if user choose the first list which is "Select your number"
        if (selectedItemId == 0) {
            Toast.makeText(AddClassActivity.this, "Member's limit not correct", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
