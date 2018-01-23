package com.umg.pelatihanar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ActivityUtama extends Activity {

	Button btn_show;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utama);
        
        btn_show=(Button)findViewById(R.id.btn_show);
        
        btn_show.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent window_brosur = new Intent(ActivityUtama.this,
						ImageTargets.class);
				Bundle myBundle1 = new Bundle();
				window_brosur.putExtras(myBundle1);
				startActivityForResult(window_brosur, 1122);
			}
		});
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_utama, menu);
        return true;
    }
    
}
