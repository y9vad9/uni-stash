({
  // All options description: https://github.com/jrburke/r.js/blob/master/build/example.build.js
  appDir: '${basedir}/src/main/webapp/js',
  baseUrl: './',
  // webapp (future WAR-file) directory
  dir: '${project.build.directory}/requirejs-min/',
  optimize: 'uglify2',
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
    locale: 'root'
  },
  optimizeCss: 'standard',
  // === Don't copy mathjax to 'dir' directory =============
  fileExclusionRegExp: /^mathjax$/,
  // Inlines any text! dependencies, to avoid separate requests.
  inlineText: true,
  // Modules to stub out in the optimized file.
  stubModules: ['text', 'tpl'],
  // Files combined into a build layer will be removed from the output folder.
  removeCombined: true,
  // This option will turn off the auto-preservation.
  preserveLicenseComments: true,
  //List the modules that will be optimized.
  modules: [
    {
      name: 'main'
    }
  ]
})
