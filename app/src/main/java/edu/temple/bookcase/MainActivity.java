package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookCommunicator, BookDetailsFragment.ServiceInterface
{
    FragmentManager fragmentManager;
    ArrayList<Book> books;
    Fragment checkPane1, checkPane2;
    boolean onePane, connected;
    BookDetailsFragment bookDetailsFragment;
    AudiobookService.MediaControlBinder binder;

    ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            connected = true;
            binder = (AudiobookService.MediaControlBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            connected = false;
            binder = null;
        }
    };

    Handler JSONHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message msg)
        {
            try
            {
                books.clear();
                JSONArray booksJSONArray = new JSONArray(msg.obj.toString());
                for (int i = 0; i < booksJSONArray.length(); i++)
                {
                    JSONObject jsonObject = (JSONObject) booksJSONArray.get(i);
                    books.add(new Book(jsonObject.getInt("book_id"),
                            jsonObject.getString("title"),
                            jsonObject.getString("author"),
                            jsonObject.getInt("published"),
                            jsonObject.getString("cover_url"),
                            jsonObject.getInt("duration")));
                }

                if (fragmentManager.findFragmentById(R.id.pane1) == null)
                    createDisplay();
                else
                    refreshBooks();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        books = new ArrayList<Book>();

        checkPane1 = fragmentManager.findFragmentById(R.id.pane1);
        checkPane2 = fragmentManager.findFragmentById(R.id.pane2);
        onePane = (findViewById(R.id.pane2) == null);

        if (checkPane1 == null)
            search("");
        else
            refreshDisplay();

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(((EditText) findViewById(R.id.searchBar)).getText().toString());
            }
        });
    }

    void createDisplay()
    {
        if (onePane)
        {
            checkPane1 = ViewPagerFragment.newInstance(books);
            fragmentManager.beginTransaction().add(R.id.pane1, checkPane1).commit();
        }

        else
        {
            checkPane1 = BookListFragment.newInstance(books);
            bookDetailsFragment = new BookDetailsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.pane1, checkPane1)
                    .add(R.id.pane2, bookDetailsFragment).commit();
        }

        startService(new Intent(this, AudiobookService.class));
        bindService(new Intent(this, AudiobookService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    void refreshDisplay()
    {
        Fragment fragment = checkPane1;
        books = ((Displayable) checkPane1).fetchBooks();

        if (onePane)
        {
            if (checkPane1 instanceof BookListFragment)
            {
                checkPane1 = ViewPagerFragment.newInstance(books);
                fragmentManager.beginTransaction().remove(fragment).add(R.id.pane1, checkPane1).commit();
            }
        }
        else
        {
            if (checkPane1 instanceof ViewPagerFragment)
            {
                checkPane1 = BookListFragment.newInstance(books);
                fragmentManager.beginTransaction().remove(fragment).add(R.id.pane1, checkPane1).commit();
            }

            if (checkPane2 instanceof BookDetailsFragment)
                bookDetailsFragment = (BookDetailsFragment) checkPane2;
            else
            {
                bookDetailsFragment = new BookDetailsFragment();
                fragmentManager.beginTransaction().add(R.id.pane2, bookDetailsFragment).commit();
            }
        }

        bookDetailsFragment = (BookDetailsFragment) checkPane2;
        startService(new Intent(this, AudiobookService.class));
        bindService(new Intent(this, AudiobookService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    void refreshBooks()
    {
        ((Displayable) checkPane1).changeListOfBooks(books);
    }

    @Override
    public void getBook(Book book)
    {
        if (bookDetailsFragment != null)
            bookDetailsFragment.changeBook(book);
    }

    public void search(final String searchTerm)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                String tempURL;
                URL url;

                try
                {
                    if (searchTerm.equals(""))
                        tempURL = "https://kamorris.com/lab/audlib/booksearch.php";
                    else
                        tempURL = "https://kamorris.com/lab/audlib/booksearch.php?search=" + searchTerm;

                    url = new URL(tempURL);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder();
                    String response;

                    while ((response = reader.readLine()) != null)
                    {
                        builder.append(response);
                    }

                    Message message = Message.obtain();
                    message.obj = builder.toString();
                    JSONHandler.sendMessage(message);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public void playBook(Book book)
    {
        if (connected)
        {
            binder.play(book.id);
        }
    }
}