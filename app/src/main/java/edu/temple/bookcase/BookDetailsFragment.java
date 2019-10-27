package edu.temple.bookcase;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookDetailsFragment extends Fragment
{
    public static final String ARG_TITLE = "argTitle";
    Context parent;
    String bookTitle;

    public static BookDetailsFragment newInstance(String bookTitle)
    {
        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, bookTitle);
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
        TextView textView = view.findViewById(R.id.textView);

        if (getArguments() != null)
            bookTitle = getArguments().getString(ARG_TITLE);

        textView.setText(bookTitle);
        view.setBackgroundColor(Color.WHITE);

        return view;
    }
}
