package edu.temple.bookcase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment
{
    public static final String ARG_BOOKS = "argBooks";
    ViewPager viewPager;
    ArrayList<BookDetailsFragment> fragments = new ArrayList<>();
    ArrayList<Book> books = new ArrayList<>();
    CustomViewPagerAdapter adapter;

    public static ViewPagerFragment newInstance(ArrayList<Book> books)
    {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKS, books);
        viewPagerFragment.setArguments(args);
        return viewPagerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        if (getArguments() != null)
            books = (ArrayList<Book>) getArguments().getSerializable(ARG_BOOKS);

        for (Book book : books)
        {
            fragments.add(BookDetailsFragment.newInstance(book));
        }

        viewPager = view.findViewById(R.id.viewPager);

        adapter = new CustomViewPagerAdapter(getChildFragmentManager(), fragments);

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

        @Override
        public int getItemPosition(@NonNull Object object)
        {
            return PagerAdapter.POSITION_NONE;
        }
    }

    public void changeListOfBooks(ArrayList<Book> newBooks)
    {
        fragments.clear();
        Log.d("After Test", Integer.toString(newBooks.size()));
        Log.d("Another Test", Integer.toString(newBooks.get(0).published));
        for (int i = 0; i < newBooks.size(); i++) {
            fragments.add(new BookDetailsFragment().newInstance(newBooks.get(i)));
        }
        adapter.notifyDataSetChanged();
    }
}
