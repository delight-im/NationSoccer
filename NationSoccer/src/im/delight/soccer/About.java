package im.delight.soccer;

import im.delight.soccer.util.TextActivity;

public class About extends TextActivity {
	
	public static final String CONTENT_TEXT = "<b>Support for this game:</b><br />+ www.facebook.com/nationsoccer<br />+ Tiborges (tiborges@delight.im)<br /><br /><b>Photos, Graphics &#38; Fonts:</b><br />+ Martin Berube<br />+ www.IconDrawer.com<br />+ Will Palmer (Creative Commons BY 2.0)<br />+ Tyler Finck (SIL Open Font License 1.1)<br />+ The Android Open Source Project<br /><br /><b>Sounds &#38; Music:</b><br />+ temawas (Creative Commons BY 3.0)<br />+ Joe DeShon (Creative Commons BY 3.0)<br />+ LloydEvans09 (Creative Commons BY 3.0)<br />+ davidou<br />+ freki3333<br />+ Mydo1<br /><br /><b>Special Thanks:</b><br />+ Federico Roberts<br />+ David Richardson<br /><br /><b>Inspired by:</b><br />+ Adam Wardle<br /><br /><b>This product includes the following third-party software libraries licensed under the Apache License, Version 2.0:</b><br />+ ActionBarSherlock by Jake Wharton<br />+ Android Support Library v4 by The Android Open Source Project<br />+ AndEngine by Nicolas Gramlich<br /><br /><b>The license text for those libraries (the Apache License) may be accessed at the following page:</b><br />www.apache.org/licenses/LICENSE-2.0";

	@Override
	public String getContentText() {
		return CONTENT_TEXT;
	}

	@Override
	public boolean isHTML() {
		return true;
	}

}