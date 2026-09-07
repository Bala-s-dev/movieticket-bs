package com.movieticket.model;

import java.time.LocalDate;

public class Movie {
    
    private long id;
    private String name;
    private String description;
    private String language;
    private String genre;
    private int durationMinutes;
    private LocalDate releaseDate;
    private boolean active;

    public Movie() {
        this.id = 0;
    }

    public Movie(long movieId, String name, String description, String language,
                 String genre, int durationMinutes, LocalDate releaseDate) {
        this.id = movieId;
        this.name = name;
        this.description = description;
        this.language = language;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
        this.active = true;
    }

    public long getMovieId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public String getDescription() { 
        return description; 
    }

    public String getLanguage() { 
        return language; 
    }

    public String getGenre() { 
        return genre; 
    }

    public void setMovieId(long id){
        this.id = id;
    }

    public int getDurationMinutes() { 
        return durationMinutes; 
    }

    public LocalDate getReleaseDate() { 
        return releaseDate; 
    }

    public boolean isActive() { 
        return active; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public void setLanguage(String language) { 
        this.language = language; 
    }

    public void setGenre(String genre) { 
        this.genre = genre; 
    }

    public void setDurationMinutes(int durationMinutes) { 
        this.durationMinutes = durationMinutes; 
    }

    public void setReleaseDate(LocalDate releaseDate) { 
        this.releaseDate = releaseDate; 
    }


    public void setActive(boolean active) { 
        this.active = active; 
    }

    @Override
    public String toString() {
        return "Movie{id=" + id + ", name=" + name + ", lang=" + language +
                ", genre=" + genre + "}";
    }
}
