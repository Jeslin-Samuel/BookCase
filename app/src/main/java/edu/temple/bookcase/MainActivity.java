package edu.temple.bookcase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookListFragment bookListFragment;
        ViewPagerFragment viewPagerFragment;
        BookDetailsFragment bookDetailsFragment;
        ArrayList<String> bookList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.list_books)));

        if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null))
        {
            viewPagerFragment = ViewPagerFragment.newInstance(bookList);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, viewPagerFragment).commit();
        }

        else
        {
            bookListFragment = BookListFragment.newInstance(bookList);
            bookDetailsFragment = BookDetailsFragment.newInstance(bookList.get(0));

            getSupportFragmentManager().beginTransaction().add(R.id.leftHalf, bookListFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.rightHalf, bookDetailsFragment).commit();

        }
    }

    @Override
    public void getBook(String title)
    {
        BookDetailsFragment bookDetailsFragment = BookDetailsFragment.newInstance(title);
        getSupportFragmentManager().beginTransaction().add(R.id.rightHalf, bookDetailsFragment).addToBackStack(null).commit();
    }
}
