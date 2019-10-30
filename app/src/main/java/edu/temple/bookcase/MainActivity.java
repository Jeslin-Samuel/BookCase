package edu.temple.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookListFragment bookListFragment;
        ViewPagerFragment viewPagerFragment;
        BookDetailsFragment bookDetailsFragment;
        ArrayList<String> bookList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.list_books)));
        ArrayList<BookDetailsFragment> detailsFragments = new ArrayList<BookDetailsFragment>();

        for (String title: bookList)
        {
            detailsFragments.add(BookDetailsFragment.newInstance(title));
        }

        if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null))
        {
            viewPagerFragment = ViewPagerFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, viewPagerFragment).commit();
        }

        else
        {
            bookListFragment = BookListFragment.newInstance(bookList);
            bookDetailsFragment = BookDetailsFragment.newInstance(bookList.get(0));
            getSupportFragmentManager().beginTransaction().add(R.id.leftHalf, bookListFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.rightHalf, bookDetailsFragment).commit();
        }
    }

    @Override
    public void getBook(String title)
    {
        BookDetailsFragment bookDetailsFragment = BookDetailsFragment.newInstance(title);
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, bookDetailsFragment).addToBackStack(null).commit();
    }
}
