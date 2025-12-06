define([
  'jquery', 'util', 'sections', 'i18n!nls/msg', 'tpl!filelist'
], function ($, util, sections, msg, filelistTpl) {
  'use strict';

  var
          el = null;

  function init() {
    el = $('#filelist-container');

    update();

    el.on('click', '.filename', function (e) {
      e.preventDefault();
      sections.insertTextToActiveSection(this.innerText);
      return false;
    });

    el.on('click', '.download', function (e) {
      var filenameEncoded = encodeURIComponent(
              $(this).parent().prev().find('.filename').text());
      e.preventDefault();
      window.open(util.url('/api/files') + '?filename=' + filenameEncoded
              + '&download=true');
      return false;
    });

    el.on('click', '.delete', function (e) {
      var filenameEncoded = encodeURIComponent(
              $(this).parent().prev().prev().find('.filename').text());
      e.preventDefault();
      $.ajax({
        url: util.url('/api/files?filename=' + filenameEncoded),
        type: 'DELETE',
        dataType: 'json',
        contentType: 'application/json'
      }).done(render);
      return false;
    });
  }

  function update() {
    $.ajax({
      url: util.url('/api/files'),
      type: 'GET',
      dataType: 'json'
    }).done(render);
  }

  function render(uploadResp) {
    el.html(filelistTpl({filelist: uploadResp.filenames, msg: msg}));
  }

  return {
    init: init,
    update: update,
    render: render
  };
});
