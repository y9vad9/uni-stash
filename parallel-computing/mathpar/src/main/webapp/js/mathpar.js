define([
  'jquery', 'sidebar', 'sections'
], function($, sidebar, sections) {
  'use strict';

  function runApp() {
    var loadingBar = $('.loading');

    sidebar.init();
    sections.init();

    $(document).ajaxStart(function() {
      loadingBar.addClass('active');
    });
    $(document).ajaxStop(function() {
      loadingBar.removeClass('active');
    });
    $(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
      loadingBar.removeClass('active');

//      console.log('ajax error');
//      console.log('event', event);
//      console.log('jqXHR', jqXHR);
//      console.log('settings', ajaxSettings);
//      console.log('error thrown', thrownError);
    });
  }

  return {
    runApp: runApp
  };
});
