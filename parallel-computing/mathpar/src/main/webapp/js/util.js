define([
  'jquery', 'i18n!nls/msg', 'tpl!global_alert'
], function ($, msg, alertTpl) {
  'use strict';

  var
      topic = {
        LOGIN_SUCCESS: 'LOGIN_SUCCESS',
        LOGIN_ERROR: 'LOGIN_ERROR',
        LOGOUT_SUCCESS: 'LOGOUT_SUCCESS',
        LOGOUT_ERROR: 'LOGOUT_ERROR'
      },
      status = {
        OK: 'OK',
        ERROR: 'ERROR',
        WARNING: 'WARNING'
      },
      userRole = {
        ANONYMOUS: 'ANONYMOUS',
        REGISTERED: 'REGISTERED',
        STUDENT: 'STUDENT',
        TEACHER: 'TEACHER',
        EDU_ADMIN: 'EDU_ADMIN',
        CLUSTER_USER: 'CLUSTER_USER',
        SUPERADMIN: 'SUPERADMIN'
      },
      authActions = {
        REGISTER: 'register',
        LOGIN: 'login',
        LOGOUT: 'logout',
        ISLOGGEDIN: 'isloggedin' // do GET request for this.
      },
      alertClasses = {
        OK: 'success',
        ERROR: 'danger',
        WARNING: 'warning'
      },
      locale = '',
      CONTEXT_PATH = '${mathparContext}';

  function log(anything) {
    if (window.console) {
      window.console.log(anything);
    }
  }

  function sub(topic, callback) {
    $.pubsub('subscribe', topic, callback);
  }

  function pub(topic, data) {
    $.pubsub('publish', topic, data);
  }

  /**
   * POSTs given text to Calculation servlet.
   * @param {Object} data data to POST. Contains fields:
   * sectionId -- section ID,
   * task -- String with text of the task to POST.
   *
   * @returns {jqXHR}
   */
  function submitToCalc(data) {
    return $.ajax({
      url: url('/api/calc'),
      type: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      data: JSON.stringify(data)
    });
  }

  function showError(data, section) {
    var
        errorMsg = data.error || msg.global['errorUnknown'],
        output = section.find('.res_panel');
    if (data.status !== status.ERROR) {
      return false;
    }
    output.html('<pre>' + msg.global['error'] + ': ' + errorMsg + '</pre>');
    return true;
  }

  /**
   * Shows notification
   * @param {String} message message to show.
   * @param {String} _status
   * @returns {undefined}
   */
  function showNotification(message, _status) {
    var
        status = _status || 'INFO',
        time = new Date().toLocaleTimeString(),
        div = $('<div></div>').insertBefore('#hr').html(alertTpl({
          alertClass: alertClasses[status] || 'info',
          message: message
        }));
    $('#global_log').append(_.template('<p>[<%=status%>] [<%=time%>] <%=message%></p>',
        {status: status, time: time, message: message}));
    if (status === 'INFO') {
      window.setTimeout(function () {
        div.hide(400, function () {
          div.remove();
        });
      }, 3000);
    }
  }

  /**
   * @param {String} path
   * @returns {String} URL containing Mathpar context path + given path
   */
  function url(path) {
    var localeStr = '';
    if (locale) {
      localeStr = ((path.indexOf('?') >= 0) ? '&' : '?') + 'locale=' + locale;
    }
    return CONTEXT_PATH + ((path.charAt(0) === '/') ? path : '/' + path) + localeStr;
  }

  /**
   * @param {Number} sectionId
   * @param {Number} frame number
   * @returns URL for image in section with given ID. It prevents caching with timestamp.
   */
  function getImageUrl(sectionId, frame) {
    return url('/servlet/image?section_number='
            + sectionId
            + (frame ? '&frame=' + frame : '')
            + '&timestamp=' + new Date().getTime() // Don't cache ever!
    );
  }

  function getFramesNumber(sectionId) {
    return $.ajax({
      url: url('/servlet/image'),
      type: 'GET',
      dataType: 'text',
      data: 'section_number=' + sectionId + '&getFramesNumber=true'
    });
  }

  /**
   * Inserts given text at cursor position of given textarea element.
   *
   * @param {DomNode} el textarea DOM element.
   * @param {string} text text to insert.
   * @param {number} cursorBackTo if not undefined, cursor goes back to given number of symbols.
   */
  function insertTextAtCursor(el, text, cursorBackTo) {
    var
        val = el.value,
        endIndex,
        range,
        back = cursorBackTo ? parseInt(cursorBackTo, 10) : 0;
    text = text.replace(/\\newline/g, '\n'); // For ability to insert newlines.
    if (typeof el.selectionStart !== 'undefined'
        && typeof el.selectionEnd !== 'undefined') { // Good browsers
      el.focus();
      endIndex = el.selectionEnd;
      el.value = val.slice(0, endIndex) + text + val.slice(endIndex);
      // cursor goes back.
      el.selectionStart = el.selectionEnd = endIndex + text.length - back;
    } else if (typeof document.selection !== 'undefined'
        && typeof document.selection.createRange !== 'undefined') { // IE
      el.focus();
      range = document.selection.createRange();
      range.collapse(false);
      range.text = text;
      range.select();
      // cursor goes back
      range.moveEnd('character', -back);
      range.moveStart('character', -back + 1);
      range.select();
    }
    if ($.fn.autosize) {
      $(el).trigger('resize.autosize');
    }
  }

  return {
    authActions: authActions,
    topic: topic,
    status: status,
    userRole: userRole,
    submitToCalc: submitToCalc,
    getFramesNumber: getFramesNumber,
    getImageUrl: getImageUrl,
    insertTextAtCursor: insertTextAtCursor,
    pub: pub,
    sub: sub,
    log: log,
    url: url,
    showError: showError,
    showNotification: showNotification
  };
});
