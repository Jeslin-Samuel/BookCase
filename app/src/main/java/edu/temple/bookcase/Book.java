package edu.temple.bookcase;

import java.io.File;
import java.io.Serializable;

public class Book implements Serializable
{
    int id, published, duration, progress = 0;
    String title, author, coverURL;
    File localAudiobook;

    public Book(int id, String title, String author, int published, String coverURL, int duration)
    {
        this.id = id;
        this.title = title;
        this.author = author;
        this.published = published;
        this.coverURL = coverURL;
        this.duration = duration;
    }

    public void setLocalAudiobook(File localAudiobook)
    {
        this.localAudiobook = localAudiobook;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }
}