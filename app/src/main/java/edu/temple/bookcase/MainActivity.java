package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookCommunicator {
    Book placeholderBook = new Book(0,"Placeholder II: Electric Boogaloo", "Jane Doe", 1624, "https://upload.wikimedia.org/wikipedia/en/8/80/Wikipedia-logo-v2.svg");
    ArrayList<Book> placeholderList = new ArrayList<>(Arrays.asList(placeholderBook));

    Handler JSONHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            ArrayList<Book> newBookList = new ArrayList<>();
            JSONObject jsonObject;
            try {
                newBookList.clear();
                Log.d("Reached here", "Please!");
                JSONArray booklistJSONArray = new JSONArray(message.obj.toString());
                for (int i = 0; i < booklistJSONArray.length(); i++)
                {
                    jsonObject = (JSONObject) booklistJSONArray.get(i);
                    Book book = new Book(jsonObject.getInt("book_id"),
                                         jsonObject.getString("title"),
                                         jsonObject.getString("author"),
                                         jsonObject.getInt("published"),
                                         jsonObject.getString("cover_url"));
                    newBookList.add(book);
                }

                FragmentManager fm = getSupportFragmentManager();

                if (fm.findFragmentById(R.id.mainViewPager) != null) {
                    Log.d("Before Test", Integer.toString(newBookList.size()));
                    ViewPagerFragment viewPagerFragment = (ViewPagerFragment) fm.findFragmentById(R.id.mainViewPager);
                    viewPagerFragment.changeListOfBooks(newBookList);
                }

                if (fm.findFragmentById(R.id.leftHalf) != null)
                {
                    Log.d("listSearch","y");
                    BookListFragment bookListFragment = (BookListFragment) fm.findFragmentById(R.id.leftHalf);
                    bookListFragment.changeListOfBooks(newBookList);
                }

            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            makeViews(newBookList);

            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText portraitSearchBar = findViewById(R.id.portraitSearchBar);
        final EditText landscapeSearchBar = findViewById(R.id.landscapeSearchBar);
        final EditText tabletSearchBar = findViewById(R.id.tabletSearchBar);
        Button portraitSearchButton = findViewById(R.id.portraitSearchButton);
        Button landscapeSearchButton = findViewById(R.id.landscapeSearchButton);
        Button tabletSearchButton = findViewById(R.id.tabletSearchButton);

        Fragment checkPortrait = getSupportFragmentManager().findFragmentById(R.id.mainViewPager);
        Fragment checkList = getSupportFragmentManager().findFragmentById(R.id.leftHalf);

        if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null))
        {
            portraitSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    search(portraitSearchBar.getText().toString());
                }
            });
        }

        else
        {
            if (findViewById(R.id.landscape) != null) {
                landscapeSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        search(landscapeSearchBar.getText().toString());
                    }
                });
            }

            else {
                    tabletSearchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            search(tabletSearchBar.getText().toString());
                        }
                    });
            }
        }

        if (checkPortrait == null && checkList == null)
            search("");
        else
            makeViews(null);
    }

    @Override
    public void getBook(Book passedBook)
    {
        BookDetailsFragment bookDetailsFragment = BookDetailsFragment.newInstance(passedBook);
        getSupportFragmentManager().beginTransaction().replace(R.id.rightHalf, bookDetailsFragment).commit();
    }

    public void search(final String searchTerm)
    {
        Log.d("Search top", "a");
        new Thread() {
            @Override
            public void run()
            {
                String tempURL;
                Log.d("Search mid", "b");
                URL url;
                try
                {
                    Log.d("Search bot", "c");
                    if (searchTerm.equals(""))
                        tempURL = "https://kamorris.com/lab/audlib/booksearch.php";
                    else
                        tempURL = "https://kamorris.com/lab/audlib/booksearch.php?search=" + searchTerm;

                    url = new URL(tempURL);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder();
                    String response;

                    while ((response = reader.readLine()) != null) {
                        builder.append(response);
                    }

                    Message message = Message.obtain();
                    message.obj = builder.toString();
                    JSONHandler.sendMessage(message);
                }

                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void makeViews(ArrayList<Book> books)
    {
        BookListFragment bookListFragment;
        ViewPagerFragment viewPagerFragment;
        BookDetailsFragment bookDetailsFragment;
        Fragment checkPortrait = getSupportFragmentManager().findFragmentById(R.id.mainViewPager);
        Fragment checkList = getSupportFragmentManager().findFragmentById(R.id.leftHalf);
        Fragment checkDetail = getSupportFragmentManager().findFragmentById(R.id.rightHalf);

        if (checkPortrait == null && checkList == null)
        {
            if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null))
            {
                viewPagerFragment = ViewPagerFragment.newInstance(books);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainViewPager, viewPagerFragment).commit();
            }

            else
            {
                bookListFragment = BookListFragment.newInstance(books);
                bookDetailsFragment = BookDetailsFragment.newInstance(placeholderBook);
                getSupportFragmentManager().beginTransaction().replace(R.id.leftHalf, bookListFragment).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.rightHalf, bookDetailsFragment).commit();
            }

            new Thread() {
                @Override
                public void run()
                {
                    URL url;
                    Log.d("Hitting try", "help");
                    try
                    {
                        Log.d("Help1","");
                        url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        Log.d("Help2","");
                        StringBuilder builder = new StringBuilder();
                        String response;

                        while ((response = reader.readLine()) != null) {
                            builder.append(response);
                        }
                        Log.d("Help3", "");
                        Message message = Message.obtain();
                        message.obj = builder.toString();
                        Log.d("Reached send", "Send!");
                        JSONHandler.sendMessage(message);
                    }

                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        else
        {
            Log.d("Exisiting","what");
            if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null)) {
                if (checkPortrait == null)
                {
                    Log.d("Test11","ughgh");
                    BookListFragment temp = (BookListFragment) checkList;
                    ArrayList<Book> otherBooks = temp.fetchBooks();
                    viewPagerFragment = ViewPagerFragment.newInstance(otherBooks);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainViewPager, viewPagerFragment).commit();
                }
                else {
                    Log.d("Test12", "yeah");
                    viewPagerFragment = (ViewPagerFragment) checkPortrait;
                }
            }
            if (findViewById(R.id.mainViewPager) == null)
            {
                if (checkList == null)
                {
                    Log.d("Test21", "asdasd");
                    ViewPagerFragment temp = (ViewPagerFragment) checkPortrait;
                    ArrayList<Book> otherBooks = temp.fetchBooks();
                    bookListFragment = BookListFragment.newInstance(otherBooks);
                    bookDetailsFragment = BookDetailsFragment.newInstance(placeholderBook);
                    getSupportFragmentManager().beginTransaction().replace(R.id.leftHalf, bookListFragment).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.rightHalf, bookDetailsFragment).commit();
                }
                else {
                    Log.d("Test22","");
                    bookListFragment = (BookListFragment) checkList;
                    bookDetailsFragment = (BookDetailsFragment) checkDetail;
                }
            }
        }
    }
}
