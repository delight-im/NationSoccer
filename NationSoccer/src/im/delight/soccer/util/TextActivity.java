package im.delight.soccer.util;

import im.delight.soccer.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Abstract class that can be used to add a simple text-only Activity to the application. Just extend this class and implement getContentText() */
public abstract class TextActivity extends SherlockActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        final int paddingLR = getResources().getDimensionPixelSize(R.dimen.content_padding_lr);
        final int paddingTB = getResources().getDimensionPixelSize(R.dimen.content_padding_tb);
        textView.setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
        textView.setTextColor(Color.WHITE);
        if (isHTML()) {
        	textView.setText(Html.fromHtml(getContentText()));
        }
        else {
        	textView.setText(getContentText());
        }
        
        scrollView.addView(textView);
        layout.addView(scrollView);

        setContentView(layout, layoutParams);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
	
	/** Method to implement so that it returns the text to display that will then be used in TextView.setText() */
	public abstract String getContentText();
	
	/** Method to implement so that it returns whether the given text for this Activity is HTML (true) or plain text (false) */
	public abstract boolean isHTML();
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	finish();
		return true;
	}
    
}