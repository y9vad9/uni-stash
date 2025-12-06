<% if(!loggedIn) { %>
  <ul class="nav nav-tabs">
    <li class="active"><a href="#login-form" data-toggle="tab"><%- msg.login_form['login'] %></a></li>
    <li><a href="#signup-form" data-toggle="tab"><%- msg.login_form['sign up'] %></a></li>
  </ul>
  <div class="tab-content">
    <div class="tab-pane fade active in" id="login-form">
      <form action="../servlet/auth" method="POST">
        <input class="form-control" type="email" name="email"
               placeholder="<%- msg.login_form['email'] %>"
               title="<%- msg.login_form['email'] %>">
        <input class="form-control" type="password" name="password"
               placeholder="<%- msg.login_form['password'] %>"
               title="<%- msg.login_form['password'] %>">
        <button id="sign-in" class="btn btn-default"><%- msg.login_form['login'] %></button>
      </form>
    </div> <!-- /login-form -->
    <div class="tab-pane fade" id="signup-form">
      <form action="../servlet/auth" method="POST">
        <input class="form-control" type="email" name="email"
               placeholder="<%- msg.login_form['email'] %>"
               title="<%- msg.login_form['email'] %>">
        <input class="form-control" type="text" name="username"
               placeholder="<%- msg.login_form['username'] %>"
               title="<%- msg.login_form['username'] %>">
        <input class="form-control" type="password" name="password"
               placeholder="<%- msg.login_form['password'] %>"
               title="<%- msg.login_form['password'] %>">
        <button id="sign-up" class="btn btn-default"><%- msg.login_form['sign up'] %></button>
      </form>
    </div> <!-- /signup-form -->
  </div> <!-- /tab-content -->
  <p id="login_status" class="text-danger"></p>
<% } else { %>
  <div class="well well-small">
    <p><%- msg.login_form['hello'] %>, <%- helloTo %>, <%- userRole %></p>
    <button id="btn_logout" class="btn btn-danger"><%- msg.login_form['logout'] %></button>
  </div>
<% } %>
