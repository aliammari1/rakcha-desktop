package com.esprit.exceptions;

/**
 * Exception thrown when a user attempts to reuse a recent password.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PasswordReusedException extends Exception {

    private final int historyLimit;

    /**
     * Constructs a new PasswordReusedException.
     *
     * @param historyLimit the number of previous passwords that cannot be reused
     */
    public PasswordReusedException(int historyLimit) {
        super(String.format("Cannot reuse any of your last %d passwords. Please choose a different password.",
            historyLimit));
        this.historyLimit = historyLimit;
    }

    /**
     * Gets the password history limit.
     *
     * @return the number of passwords in history
     */
    public int getHistoryLimit() {
        return historyLimit;
    }
}
