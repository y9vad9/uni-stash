define([
  'jquery', 'util', 'i18n!nls/msg', 'tpl!login_form'
], function ($, U, msg, loginFormTpl) {
  'use strict';

  var
      el = null,
      isLoggedIn = false,
      userRole = '';

  // TODO: remove explicit init().
  function init() {
    el = $("#kbd_login");
    loadLoginForm();
    initSigninBtn();
    initLogoutBtn();
    initSignupBtn();
  }

  function loadLoginForm() {
    return $.ajax({
      url: U.url('/api/auth/isloggedin'),
      type: 'POST',
      contentType: "application/json",
      dataType: 'json'
    }).done(renderLoginForm);
  }

  function renderLoginForm(dataOrXhr, textStatus, xhrOrError) {
    if (textStatus === "error") {
      console.log('textStatus === error; error message:', dataOrXhr.responseJSON.message);
      $('#login_status').html(dataOrXhr.responseJSON.message);
    } else {
      // TODO: remove logic from template. Split templates to several.
      if (dataOrXhr.status === U.status.ERROR && dataOrXhr.errorMsg) {
        $('#login_status').html(dataOrXhr.errorMsg);
        return;
      }

      if (dataOrXhr.status === U.status.ERROR && !dataOrXhr.errorMsg
          && dataOrXhr.userRole === U.userRole.ANONYMOUS) {
        U.pub(U.topic.LOGOUT_SUCCESS, dataOrXhr);
      }

      if (dataOrXhr.status === U.status.OK) {
        U.pub(U.topic.LOGIN_SUCCESS, dataOrXhr);
      }

      el.html(loginFormTpl({
        loggedIn: dataOrXhr.status === U.status.OK,
        userRole: dataOrXhr.userRole,
        helloTo: dataOrXhr.username || dataOrXhr.email,
        msg: msg
      }));
    }
  }

  function initSigninBtn() {
    el.on('click', '#sign-in', function (ev) {
      ev.preventDefault();
      $.ajax({
        url: U.url('/api/auth/login'),
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({
          email: $('#login-form input[name="email"]').val(),
          password: $('#login-form input[name="password"]').val()
        })
      }).always(renderLoginForm);
    });
  }

  function initLogoutBtn() {
    el.on('click', '#btn_logout', function (ev) {
      ev.preventDefault();
      $.ajax({
        url: U.url('/api/auth/logout'),
        type: 'POST',
        contentType: "application/json",
        dataType: 'json'
      }).always(renderLoginForm);
    });
  }

  function initSignupBtn() {
    el.on('click', '#sign-up', function (ev) {
      ev.preventDefault();
      $.ajax({
        url: U.url('/api/auth/register'),
        type: 'POST',
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({
          email: $('#signup-form input[name="email"]').val(),
          username: $('#signup-form input[name="username"]').val(),
          password: $('#signup-form input[name="password"]').val()
        })
      }).always(renderLoginForm);
    });
  }

  // export public module functions
  return {
    init: init,
    render: renderLoginForm
  };
});
