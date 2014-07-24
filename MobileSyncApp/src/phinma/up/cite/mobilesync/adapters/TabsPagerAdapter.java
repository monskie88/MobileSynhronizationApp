package phinma.up.cite.mobilesync.adapters;

import phinma.up.cite.mobilesyncapp.LocalFilesFragment;
import phinma.up.cite.mobilesyncapp.NetworkFilesFragment;
import phinma.up.cite.mobilesyncapp.SettingsFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch(position){
		case 0:
			return new LocalFilesFragment();
		case 1:
			return new NetworkFilesFragment();
		case 2:
			return new SettingsFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
