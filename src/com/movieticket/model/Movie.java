package com.movieticket.model;

import java.time.LocalDate;

public class Movie {
    
    private final long id;
    private String name;
    private String description;
    private String language;
    private String genre;
    private int durationMinutes;
    private LocalDate releaseDate;
    private double rating;
    private boolean active;

    public Movie(long movieId, String name, String description, String language,
                 String genre, int durationMinutes, LocalDate releaseDate, double rating) {
        this.id = movieId;
        this.name = name;
        this.description = description;
        this.language = language;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
        this.rating = rating;
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

    public int getDurationMinutes() { 
        return durationMinutes; 
    }

    public LocalDate getReleaseDate() { 
        return releaseDate; 
    }

    public double getRating() { 
        return rating; 
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

    public void setRating(double rating) { 
        this.rating = rating; 
    }

    public void setActive(boolean active) { 
        this.active = active; 
    }

    @Override
    public String toString() {
        return "Movie{id=" + id + ", name=" + name + ", lang=" + language +
                ", genre=" + genre + ", rating=" + rating + "}";
    }
}
