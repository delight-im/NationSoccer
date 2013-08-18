package im.delight.soccer;

import com.actionbarsherlock.app.SherlockFragment;
import im.delight.soccer.R;
import im.delight.soccer.util.Player;
import im.delight.soccer.util.PlayerSelectionHandler;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/** Fragment class where one object represents a single step in the ViewPager */
public class PlayerSelectionFragment extends SherlockFragment {
	
	private static final String ARG_PAGE = "page";
	private static final String ARG_PAGE_COUNT = "page_count";
	private static final String ARG_PLAYER = "player";
	private static final String ARG_REQUEST_CODE = "request_code";
	private int mPage;
	private int mPageCount;
	private Player mPlayer;
	private int mRequestCode;
	private PlayerSelectionHandler mCallback;
	private View.OnClickListener mContentClick = new View.OnClickListener() {
		public void onClick(View v) {
			mCallback.onSelectPlayer(mRequestCode, mPage, mPlayer);
		}
	};

    /** Factory method for this fragment class which constructs a new fragment for the given page number */
    public static PlayerSelectionFragment create(int page, int pageCount, Player player, int requestCode) {
    	PlayerSelectionFragment fragment = new PlayerSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(ARG_PAGE_COUNT, pageCount);
        args.putParcelable(ARG_PLAYER, player);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        fragment.setArguments(args);
        return fragment;
    }
    
    public PlayerSelectionFragment() {
    	super();
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	if (activity instanceof PlayerSelectionHandler) {
    		mCallback = (PlayerSelectionHandler) activity;
    	}
    	else {
    		throw new RuntimeException("Activity must implement interface PlayerSelectionHandler");
    	}
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mPageCount = getArguments().getInt(ARG_PAGE_COUNT);
        mPlayer = getArguments().getParcelable(ARG_PLAYER);
        mRequestCode = getArguments().getInt(ARG_REQUEST_CODE);
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.player_selection_page, container, false); // inflate the layout

        getActivity().setTitle(mRequestCode == PlayerSelection.REQUEST_CODE_GET_TEAM_OPPONENT ? R.string.choose_opponent_player : R.string.choose_your_player);
        ImageView picture = (ImageView) rootView.findViewById(R.id.picture);
        picture.setImageResource(mPlayer.getDrawableID());
        ImageView flag = (ImageView) rootView.findViewById(R.id.flag);
        flag.setImageResource(mPlayer.getFlagDrawableID());
        ProgressBar skillSpeed = (ProgressBar) rootView.findViewById(R.id.skill_speed);
        skillSpeed.setProgress(Math.round(mPlayer.getSpeed()*100));
        ProgressBar skillJump = (ProgressBar) rootView.findViewById(R.id.skill_jump);
        skillJump.setProgress(Math.round(mPlayer.getJump()*100));
        ProgressBar skillPower = (ProgressBar) rootView.findViewById(R.id.skill_power);
        skillPower.setProgress(Math.round(mPlayer.getPower()*100));
        ((TextView) rootView.findViewById(R.id.page_number)).setText(getString(R.string.player_selection_page, mPage+1, mPageCount));
        rootView.findViewById(R.id.submit_selection).setOnClickListener(mContentClick);

		// SHOW OR HIDE FORWARD AND BACKWARD BUTTONS BEGIN
		View vBackward = rootView.findViewById(R.id.player_back);
		View vForward = rootView.findViewById(R.id.player_forward);
		if (mPage == 0) {
			vBackward.setVisibility(View.INVISIBLE);
		}
		else {
			vBackward.setVisibility(View.VISIBLE);
			vBackward.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCallback.navigateTo(mPage-1);
				}
			});
		}
		if ((mPage+1) < mPageCount) {
			vForward.setVisibility(View.VISIBLE);
			vForward.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCallback.navigateTo(mPage+1);
				}
			});
		}
		else {
			vForward.setVisibility(View.INVISIBLE);
		}
		// SHOW OR HIDE FORWARD AND BACKWARD BUTTONS END

        return rootView; // return the newly constructed view
    }

	public int getPageNumber() {
        return mPage;
    }

}
