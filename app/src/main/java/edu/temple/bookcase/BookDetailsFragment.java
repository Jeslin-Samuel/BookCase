package edu.temple.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    Button downloadButton, deleteButton;
    ServiceInterface serviceInterface;

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

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof ServiceInterface)
            serviceInterface = (ServiceInterface) context;
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
        downloadButton = view.findViewById(R.id.downloadButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        if (book != null)
            changeBook(book);

        view.findViewById(R.id.playButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                serviceInterface.playBook(book);
            }
        });

        view.findViewById(R.id.downloadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                serviceInterface.downloadBook(book);
            }
        });

        view.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                serviceInterface.deleteBook(book);
            }
        });

        return view;
    }

    public void changeBook(Book book)
    {
        this.book = book;
        bookTitle.setText(book.title);
        bookAuthor.setText(book.author);
        bookDate.setText(Integer.toString(book.published));
        Picasso.get().load(book.coverURL).into(bookCover);
    }

    public interface ServiceInterface
    {
        void playBook(Book book);
        void downloadBook (Book book);
        void deleteBook(Book book);
    }
}