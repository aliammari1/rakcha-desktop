package com.esprit.exceptions;

import com.esprit.models.cinemas.MovieSession;

/**
 * Exception thrown when a movie session conflicts with an existing session in
 * the same hall.
 */
public class SessionConflictException extends RuntimeException {

    private final MovieSession conflictingSession;

    public SessionConflictException(String message, MovieSession conflictingSession) {
        super(message);
        this.conflictingSession = conflictingSession;
    }

    public MovieSession getConflictingSession() {
        return conflictingSession;
    }
}
