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
    public static final String ARG_TITLES = "argTitles";
    ViewPager viewPager;
    ArrayList<BookDetailsFragment> fragments = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();

    public static ViewPagerFragment newInstance(ArrayList<String> titles)
    {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TITLES, titles);
        viewPagerFragment.setArguments(args);
        return viewPagerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        if (getArguments() != null)
            titles = getArguments().getStringArrayList(ARG_TITLES);

        for (String title : titles)
        {
            fragments.add(BookDetailsFragment.newInstance(title));
        }

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
