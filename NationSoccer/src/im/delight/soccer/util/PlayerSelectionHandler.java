package im.delight.soccer.util;

public interface PlayerSelectionHandler {

	public void onSelectPlayer(int requestCode, int index, Player player);
	public void navigateTo(int index);

}
