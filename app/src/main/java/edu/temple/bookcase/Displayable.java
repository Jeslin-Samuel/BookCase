package edu.temple.bookcase;

import java.util.ArrayList;

public interface Displayable
{
    ArrayList<Book> fetchBooks();
    void changeListOfBooks(ArrayList<Book> books);
}
