package com.sutd.hostelmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends AppCompatActivity {

    User user;
    Spinner user_year,user_pillar, user_block, user_level;
    EditText user_name, user_unit;
    FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mUserDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("residents").child(u.getUid());

        user_year = (Spinner) findViewById(R.id.user_class);
        user_pillar = (Spinner) findViewById(R.id.user_pillar);
        user_block  = (Spinner) findViewById(R.id.user_block);
        user_level = (Spinner) findViewById(R.id.user_level);
        user_name = (EditText) findViewById(R.id.user_name);
        user_unit = (EditText)findViewById(R.id.user_unit);

        ArrayAdapter<CharSequence> pillaradapter = ArrayAdapter.createFromResource(this,
                R.array.Pillars, android.R.layout.simple_spinner_item);
        pillaradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_pillar.setAdapter(pillaradapter);

        ArrayAdapter<CharSequence> classadapter = ArrayAdapter.createFromResource(this,
                R.array.ClassOf, android.R.layout.simple_spinner_item);
        classadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_year.setAdapter(classadapter);

        ArrayAdapter<CharSequence> blockadapter = ArrayAdapter.createFromResource(this,
                R.array.Block, android.R.layout.simple_spinner_item);
        blockadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_block.setAdapter(blockadapter);

        ArrayAdapter<CharSequence> leveladapter = ArrayAdapter.createFromResource(this,
                R.array.Level, android.R.layout.simple_spinner_item);
        leveladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_level.setAdapter(leveladapter);


        Button submit = (Button) findViewById(R.id.submit_changes);
        Intent intent= new Intent();
        int mode = intent.getIntExtra("PROFILE_STATE", 0); // profile state == 0, new profile
        View.OnClickListener click;
        switch(mode){
            case 1:
                submit.setText("Save Changes");

                 click = new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        saveChanges();
                }};
                break;
            default:
                submit.setText("Confirm");
                click = new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        initialSave();
                }};
        }
        submit.setOnClickListener(click);

    }
    private void saveData(){
//        String uid, String name, String pillar, String unit, int block, int Class, int level
        user = new User(
                user_name.getText().toString(),
                user_pillar.getSelectedItem().toString(),
                user_unit.getText().toString(),
                Integer.parseInt(user_block.getSelectedItem().toString()),
                Integer.parseInt(user_level.getSelectedItem().toString()),
                Integer.parseInt(user_year.getSelectedItem().toString()));

        mUserDatabase.setValue(user);

    }

    private void saveChanges(){//include firebase
        Toast.makeText(this, "Saving Changes", Toast.LENGTH_SHORT).show();
        saveData();
        finish();
    }
    //called when user firth creates account
    private void initialSave(){//include firebase
        Toast.makeText(this, "Saved Profile", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Welcome to Hostelmates", Toast.LENGTH_SHORT).show();
        saveData();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }


}
