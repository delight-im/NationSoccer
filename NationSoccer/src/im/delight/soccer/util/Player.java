package im.delight.soccer.util;

import im.delight.soccer.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
	
	public static final int PLAYERS_TOTAL = 24;
	public static final int COUNTRY_SPAIN = 1;
	public static final int COUNTRY_BRAZIL = 2;
	public static final int COUNTRY_FRANCE = 3;
	public static final int COUNTRY_CHINA = 4;
	public static final int COUNTRY_GERMANY = 5;
	public static final int COUNTRY_CAMEROON = 6;
	public static final int COUNTRY_RUSSIA = 7;
	public static final int COUNTRY_NIGERIA = 8;
	public static final int COUNTRY_USA = 9;
	public static final int COUNTRY_SOUTH_KOREA = 10;
	public static final int COUNTRY_UK = 11;
	public static final int COUNTRY_JAMAICA = 12;
	public static final int COUNTRY_ARGENTINA = 13;
	public static final int COUNTRY_JAPAN = 14;
	public static final int COUNTRY_MEXICO = 15;
	public static final int COUNTRY_IVORY_COAST = 16;
	public static final int COUNTRY_GREECE = 17;
	public static final int COUNTRY_NETHERLANDS = 18;
	public static final int COUNTRY_URUGUAY = 19;
	public static final int COUNTRY_BELGIUM = 20;
	public static final int COUNTRY_ITALY = 21;
	public static final int COUNTRY_TURKEY = 22;
	public static final int COUNTRY_PORTUGAL = 23;
	public static final int COUNTRY_ECUADOR = 24;
	public static final int SKILL_SPEED = 1;
	public static final int SKILL_JUMP = 2;
	public static final int SKILL_POWER = 3;
	private static final float FACTOR_SCALING_SPEED = 0.4f;
	private static final float FACTOR_SCALING_JUMP = 0.36f;
	private static final float FACTOR_SCALING_POWER = 0.32f;
	public static final String SERIALIZATION_DELIMITER = "§";
	private int mCountry;
	private float mSpeed; // between 0.0 (slow) and 1.0 (fast)
	private float mJump; // between 0.0 (low) and 1.0 (high)
	private float mPower; // between 0.0 (weak) and 1.0 (strong)
	
    public String getShortName() {
    	switch (mCountry) {
	    	case COUNTRY_BRAZIL: return "BRA";
	    	case COUNTRY_CHINA: return "CHN";
	    	case COUNTRY_FRANCE: return "FRA";
	    	case COUNTRY_GERMANY: return "GER";
	    	case COUNTRY_NIGERIA: return "NGR";
	    	case COUNTRY_UK: return "GBR";
	    	case COUNTRY_RUSSIA: return "RUS";
	    	case COUNTRY_JAMAICA: return "JAM";
	    	case COUNTRY_SOUTH_KOREA: return "KOR";
	    	case COUNTRY_USA: return "USA";
	    	case COUNTRY_SPAIN: return "ESP";
	    	case COUNTRY_JAPAN: return "JPN";
	    	case COUNTRY_CAMEROON: return "CMR";
	    	case COUNTRY_IVORY_COAST: return "CIV";
	    	case COUNTRY_MEXICO: return "MEX";
	    	case COUNTRY_ARGENTINA: return "ARG";
	    	case COUNTRY_GREECE: return "GRC";
	    	case COUNTRY_NETHERLANDS: return "NLD";
	    	case COUNTRY_URUGUAY: return "URY";
	    	case COUNTRY_BELGIUM: return "BEL";
	    	case COUNTRY_ITALY: return "ITA";
	    	case COUNTRY_TURKEY: return "TUR";
	    	case COUNTRY_PORTUGAL: return "PRT";
	    	case COUNTRY_ECUADOR: return "ECU";
	    	default: return "COM";
    	}
    }
    
    public String getLongName(Context context) {
    	return context.getString(getLongNameID());
    }
    
    public int getLongNameID() {
    	switch (mCountry) {
	    	case COUNTRY_BRAZIL: return R.string.player_br;
	    	case COUNTRY_CHINA: return R.string.player_cn;
	    	case COUNTRY_FRANCE: return R.string.player_fr;
	    	case COUNTRY_GERMANY: return R.string.player_de;
	    	case COUNTRY_NIGERIA: return R.string.player_ng;
	    	case COUNTRY_UK: return R.string.player_uk;
	    	case COUNTRY_RUSSIA: return R.string.player_ru;
	    	case COUNTRY_JAMAICA: return R.string.player_jm;
	    	case COUNTRY_SOUTH_KOREA: return R.string.player_kr;
	    	case COUNTRY_USA: return R.string.player_us;
	    	case COUNTRY_SPAIN: return R.string.player_es;
	    	case COUNTRY_JAPAN: return R.string.player_jp;
	    	case COUNTRY_CAMEROON: return R.string.player_cm;
	    	case COUNTRY_IVORY_COAST: return R.string.player_ci;
	    	case COUNTRY_MEXICO: return R.string.player_mx;
	    	case COUNTRY_ARGENTINA: return R.string.player_ar;
	    	case COUNTRY_GREECE: return R.string.player_gr;
	    	case COUNTRY_NETHERLANDS: return R.string.player_nl;
	    	case COUNTRY_URUGUAY: return R.string.player_uy;
	    	case COUNTRY_BELGIUM: return R.string.player_be;
	    	case COUNTRY_ITALY: return R.string.player_it;
	    	case COUNTRY_TURKEY: return R.string.player_tr;
	    	case COUNTRY_PORTUGAL: return R.string.player_pt;
	    	case COUNTRY_ECUADOR: return R.string.player_ec;
	    	default: return 0;
    	}
    }
    
    public Player(int country, float speed, float jump, float power) {
    	mCountry = country;
    	mSpeed = speed;
    	mJump = jump;
    	mPower = power;
    }
   
    public int getCountry() {
        return mCountry;
    }
    
    public float getSpeed() {
        return mSpeed;
    }
    
    /** With given scaling 0.X, this function scales the speed effect to a factor around 1.0 with -0.X and +0.X deviation */
    public float getSpeedFactor() {
    	return ((mSpeed - 0.5f) * 2 * FACTOR_SCALING_SPEED) + 1.0f;
    }
    
    public void setSpeed(float speed) {
    	if (speed < 0.0f || speed > 1.0f) {
    		throw new RuntimeException("Value out of range: "+speed);
    	}
    	else {
    		mSpeed = speed;
    	}
    }
    
    public void increaseSpeed(float value) {
		mSpeed += value;
		if (mSpeed > 1.0f) {
			mSpeed = 1.0f;
		}
		else if (mSpeed < 0.0f) {
			mSpeed = 0.0f;
		}
    }
    
    public float getJump() {
        return mJump;
    }
    
    /** With given scaling 0.X, this function scales the jump effect to a factor around 1.0 with -0.X and +0.X deviation */
    public float getJumpFactor() {
    	return ((mJump - 0.5f) * 2 * FACTOR_SCALING_JUMP) + 1.0f;
    }
    
    public void setJump(float jump) {
    	if (jump < 0.0f || jump > 1.0f) {
    		throw new RuntimeException("Value out of range: "+jump);
    	}
    	else {
    		mJump = jump;
    	}
    }
    
    public void increaseJump(float value) {
    	mJump += value;
		if (mJump > 1.0f) {
			mJump = 1.0f;
		}
		else if (mJump < 0.0f) {
			mJump = 0.0f;
		}
    }
    
    public float getPower() {
        return mPower;
    }
    
    /** With given scaling 0.X, this function scales the power effect to a factor around 1.0 with -0.X and +0.X deviation */
    public float getPowerFactor() {
    	return ((mPower - 0.5f) * 2 * FACTOR_SCALING_POWER) + 1.0f;
    }
    
    public void setPower(float power) {
    	if (power < 0.0f || power > 1.0f) {
    		throw new RuntimeException("Value out of range: "+power);
    	}
    	else {
    		mPower = power;
    	}
    }
    
    public void increasePower(float value) {
		mPower += value;
		if (mPower > 1.0f) {
			mPower = 1.0f;
		}
		else if (mPower < 0.0f) {
			mPower = 0.0f;
		}
    }
    
    public int getDrawableID() {
    	switch (mCountry) {
	    	case COUNTRY_BRAZIL: return R.drawable.player_br;
	    	case COUNTRY_CHINA: return R.drawable.player_cn;
	    	case COUNTRY_FRANCE: return R.drawable.player_fr;
	    	case COUNTRY_GERMANY: return R.drawable.player_de;
	    	case COUNTRY_NIGERIA: return R.drawable.player_ng;
	    	case COUNTRY_UK: return R.drawable.player_uk;
	    	case COUNTRY_RUSSIA: return R.drawable.player_ru;
	    	case COUNTRY_JAMAICA: return R.drawable.player_jm;
	    	case COUNTRY_SOUTH_KOREA: return R.drawable.player_kr;
	    	case COUNTRY_USA: return R.drawable.player_us;
	    	case COUNTRY_SPAIN: return R.drawable.player_es;
	    	case COUNTRY_JAPAN: return R.drawable.player_jp;
	    	case COUNTRY_CAMEROON: return R.drawable.player_cm;
	    	case COUNTRY_IVORY_COAST: return R.drawable.player_ci;
	    	case COUNTRY_MEXICO: return R.drawable.player_mx;
	    	case COUNTRY_ARGENTINA: return R.drawable.player_ar;
	    	case COUNTRY_GREECE: return R.drawable.player_gr;
	    	case COUNTRY_NETHERLANDS: return R.drawable.player_nl;
	    	case COUNTRY_URUGUAY: return R.drawable.player_uy;
	    	case COUNTRY_BELGIUM: return R.drawable.player_be;
	    	case COUNTRY_ITALY: return R.drawable.player_it;
	    	case COUNTRY_TURKEY: return R.drawable.player_tr;
	    	case COUNTRY_PORTUGAL: return R.drawable.player_pt;
	    	case COUNTRY_ECUADOR: return R.drawable.player_ec;
	    	default: return 0;
    	}
    }
    
    public String getDrawableString() {
    	switch (mCountry) {
	    	case COUNTRY_BRAZIL: return "player_br.png";
	    	case COUNTRY_CHINA: return "player_cn.png";
	    	case COUNTRY_FRANCE: return "player_fr.png";
	    	case COUNTRY_GERMANY: return "player_de.png";
	    	case COUNTRY_NIGERIA: return "player_ng.png";
	    	case COUNTRY_UK: return "player_uk.png";
	    	case COUNTRY_RUSSIA: return "player_ru.png";
	    	case COUNTRY_JAMAICA: return "player_jm.png";
	    	case COUNTRY_SOUTH_KOREA: return "player_kr.png";
	    	case COUNTRY_USA: return "player_us.png";
	    	case COUNTRY_SPAIN: return "player_es.png";
	    	case COUNTRY_JAPAN: return "player_jp.png";
	    	case COUNTRY_CAMEROON: return "player_cm.png";
	    	case COUNTRY_IVORY_COAST: return "player_ci.png";
	    	case COUNTRY_MEXICO: return "player_mx.png";
	    	case COUNTRY_ARGENTINA: return "player_ar.png";
	    	case COUNTRY_GREECE: return "player_gr.png";
	    	case COUNTRY_NETHERLANDS: return "player_nl.png";
	    	case COUNTRY_URUGUAY: return "player_uy.png";
	    	case COUNTRY_BELGIUM: return "player_be.png";
	    	case COUNTRY_ITALY: return "player_it.png";
	    	case COUNTRY_TURKEY: return "player_tr.png";
	    	case COUNTRY_PORTUGAL: return "player_pt.png";
	    	case COUNTRY_ECUADOR: return "player_ec.png";
	    	default: return null;
    	}
    }
    
    public int getFlagDrawableID() {
    	switch (mCountry) {
	    	case COUNTRY_BRAZIL: return R.drawable.flag_br;
	    	case COUNTRY_CHINA: return R.drawable.flag_cn;
	    	case COUNTRY_FRANCE: return R.drawable.flag_fr;
	    	case COUNTRY_GERMANY: return R.drawable.flag_de;
	    	case COUNTRY_NIGERIA: return R.drawable.flag_ng;
	    	case COUNTRY_UK: return R.drawable.flag_uk;
	    	case COUNTRY_RUSSIA: return R.drawable.flag_ru;
	    	case COUNTRY_JAMAICA: return R.drawable.flag_jm;
	    	case COUNTRY_SOUTH_KOREA: return R.drawable.flag_kr;
	    	case COUNTRY_USA: return R.drawable.flag_us;
	    	case COUNTRY_SPAIN: return R.drawable.flag_es;
	    	case COUNTRY_JAPAN: return R.drawable.flag_jp;
	    	case COUNTRY_CAMEROON: return R.drawable.flag_cm;
	    	case COUNTRY_IVORY_COAST: return R.drawable.flag_ci;
	    	case COUNTRY_MEXICO: return R.drawable.flag_mx;
	    	case COUNTRY_ARGENTINA: return R.drawable.flag_ar;
	    	case COUNTRY_GREECE: return R.drawable.flag_gr;
	    	case COUNTRY_NETHERLANDS: return R.drawable.flag_nl;
	    	case COUNTRY_URUGUAY: return R.drawable.flag_uy;
	    	case COUNTRY_BELGIUM: return R.drawable.flag_be;
	    	case COUNTRY_ITALY: return R.drawable.flag_it;
	    	case COUNTRY_TURKEY: return R.drawable.flag_tr;
	    	case COUNTRY_PORTUGAL: return R.drawable.flag_pt;
	    	case COUNTRY_ECUADOR: return R.drawable.flag_ec;
	    	default: return 0;
    	}
    }
    
    public String getFlagDrawableString() {
    	switch (mCountry) {
	    	case COUNTRY_BRAZIL: return "flag_br.png";
	    	case COUNTRY_CHINA: return "flag_cn.png";
	    	case COUNTRY_FRANCE: return "flag_fr.png";
	    	case COUNTRY_GERMANY: return "flag_de.png";
	    	case COUNTRY_NIGERIA: return "flag_ng.png";
	    	case COUNTRY_UK: return "flag_uk.png";
	    	case COUNTRY_RUSSIA: return "flag_ru.png";
	    	case COUNTRY_JAMAICA: return "flag_jm.png";
	    	case COUNTRY_SOUTH_KOREA: return "flag_kr.png";
	    	case COUNTRY_USA: return "flag_us.png";
	    	case COUNTRY_SPAIN: return "flag_es.png";
	    	case COUNTRY_JAPAN: return "flag_jp.png";
	    	case COUNTRY_CAMEROON: return "flag_cm.png";
	    	case COUNTRY_IVORY_COAST: return "flag_ci.png";
	    	case COUNTRY_MEXICO: return "flag_mx.png";
	    	case COUNTRY_ARGENTINA: return "flag_ar.png";
	    	case COUNTRY_GREECE: return "flag_gr.png";
	    	case COUNTRY_NETHERLANDS: return "flag_nl.png";
	    	case COUNTRY_URUGUAY: return "flag_uy.png";
	    	case COUNTRY_BELGIUM: return "flag_be.png";
	    	case COUNTRY_ITALY: return "flag_it.png";
	    	case COUNTRY_TURKEY: return "flag_tr.png";
	    	case COUNTRY_PORTUGAL: return "flag_pt.png";
	    	case COUNTRY_ECUADOR: return "flag_ec.png";
	    	default: return null;
    	}
    }
    
	public Player(Player copyFrom) {
		if (copyFrom != null) {
			mCountry = copyFrom.mCountry;
			mSpeed = copyFrom.mSpeed;
			mJump = copyFrom.mJump;
			mPower = copyFrom.mPower;
		}
		else {
			throw new RuntimeException("You cannot create a Player copy from a null reference");
		}
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(mCountry+SERIALIZATION_DELIMITER);
		out.append(mSpeed+SERIALIZATION_DELIMITER);
		out.append(mJump+SERIALIZATION_DELIMITER);
		out.append(mPower);
		return out.toString();
	}
	
	public Player(String data) throws Exception {
		try {
			final String[] parts = data.split(SERIALIZATION_DELIMITER, -1); // -1 to preserve possible empty word at the end
			if (parts.length == 4) {
				mCountry = Integer.parseInt(parts[0]);
				mSpeed = Float.parseFloat(parts[1]);
				mJump = Float.parseFloat(parts[2]);
				mPower = Float.parseFloat(parts[3]);
			}
			else {
				throw new Exception("Player serialization of unknown length");
			}
		}
		catch (Exception e) {
			throw new Exception("Could not parse Player object");
		}
	}
    
    @Override
    public int hashCode() {
    	// start with a non-zero constant
    	int hash = 17;
    	// now include a hash for each field
    	hash = hash * 31 + mCountry;
    	return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) {
    		return true;
    	}
		if (obj != null) {
			if (this.hashCode() == obj.hashCode()) {
				return true;
			}
		}
    	return false;
    }
    
	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
		@Override
		public Player createFromParcel(Parcel in) {
			return new Player(in);
		}
		@Override
		public Player[] newArray(int size) {
			return new Player[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mCountry);
		out.writeFloat(mSpeed);
		out.writeFloat(mJump);
		out.writeFloat(mPower);
	}
	
	private Player(Parcel in) {
		mCountry = in.readInt();
		mSpeed = in.readFloat();
		mJump = in.readFloat();
		mPower = in.readFloat();
	}

}