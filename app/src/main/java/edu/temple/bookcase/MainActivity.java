package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
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
    TextView playerStatus;
    SeekBar seekBar;
    int globalID;
    Intent activityIntent;
    DownloadManager downloadManager;

    ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            connected = true;
            binder = (AudiobookService.MediaControlBinder) service;
            binder.setProgressHandler(progressHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            connected = false;
            binder = null;
        }
    };

    Handler progressHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message message)
        {
            final AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress) message.obj;
            int bookIndex = 0, progressOfBook;
            double duration;

            if (bookProgress != null)
            {
                bookIndex = findBookIndex(bookProgress, books);

                Log.d("Debug", "Book index: " + bookIndex);
                Log.d("Debug", "Progress: " + bookProgress.getProgress());

                duration = books.get(bookIndex).duration;
                progressOfBook = (int) (100.0 * bookProgress.getProgress() / duration);
                seekBar.setProgress(progressOfBook);
                globalID = bookProgress.getBookId();
                playerStatus.setText("Now Playing: " + books.get(bookIndex).title);
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    int time = 0, duration, bookIndex = 0;
                    double percentage;

                    if (fromUser)
                    {
                        bookIndex = findBookIndex(bookProgress, books);
                        duration = books.get(bookIndex).duration;
                        percentage = progress/100.0;
                        time = (int) ((double) duration * percentage);
                        binder.seekTo(time);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar){}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar){}
            });

            return true;
        }
    });

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
    protected void onDestroy()
    {
        super.onDestroy();
        if (serviceConnection != null)
            unbindService(serviceConnection);
    }

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
        activityIntent = new Intent(this, AudiobookService.class);

        if (checkPane1 == null)
            search("");
        else
            refreshDisplay();

        seekBar = findViewById(R.id.seekBar);
        bindService(activityIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(((EditText) findViewById(R.id.searchBar)).getText().toString());
            }
        });

        findViewById(R.id.pauseButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                binder.pause();
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                binder.stop();
                playerStatus.setText("");
                seekBar.setProgress(0);
                stopService(new Intent(getBaseContext(), AudiobookService.class));
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

        playerStatus = findViewById(R.id.playerStatus);
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
        playerStatus = findViewById(R.id.playerStatus);
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
        startService(activityIntent);
        if (connected)
        {
            if (book.localAudiobook != null)
                binder.play(book.localAudiobook, 15);
            else
                binder.play(book.id);

            playerStatus.setText("Now Playing: " + book.title);
            binder.setProgressHandler(progressHandler);
        }
    }

    @Override
    public void downloadBook(Book book)
    {
        File audiobookFile = new File(getExternalFilesDir(null), "Book" + book.id);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        String downloadURI = "https://kamorris.com/lab/audlib/download.php?id=" + book.id;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURI));
        request.setTitle("Audiobook Download");
        request.setDescription("Downloading the audiobook that corresponds with the passed ID");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationUri(Uri.fromFile(audiobookFile));
        request.setRequiresCharging(false);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        request.setAllowedOverMetered(true);

        downloadManager.enqueue(request);
        book.setLocalAudiobook(audiobookFile);
    }

    public void deleteBook(Book book)
    {
//        Log.d("Debug", "Path: " + book.localAudiobook.getAbsolutePath());
        if (book.localAudiobook != null)
        {
            book.localAudiobook.delete();
            book.setLocalAudiobook(null);
        }
    }

    public int findBookIndex(AudiobookService.BookProgress bookProgress, ArrayList<Book> books)
    {
        int bookIndex = 0;
        String uriString, URI_ID, compareString;

        if (bookProgress.getBookId() == -1)
        {
            for (int i = 0; i < books.size(); i++)
            {
                uriString = bookProgress.getBookUri().toString();
                URI_ID = uriString.substring(uriString.length() - 5);
                compareString = "Book" + books.get(i).id;

                if (compareString.equals(URI_ID))
                    bookIndex = i;
            }
        }

        else {
            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).id == bookProgress.getBookId())
                    bookIndex = i;
            }
        }

        return bookIndex;
    }
}