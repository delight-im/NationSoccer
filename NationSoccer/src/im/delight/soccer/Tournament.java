package im.delight.soccer;

import im.delight.soccer.util.Match;
import im.delight.soccer.util.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Tournament implements Parcelable {
	
	private List<Player> mParticipants;
    private Match[][] mMatches;
	private int mParticipantCount;
	private int mRounds;
    
    public Tournament(int participants, Player[] playerList, Player userControlledPlayer) {
    	mParticipantCount = participants;
    	mRounds = getNeededRounds(mParticipantCount);

    	mParticipants = getRandomParticipants(userControlledPlayer, playerList);
    	mMatches = createMatchArrays();
    	initFirstRound();
    }
    
    private List<Player> getRandomParticipants(Player userControlledPlayer, Player[] availableTeams) {
    	List<Player> participants = new ArrayList<Player>(mParticipantCount);
    	participants.add(userControlledPlayer); // user-controlled player must always participate of course
    	int participantsLeft = mParticipantCount-1;
    	for (Player player : availableTeams) {
    		if (player != null && participantsLeft > 0 && !player.equals(userControlledPlayer)) {
    			participants.add(player);
    			participantsLeft--;
    		}
    	}
    	Collections.shuffle(participants);
    	return participants;
    }
    
    private Match[][] createMatchArrays() {
    	Match[][] matches = new Match[mRounds][];
    	int nMatches = mParticipantCount;
    	for (int r = 0; r < matches.length; r++) {
    		nMatches = nMatches/2;
    		matches[r] = new Match[nMatches];
    	}
    	return matches;
    }
    
    private void initFirstRound() {
		for (int m = 0; m < mMatches[0].length; m++) {
			mMatches[0][m] = new Match(mParticipants.get(m*2), mParticipants.get(m*2+1));
		}
    }
    
    public void propagateWinnersToNextRounds(int round) {
    	propagateWinnersToNextRounds(round, null);
    }
    
    private void propagateWinnersToNextRounds(int round, List<Player> qualifiedPlayers) {
    	List<Player> qualifiedForNextRound;
    	boolean allMatchesFinished;
    	
		if (mMatches[round] != null) { // if round has been initialized
			qualifiedForNextRound = new LinkedList<Player>(); // create new list for teams that have qualified for the next round
			allMatchesFinished = true; // assume that all matches have been finished and check if this is true
			for (int m = 0; m < mMatches[round].length; m++) {
				if (mMatches[round][m] != null && mMatches[round][m].isReady()) { // if match has already been drawn
					if (mMatches[round][m].isFinished()) { // if match has already been played
						qualifiedForNextRound.add(mMatches[round][m].getWinner());
					}
					else {
						allMatchesFinished = false;
					}
				}
				else { // if match still has to be drawn
					allMatchesFinished = false;
					if (qualifiedPlayers != null && qualifiedPlayers.size() > (m*2+1)) { // if list of qualified players is available
						mMatches[round][m] = new Match(qualifiedPlayers.get(m*2), qualifiedPlayers.get(m*2+1));
					}
				}
			}
			if (allMatchesFinished) { // if all matches had been played we now have all qualified teams here for the new draw
				if ((round+1) < mMatches.length) { // if more rounds are to come
					propagateWinnersToNextRounds(round+1, qualifiedForNextRound); // update the next round
				}
			}
		}
		else {
			throw new RuntimeException("Round "+round+" has not been initialized yet");
		}

    }
    
    public Match[] getMatches(int round) {
    	if (round >= 0 && round < mMatches.length) {
    		return mMatches[round];
    	}
    	else {
    		throw new RuntimeException(round+" is not a valid round number");
    	}
    }
    
    public List<Match> getPendingMatches() {
    	boolean pendingGamesFound = false;
    	List<Match> out = new LinkedList<Match>();
    	for (int r = 0; r < mRounds; r++) {
    		if (!pendingGamesFound) {
	    		for (int p = 0; p < mMatches[r].length; p++) {
	    			if (mMatches[r][p].isPending()) {
	    				out.add(mMatches[r][p]);
	    				pendingGamesFound = true;
	    			}
	    		}
    		}
    	}
    	return out;
    }
    
    private static int getNeededRounds(int participants) {
    	int rounds = 0;
    	while (participants > 1) {
    		if (participants % 2 != 0) {
    			throw new RuntimeException(participants+" is not a valid participant count");
    		}
    		else {
    			participants = participants/2;
    			rounds++;
    		}
    	}
    	return rounds;
    }
    
    @Override
    public int hashCode() {
    	// start with a non-zero constant
    	int hash = 17;
    	// now include a hash for each field
    	hash = hash * 31 + mParticipantCount;
    	hash = hash * 31 + mRounds;
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
    
	public static final Parcelable.Creator<Tournament> CREATOR = new Parcelable.Creator<Tournament>() {
		@Override
		public Tournament createFromParcel(Parcel in) {
			return new Tournament(in);
		}
		@Override
		public Tournament[] newArray(int size) {
			return new Tournament[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mParticipantCount);
		out.writeInt(mRounds);
		for (int r = 0; r < mRounds; r++) {
			out.writeTypedArray(mMatches[r], flags);
		}
		out.writeTypedList(mParticipants);
	}
	
	private Tournament(Parcel in) {
		mParticipantCount = in.readInt();
		mRounds = in.readInt();
		for (int r = 0; r < mRounds; r++) {
			mMatches[r] = in.createTypedArray(Match.CREATOR);
		}
		mParticipants = in.createTypedArrayList(Player.CREATOR);
	}

}