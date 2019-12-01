package edu.temple.bookcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class BookDetailsFragment extends Fragment
{
    static final String ARG_BOOK = "argBook";
    Book book;
    ImageView bookCover;
    TextView bookTitle, bookAuthor, bookDate;

    public BookDetailsFragment(){}

    public static BookDetailsFragment newInstance(Book book)
    {
        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        bookDetailsFragment.setArguments(args);
        return bookDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            book = (Book) getArguments().getSerializable(ARG_BOOK);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_bookdetails, container, false);

        bookTitle = view.findViewById(R.id.bookTitle);
        bookAuthor = view.findViewById(R.id.bookAuthor);
        bookDate = view.findViewById(R.id.bookDate);
        bookCover = view.findViewById(R.id.bookCover);

        if (book != null)
            changeBook(book);

        return view;
    }

    public void changeBook(Book book)
    {
        bookTitle.setText(book.title);
        bookAuthor.setText(book.author);
        Picasso.get().load(book.coverURL).into(bookCover);
    }
}