package edu.temple.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class BookListFragment extends Fragment implements Displayable
{
    static final String ARG_BOOKS = "argBooks";
    ArrayList<Book> books;
    ListView listView;
    CustomArrayAdapter adapter;
    BookCommunicator mainActivity;

    public BookListFragment(){}

    public static BookListFragment newInstance(ArrayList<Book> books)
    {
        BookListFragment bookListFragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKS, books);
        bookListFragment.setArguments(args);
        return bookListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            books = (ArrayList<Book>) getArguments().getSerializable(ARG_BOOKS);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof BookCommunicator)
            mainActivity = (BookCommunicator) context;
        else
            throw new RuntimeException("Please implement CustomArrayAdapter");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_booklist, container, false);

        listView = view.findViewById(R.id.booklistListView);
        listView.setAdapter(new CustomArrayAdapter((Context) mainActivity, books));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainActivity.getBook(books.get(position));
            }
        });

        return view;
    }

    @Override
    public void changeListOfBooks(ArrayList<Book> books)
    {
        this.books = books;
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public ArrayList<Book> fetchBooks(){return books;}

    public interface BookCommunicator {void getBook(Book book);}

    public class CustomArrayAdapter extends BaseAdapter
    {
        ArrayList<Book> books;
        Context context;

        public CustomArrayAdapter(Context context, ArrayList<Book> books)
        {
            this.books = books;
            this.context = context;
        }

        @Override
        public int getCount()
        {
            return books.size();
        }

        @Override
        public Object getItem(int i)
        {
            return books.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            TextView textView = new TextView(context);
            textView.setText(books.get(i).title);
            return textView;
        }
    }
}
