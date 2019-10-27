package edu.temple.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment
{
    public static final String ARG_BOOKS = "argBooks";
    public Context parent;
    public ArrayList<String> books;

    public static BookListFragment newInstance(ArrayList<String> books)
    {
        BookListFragment bookListFragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_BOOKS, books);
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
            books = getArguments().getStringArrayList(ARG_BOOKS);
        List<String> bookList = books;
        
        ArrayAdapter adapter = new ArrayAdapter(parent, android.R.layout.simple_list_item_1, bookList);
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

    public interface BookCommunicator
    {
        void getBook(String title);
    }
}
