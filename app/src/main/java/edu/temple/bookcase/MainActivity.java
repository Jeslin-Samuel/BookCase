package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
    ArrayList<String> listOfBookTitle = new ArrayList<>();

    Handler JSONHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            JSONObject jsonObject;
            try {
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

        Log.d("onCreate Test", Integer.toString(newBookList.size()));

        ArrayList<String> bookList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.list_books)));

        for (int i = 0; i < newBookList.size(); i++) {
            listOfBookTitle.add(newBookList.get(i).title);
        }

        new Thread() {
            @Override
            public void run()
            {
                URL url;
                try
                {
                    url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
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

        if ((findViewById(R.id.landscape) == null) && (findViewById(R.id.large) == null))
        {
            viewPagerFragment = ViewPagerFragment.newInstance(newBookList);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, viewPagerFragment).commit();
        }

        else
        {
            bookListFragment = BookListFragment.newInstance(listOfBookTitle);
            bookDetailsFragment = BookDetailsFragment.newInstance(newBookList.get(0));

            getSupportFragmentManager().beginTransaction().add(R.id.leftHalf, bookListFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.rightHalf, bookDetailsFragment).commit();
        }
    }

    @Override
    public void getBook(String title)
    {
        Book newBook = null;

        for (int i = 0; i < newBookList.size(); i++) {
            if (newBookList.get(i).title.equals(title));
                newBook = newBookList.get(i);
        }

        BookDetailsFragment bookDetailsFragment = BookDetailsFragment.newInstance(newBook);
        getSupportFragmentManager().beginTransaction().replace(R.id.rightHalf, bookDetailsFragment).commit();
    }
}
