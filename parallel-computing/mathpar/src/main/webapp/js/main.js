/* global requirejs */
console.log(window.location.pathname.indexOf('/ru/') >= 0 ? 'ru' : 'en');

requirejs.config({
  paths: {
    'underscore': 'libs/underscore',
    'jquery': 'libs/jquery',
    'jquery.autosize': 'libs/jquery.autosize',
    'jquery.cookie': 'libs/jquery.cookie',
    'jquery.form': 'libs/jquery.form',
    'jquery.pubsub': 'libs/jquery.pubsub',
    'bootstrap': 'libs/bootstrap',
    'bootstrap-slider': 'libs/bootstrap-slider',
    'three': 'libs/three.js/three',
    'three-detector': 'libs/three.js/Detector',
    'three-projector': 'libs/three.js/Projector',
    'three-canvas': 'libs/three.js/CanvasRenderer',
    'three-orbit-controls': 'libs/three.js/OrbitControls',
    'three-window-resize': 'libs/three.js/THREEx.WindowResize',
    'three-font-helvetiker': 'libs/three.js/helvetiker_regular.typeface',
    'text': 'libs/requirejs_plugins/text',
    'i18n': 'libs/requirejs_plugins/i18n',
    'tpl': 'libs/requirejs_plugins/tpl'
  },
  shim: {
    'underscore': {exports: '_'},
    'three': {exports: 'THREE'},
    'three-detector': {exports: 'Detector'},
    'three-projector': ['three'],
    'three-canvas': ['three', 'three-projector'],
    'three-orbit-controls': ['three'],
    'three-window-resize': {exports: 'THREEx'},
    'three-font-helvetiker': ['three'],
    'bootstrap': ['jquery'],
    'bootstrap-slider': ['jquery'],
    'jquery.autosize': ['jquery'],
    'jquery.cookie': ['jquery'],
    'jquery.form': ['jquery'],
    'jquery.pubsub': ['jquery']
  },
  tpl: {
    extension: '.tpl', // default = '.html'
    path: 'templates/'
  },
  i18n: {
    // TODO: determine locale somehow
    locale: window.location.pathname.indexOf('/ru/') >= 0 ? 'ru' : 'en'
  },
  //urlArgs: "bust=" + (new Date()).getTime()   // For development
  urlArgs: "bust=v20150613"                            // For production
});

requirejs([
  'jquery', 'mathpar',
  // Load jQuery plugins at once.
  'bootstrap', 'bootstrap-slider', 'jquery.autosize', 'jquery.cookie', 'jquery.form', 'jquery.pubsub'
], function ($, mathpar) {
  'use strict';

  $(function () {
    $.fx.off = true;

    mathpar.runApp();
  });
});
