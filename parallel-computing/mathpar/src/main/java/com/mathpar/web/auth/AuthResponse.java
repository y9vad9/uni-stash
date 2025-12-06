package com.mathpar.web.auth;

import com.mathpar.web.db.entity.UserRole;
import com.mathpar.web.entity.ResponseStatus;

/**
 * Authentication response.
 */
public class AuthResponse {
    public String email;
    public String username;
    public UserRole userRole;
    public ResponseStatus status;
    public String errorMsg;

    public AuthResponse(String email, String username, UserRole userRole,
                        ResponseStatus status) {
        this.email = email;
        this.username = username;
        this.userRole = userRole;
        this.status = status;
    }

    public AuthResponse(String email, String username, UserRole userRole,
                        ResponseStatus status, String errorMsg) {
        this(email, username, userRole, status);
        this.errorMsg = errorMsg;
    }
}
