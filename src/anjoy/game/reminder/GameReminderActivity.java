package anjoy.game.reminder;


import anjoy.game.reminder.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GameReminderActivity extends Activity {
    /** Called when the activity is first created. */
	
	private Button nbaListViewBtn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		findViews();
		setLIsteners();
		
    }
	
    private void findViews() {
    	nbaListViewBtn = (Button) findViewById(R.id.nbalistview_btn);
    }

    private void setLIsteners() {
    	nbaListViewBtn.setOnClickListener(nbaListViewBtnListener);

    }
       
	private View.OnClickListener nbaListViewBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v("nbaListView", "nbaListViewBtnListener");
			Intent intent = new Intent(GameReminderActivity.this, nbaListView.class);
			startActivity(intent);
		}
	};
}
