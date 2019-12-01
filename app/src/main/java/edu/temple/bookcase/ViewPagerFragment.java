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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment implements Displayable
{
    static final String ARG_BOOKS = "argBooks";
    ViewPager viewPager;
    ArrayList<Book> books;
    CustomViewPagerAdapter adapter;

    public ViewPagerFragment(){}

    public static ViewPagerFragment newInstance(ArrayList<Book> books)
    {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKS, books);
        viewPagerFragment.setArguments(args);
        return viewPagerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            books = (ArrayList<Book>) getArguments().getSerializable(ARG_BOOKS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomViewPagerAdapter(getChildFragmentManager(), books));
        return view;
    }

    @Override
    public void changeListOfBooks(ArrayList<Book> books)
    {
        this.books = books;
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public ArrayList<Book> fetchBooks(){return books;}

    class CustomViewPagerAdapter extends FragmentStatePagerAdapter
    {
        ArrayList<Book> books;

        public CustomViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<Book> books)
        {
            super(fm);
            this.books = books;
        }

        @NonNull
        @Override
        public Fragment getItem(int position)
        {
            return BookDetailsFragment.newInstance(books.get(position));
        }

        @Override
        public int getCount()
        {
            return books.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object)
        {
            return PagerAdapter.POSITION_NONE;
        }
    }
}
