package edu.temple.bookcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment
{
    ViewPager viewPager;
    ArrayList<BookDetailsFragment> fragments;

    public static ViewPagerFragment newInstance()
    {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        return viewPagerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookdetails, container, false);

        fragments = new ArrayList<BookDetailsFragment>();

        fragments.add(BookDetailsFragment.newInstance("Huckleberry Finn"));

        viewPager = view.findViewById(R.id.viewPager);

        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getChildFragmentManager(), fragments);

        viewPager.setAdapter(adapter);

        return view;
    }

    class CustomViewPagerAdapter extends FragmentStatePagerAdapter
    {
        ArrayList<BookDetailsFragment> fragments;

        public CustomViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<BookDetailsFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
