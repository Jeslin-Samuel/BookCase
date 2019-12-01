package edu.temple.bookcase;

import java.io.Serializable;

public class Book implements Serializable
{
    int id, published, duration;
    String title, author, coverURL;

    public Book(int id, String title, String author, int published, String coverURL, int duration)
    {
        this.id = id;
        this.title = title;
        this.author = author;
        this.published = published;
        this.coverURL = coverURL;
        this.duration = duration;
    }
}