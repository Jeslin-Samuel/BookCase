package edu.temple.bookcase;

import java.io.Serializable;

public class Book implements Serializable
{
    int ID;
    String title;
    String author;
    int published;
    String coverURL;

    public Book(int ID, String title, String author, int published, String coverURL)
    {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.published = published;
        this.coverURL = coverURL;
    }
}
