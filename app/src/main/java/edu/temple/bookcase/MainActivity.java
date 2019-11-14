package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookCommunicator {
    ArrayList<Book> newBookList = new ArrayList<>();
    BookListFragment bookListFragment;
    ViewPagerFragment viewPagerFragment;
    BookDetailsFragment bookDetailsFragment;
    Book placeholderBook = new Book(0,"Placeholder II: Electric Boogaloo", "Jane Doe", 1624, "https://upload.wikimedia.org/wikipedia/en/8/80/Wikipedia-logo-v2.svg");

    Handler JSONHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            JSONObject jsonObject;
            try {
                newBookList.clear();
                Log.d("Reached here", "Please!");
                JSONArray booklistJSONArray = new JSONArray(message.obj.toString());
                for (int i = 0; i < booklistJSONArray.length(); i++) {
                    jsonObject = (JSONObject) booklistJSONArray.get(i);
                    Book book = new Book(jsonObject.getInt("book_id"),
                                         jsonObject.getString("title"),
                                         jsonObject.getString("author"),
                                         jsonObject.getInt("published"),
                                         jsonObject.getString("cover_url"));
                    newBookList.add(book);
                }

                if (viewPagerFragment != null)
                {
                    Log.d("Before Test", Integer.toString(newBookList.size()));
                    viewPagerFragment.changeListOfBooks(newBookList);
                }

                else
                {
                    bookListFragment.changeListOfBooks(newBookList);
                }

            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            return false;
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

        if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null))
        {
            portraitSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    search(portraitSearchBar.getText().toString());
                }
            });
            viewPagerFragment = ViewPagerFragment.newInstance(newBookList);
            getSupportFragmentManager().beginTransaction().add(R.id.mainViewPager, viewPagerFragment).commit();
        }

        else
        {
            if (findViewById(R.id.landscape) != null)
            {
                landscapeSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        search(landscapeSearchBar.getText().toString());
                    }
                });
            }

            else
            {
                tabletSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        search(tabletSearchBar.getText().toString());
                    }
                });
            }

            bookListFragment = BookListFragment.newInstance(newBookList);
            bookDetailsFragment = BookDetailsFragment.newInstance(placeholderBook);

            getSupportFragmentManager().beginTransaction().add(R.id.leftHalf, bookListFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.rightHalf, bookDetailsFragment).commit();
        }
    }

    @Override
    public void getBook(Book passedBook)
    {
        BookDetailsFragment bookDetailsFragment = BookDetailsFragment.newInstance(passedBook);
        getSupportFragmentManager().beginTransaction().replace(R.id.rightHalf, bookDetailsFragment).commit();
    }

    public void search(final String searchTerm)
    {
        new Thread() {
            @Override
            public void run()
            {
                URL url;
                try
                {
                    String tempURL = "https://kamorris.com/lab/audlib/booksearch.php?search=" + searchTerm;
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
}
