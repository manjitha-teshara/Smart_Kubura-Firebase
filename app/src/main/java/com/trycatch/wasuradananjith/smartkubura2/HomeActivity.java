package com.trycatch.wasuradananjith.smartkubura2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trycatch.wasuradananjith.smartkubura2.Model.PaddyField;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView farmerName,farmerEmail,handleWaterLevel,paddyFieldList;
    ImageView imgHandleWaterLevel;
    Spinner dropdown;
    DatabaseReference mDatabase,mDatabase1;
    String phone,field_name_stored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = new Intent(this, NotificationService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleWaterLevel = (TextView)findViewById(R.id.txtWaterLevelControl);
        imgHandleWaterLevel = (ImageView)findViewById(R.id.imgWaterLevelControl);
        dropdown = findViewById(R.id.spinner);

        // get data for the particular logged in user from Shared Preferences (local storage of the app)
        final SharedPreferences pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        phone = pref.getString("phone", null);

        field_name_stored = pref.getString("field_name", null);

        Toast.makeText(getApplicationContext(), field_name_stored+ " තෝරාගෙන ඇත", Toast.LENGTH_LONG).show();

        // get the database reference "paddy_fields+thePhoneNumberOfTheLoggedInUser" in firebase realtime database
        mDatabase = FirebaseDatabase.getInstance().getReference("paddy_fields/"+phone);

        // creating an ArrayList to store the paddy_filed names of a particular user (logged in user)
        final List<String> paddies = new ArrayList<String>();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paddies.clear();
                paddies.add("කුඹුරක් තෝරාගෙන නැත");

                // add all the paddy_filed names of the particular user to the created ArrayList "paddies"
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    PaddyField paddyField= postSnapshot.getValue(PaddyField.class);
                    paddies.add(paddyField.getPaddyFieldName());
                    //Log.w("snapshot",postSnapshot.toString());
                }
                // creating the Spinner/Dropdown to select a particular paddy field name, in order to proceed with all the tasks in future
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
                        R.layout.spinner_item, paddies);
                adapter.setDropDownViewResource(R.layout.spinner_item);
                dropdown.setAdapter(adapter);
                dropdown.setSelection(adapter.getPosition(pref.getString("field_name", null)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MYTAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


        // when a particular paddy field name is selected from the Spinner/Dropdown
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                final SharedPreferences.Editor editor = pref.edit();
                editor.putString("field_name", item);
                editor.commit();
                /*if (item.equals("කුඹුරක් තෝරාගෙන නැත")){
                    Toast.makeText(parent.getContext(), "කුඹුරක් තෝරාගෙන නැත", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(parent.getContext(), item+ " තෝරාගෙන ඇත", Toast.LENGTH_LONG).show();
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // when the ImageView to handle the water level is clicked
        imgHandleWaterLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropdown.getSelectedItem()=="කුඹුරක් තෝරාගෙන නැත"){
                    Toast.makeText(getApplicationContext(), "කරුණාකර කුඹුරක් තෝරන්න!", Toast.LENGTH_LONG).show();
                }
                else{

                    // get the database reference "paddy_fields+thePhoneNumberOfTheLoggedInUser+selectedPaddyFieldName" in firebase realtime database
                    mDatabase1 = FirebaseDatabase.getInstance().getReference("paddy_fields/"+phone+"/"+dropdown.getSelectedItem());

                    mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            PaddyField paddyField= dataSnapshot.getValue(PaddyField.class);
                            //Toast.makeText(getApplicationContext(), paddyField.getIsFilling().toString(), Toast.LENGTH_LONG).show();
                            switch (paddyField.getIsFilling()){
                                // if the isFilling attribute is 0 in the selected paddy field (NOT FILLING) load the StartWaterPassActivity
                                case 0:{
                                    Intent in = new Intent(getApplicationContext(), StartWaterPassActivity.class);
                                    in.putExtra("paddy_field_name", paddyField.getPaddyFieldName());
                                    in.putExtra("water_level", paddyField.getWaterLevel());
                                    in.putExtra("required_water_level", paddyField.getRequiredWaterLevel());
                                    startActivity(in);
                                    finish();
                                    break;
                                }
                                // if the isFilling attribute is 1 in the selected paddy field (IS FILLING) load the StopWaterPassActivity
                                case 1:
                                    Intent in = new Intent(getApplicationContext(), StopWaterPassActivity.class);
                                    in.putExtra("paddy_field_name", paddyField.getPaddyFieldName());
                                    in.putExtra("water_level", paddyField.getWaterLevel());
                                    in.putExtra("required_water_level", paddyField.getRequiredWaterLevel());
                                    startActivity(in);
                                    finish();
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // when the floating button to add a new paddy field is clicked, load the AddPaddyFieldActivity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPaddyFieldActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        // default code for Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        SharedPreferences pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//
        farmerName = (TextView)findViewById(R.id.titleFarmerName);
        farmerEmail = (TextView)findViewById(R.id.titleFarmerEmail);
        Log.v("farmerName", String.valueOf(farmerName));
//        farmerName.setText(pref.getString("user", null));
//        farmerEmail.setText(pref.getString("email", null));
//        farmerEmail.setText("wasura@gmail.com");
//       farmerName.setText("Wasura");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        // when the logout button is clicked
        if (id == R.id.btnLogout) {
            SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("phone", null);
            editor.putString("email", null);
            editor.putString("user", null);

            editor.commit();

            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(in);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
