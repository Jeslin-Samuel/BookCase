package edu.temple.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment
{
    public static final String ARG_BOOKS = "argBooks";
    public Context parent;
    public ArrayList<Book> books;
    CustomArrayAdapter adapter;

    public static BookListFragment newInstance(ArrayList<Book> books)
    {
        BookListFragment bookListFragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKS, books);
        bookListFragment.setArguments(args);
        return bookListFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.parent = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booklist, container, false);
        ListView listView = view.findViewById(R.id.booklistListView);

        if (getArguments() != null)
            books = (ArrayList<Book>) getArguments().getSerializable(ARG_BOOKS);

        adapter = new CustomArrayAdapter(parent, books);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                ((BookCommunicator) parent).getBook(books.get(i));
            }
        });

        return view;
    }

    public void changeListOfBooks(ArrayList<Book> bookTitles)
    {
        Log.d("After Title Test", Integer.toString(bookTitles.size()));
//        books.clear();
//        books.addAll(bookTitles);
        Log.d("Whywhywhy", Integer.toString(books.size()));
        adapter.notifyDataSetChanged();
    }

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
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int i) {
            return books.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(context);
            textView.setText(books.get(i).title);
            return textView;
        }
    }

    public interface BookCommunicator
    {
        void getBook(Book passedBook);
    }
}
