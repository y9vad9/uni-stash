package com.mathpar.web.auth;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import com.mathpar.web.controller.PageParam;
import com.mathpar.web.db.dao.DbUser;
import com.mathpar.web.db.entity.User;
import com.mathpar.web.db.entity.UserRole;
import com.mathpar.web.entity.ResponseStatus;
import com.mathpar.web.exceptions.AuthException;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@RestController
@RequestMapping(value = "/api/auth", method = RequestMethod.POST)
public class Auth {
    private static final Logger LOG = getLogger(Auth.class);

    private DbUser dbUser;

    @Autowired
    public void setDbUser(DbUser dbUser) {
        this.dbUser = dbUser;
    }

    @RequestMapping("/isloggedin")
    public AuthResponse isLoggedIn(HttpSession s, Locale locale, @PageParam Page page) {
        User user = page.getUser();
        if (user == null) {
            return new AuthResponse(null, null, UserRole.ANONYMOUS, ResponseStatus.ERROR);
        } else {
            return new AuthResponse(user.getEmail(), user.getUsername(), user.getRole(), ResponseStatus.OK);
        }
    }

    @RequestMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest req, @PageParam Page page) {
        try {
            LOG.debug("Register request: {}", req);
            AuthRequest cleanReq = clean(req);
            LOG.debug("Cleaned register request: {}", cleanReq);
            validate(cleanReq);

            // TODO: remove stub for user role STUDENT
            User u = new User(cleanReq.getUsername(), cleanReq.getEmail(),
                    cleanReq.getPassword(), UserRole.STUDENT);
            long newUserId = dbUser.save(u);
            u.setId(newUserId);
            // TODO: insert to students only users with STUDENT role.
            dbUser.insertIntoStudents(newUserId, 1);
            page.setUser(u);
            page.setClusterQueryCreator(new QueryCreator(req.getUsername(),req.getPassword()));
            return new AuthResponse(cleanReq.getEmail(), cleanReq.getUsername(), u.getRole(), ResponseStatus.OK);
        } catch (IllegalArgumentException e) {
            return new AuthResponse(null, null, null, ResponseStatus.ERROR, e.getMessage());
        } catch (Exception e) {
            throw new AuthException("Error registering new user", e);
        }
    }

    private AuthRequest clean(AuthRequest req) {
        AuthRequest r = new AuthRequest();
        r.setEmail(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : "");
        r.setUsername(req.getUsername() != null ? req.getUsername().trim() : "");
        r.setPassword(req.getPassword());
        return r;
    }

    private void validate(AuthRequest req) {
        String email = req.getEmail();
        if (!(StringUtils.hasText(email))) {
            throw new IllegalArgumentException("Email can not be empty.");
        }
        if (!validateEmail(email)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        if (!(StringUtils.hasText(req.getUsername()))) {
            throw new IllegalArgumentException("Username can not be empty.");
        }
        if (!(StringUtils.hasText(req.getPassword()))) {
            throw new IllegalArgumentException("Password can not be empty.");
        }
    }

    private boolean validateEmail(String email) {
        boolean isValid = false;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            // Empty.
        }
        return isValid;
    }

    @RequestMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req, @PageParam Page page) {
        try {
            User u = dbUser.getUser(req.getEmail(), req.getPassword());            
            page.setUser(u);            
            String curUName=dbUser.getUsernameByUserId(u.getId());
            page.setClusterQueryCreator(new QueryCreator(curUName,req.getPassword()));
            return new AuthResponse(u.getEmail(), u.getUsername(), u.getRole(), ResponseStatus.OK);
        } catch (AuthException e) {
            LOG.warn("Authentication exception for " + req.getEmail(), e);
            return new AuthResponse(null, null, UserRole.ANONYMOUS, ResponseStatus.ERROR, e.getLocalizedMessage());
        } catch (IOException | NoSuchAlgorithmException e) {
            return new AuthResponse(null, null, UserRole.ANONYMOUS, ResponseStatus.ERROR);
        }
    }

    @RequestMapping("/logout")
    public AuthResponse logout(@PageParam Page page) {
        page.setUser(null);
        return new AuthResponse(null, null, UserRole.ANONYMOUS, ResponseStatus.ERROR);
    }
}
