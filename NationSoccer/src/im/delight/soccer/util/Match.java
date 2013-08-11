package im.delight.soccer.util;


import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable {
	
	private Player mPlayer1;
	private Player mPlayer2;
	private int mGoals1;
	private int mGoals2;
    
    public Match(Player p1, Player p2) {
    	this(p1, p2, -1, -1);
    }
    
    public Match(Player p1, Player p2, int g1, int g2) {
    	mPlayer1 = p1;
    	mPlayer2 = p2;
    	mGoals1 = g1;
    	mGoals2 = g2;
    }
    
    public void setResult(int goals1, int goals2) {
    	mGoals1 = goals1;
    	mGoals2 = goals2;
    }
    
    public void addGoals(int forHome, int forGuest) {
    	mGoals1 += forHome;
    	mGoals2 += forGuest;
    }
    
    public Player getPlayerHome() {
    	return mPlayer1;
    }
    
    public Player getPlayerGuest() {
    	return mPlayer2;
    }
    
    public int getGoalsHome() {
    	return mGoals1;
    }
    
    public int getGoalsGuest() {
    	return mGoals2;
    }
    
    public Player getWinner() {
    	if (mGoals1 > mGoals2) {
    		return mPlayer1;
    	}
    	else if (mGoals2 > mGoals1) {
    		return mPlayer2;
    	}
    	else {
    		return null;
    	}
    }
    
    public String getResultString() {
    	return mGoals1+":"+mGoals2;
    }
    
    public void swapTeams() {
    	Player player1 = mPlayer1;
    	mPlayer1 = mPlayer2;
    	mPlayer2 = player1;

    	int goals1 = mGoals1;
    	mGoals1 = mGoals2;
    	mGoals2 = goals1;
    }
    
    public boolean isPending() {
    	return !isFinished();
    }
    
    public boolean isFinished() {
    	return mGoals1 > -1 && mGoals2 > -1 && mGoals1 != mGoals2;
    }
    
    public boolean isReady() {
    	return mPlayer1 != null && mPlayer2 != null;
    }
    
    @Override
    public int hashCode() {
    	// start with a non-zero constant
    	int hash = 17;
    	// now include a hash for each field
    	hash = hash * 31 + (mPlayer1 == null ? 0 : mPlayer1.hashCode());
    	hash = hash * 31 + (mPlayer2 == null ? 0 : mPlayer1.hashCode());
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
    
	public static final Parcelable.Creator<Match> CREATOR = new Parcelable.Creator<Match>() {
		@Override
		public Match createFromParcel(Parcel in) {
			return new Match(in);
		}
		@Override
		public Match[] newArray(int size) {
			return new Match[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(mPlayer1, flags);
		out.writeParcelable(mPlayer2, flags);
		out.writeInt(mGoals1);
		out.writeInt(mGoals2);
	}
	
	private Match(Parcel in) {
		mPlayer1 = in.readParcelable(Player.class.getClassLoader());
		mPlayer2 = in.readParcelable(Player.class.getClassLoader());
		mGoals1 = in.readInt();
		mGoals2 = in.readInt();
	}

}