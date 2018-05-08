package com.trycatch.wasuradananjith.smartkubura2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StartWaterPassActivity extends AppCompatActivity {
    TextView txtWaterLevel;
    ImageView btnBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_water_pass);

        txtWaterLevel =(TextView)findViewById(R.id.lblcurrentWaterLevel);
        btnBackButton = (ImageView)findViewById(R.id.imgBackButton);


        Bundle bundle = getIntent().getExtras();
        String water_level = bundle.getString("water_level");
        txtWaterLevel.setText(water_level);

        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
}
