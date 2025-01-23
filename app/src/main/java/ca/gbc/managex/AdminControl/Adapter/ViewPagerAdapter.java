package ca.gbc.managex.AdminControl.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ca.gbc.managex.AdminControl.Fragments.ManageEmployeeFragment;
import ca.gbc.managex.AdminControl.Fragments.ManagePOSFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
      switch (position){
          case 0:
              fragment = new ManagePOSFragment();
              break;
          case 1:
              fragment = new ManageEmployeeFragment();
              break;
          default:
              fragment = null;
      }
      return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
