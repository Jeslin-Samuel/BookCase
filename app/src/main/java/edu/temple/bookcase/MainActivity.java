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

    Handler JSONHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            JSONObject jsonObject;

            try {
                JSONArray booklistJSONArray = new JSONArray(message.obj.toString());

                for (int i = 0; i < booklistJSONArray.length(); i++) {
                    jsonObject = (JSONObject) booklistJSONArray.get(i);
                    newBookList.add(new Book(
                            jsonObject.getInt("book_id"),
                            jsonObject.getString("title"),
                            jsonObject.getString("author"),
                            jsonObject.getInt("published"),
                            jsonObject.getString("cover_url")));
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

        BookListFragment bookListFragment;
        ViewPagerFragment viewPagerFragment;
        BookDetailsFragment bookDetailsFragment;
        ArrayList<String> bookList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.list_books)));

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

                    Log.d("JSON Data", message.obj.toString());

                }

                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

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
            getSupportFragmentManager().beginTransaction().add(R.id.rightHalf, bookDetailsFragment).commit();

        }
    }

    @Override
    public void getBook(String title)
    {
        BookDetailsFragment bookDetailsFragment = (BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.rightHalf);
        bookDetailsFragment.textView.setText(title);
    }

    public void booklistJSONGet(String myURL) {}
}
