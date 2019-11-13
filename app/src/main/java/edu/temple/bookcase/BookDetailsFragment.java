package edu.temple.bookcase;

import android.content.Context;
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
    public static final String ARG_BOOK = "argBook";
    Context parent;
    Book book;

    public static BookDetailsFragment newInstance(Book book)
    {
        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        bookDetailsFragment.setArguments(args);
        return bookDetailsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.parent = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookdetails, container, false);
        ImageView bookCover = view.findViewById(R.id.bookCover);
        TextView bookTitle = view.findViewById(R.id.bookTitle);
        TextView bookAuthor = view.findViewById(R.id.bookAuthor);
        TextView bookDate = view.findViewById(R.id.bookDate);

        if (getArguments() != null)
            book = (Book) getArguments().getSerializable(ARG_BOOK);

        if (book != null)
        {
            bookTitle.setText(book.title);
            bookAuthor.setText(book.author);
            bookDate.setText(Integer.toString(book.published));
            Picasso.get().load(book.coverURL).into(bookCover);
        }

        return view;
    }
}
