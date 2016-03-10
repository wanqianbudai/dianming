package se.anyro.nfc_reader;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView{

	public CustomFontTextView(Context context) {
		super(context);
		init(context);
		
	}
	private void init(Context context) {
		AssetManager ass=context.getAssets();
		Typeface font=Typeface.createFromAsset(ass, "fonts/Roboto-ThinItalic.ttf");
		setTypeface(font);
	}
	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public CustomFontTextView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs,defStyle);
		init(context);
	}
}
