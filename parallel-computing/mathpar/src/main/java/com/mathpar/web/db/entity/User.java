package com.mathpar.web.db.entity;

import com.mathpar.web.db.util.SecurityUtil;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

public class User {
    private long id;
    private String username;
    private String email;
    private String salt;
    private String password;
    private Date registrationDate = new Date();
    private UserRole role;

    public User() {
    }

    public User(String username, String email, String password) throws NoSuchAlgorithmException {
        this.username = username;
        this.email = email;
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] bSalt = new byte[8];
        random.nextBytes(bSalt);
        this.salt = SecurityUtil.byteToBase64(bSalt);
        this.password = SecurityUtil.byteToBase64(SecurityUtil.getHash(password, bSalt));
    }

    public User(String username, String email, String password, UserRole role)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this(username, email, password);
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (!email.equals(user.email)) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (!password.equals(user.password)) return false;
        if (!registrationDate.equals(user.registrationDate)) return false;
        if (role != user.role) return false;
        if (!salt.equals(user.salt)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + email.hashCode();
        result = 31 * result + salt.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + registrationDate.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + username + '\'' +
                ", email='" + email + '\'' +
                ", salt='" + salt + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate=" + registrationDate +
                ", role=" + role +
                '}';
    }
}
