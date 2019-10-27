package edu.temple.bookcase;

import androidx.appcompat.app.AppCompatActivity;

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
        ArrayList<String> bookList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.list_books)));

        bookListFragment = BookListFragment.newInstance(bookList);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, bookListFragment).commit();
    }

    @Override
    public void getBook(String title)
    {
        BookDetailsFragment bookDetailsFragment = BookDetailsFragment.newInstance(title);
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, bookDetailsFragment).addToBackStack(null).commit();
    }
}
