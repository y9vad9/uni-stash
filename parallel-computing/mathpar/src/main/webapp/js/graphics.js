define([
  'jquery', 'underscore', 'util', 'i18n!nls/msg', 'tpl!graphics_addons',
  'tableplot_ui', 'mathpar3d', 'plot3d_implicit', 'plot3d_explicit', 'plot3d_parametric', 
  'ms3drender' // render of multiple surfaces
], function ($, _, util, msg, graphAddonsTpl,
             TablePlotUi, Mathpar3d, Plot3dImplicit,
             Plot3dExplicit, Plot3dParametric, RenderMultipleSurfaces) {
  'use strict';

  var
      SEL_FRAME = '.plot-frame',
      CLASS_FRAME = 'plot-frame',
      DEFAULT_SETTINGS = [false, 3, 3, 1];

  // Fit canvas to image width.
  $(window).on('resize', function () {
    var canvas = $('canvas');
    if (canvas.length !== 0) {
      canvas.each(function (index) {
        var img = $(this).siblings('.plot');
        this.width = img.width();
        this.height = img.height();
      });
    }
  });

  /**
   * @param response MathparResponse object.
   * @returns {Graphics}
   * @constructor
   */
  var Graphics = function (response) {
    this.response = response;
    this.initData();
    this.init();
    return this;
  };

  Graphics.prototype.initData = function () {
    var task = this.response.task;

    this.sectionId = this.response.sectionId;
    this.section = $('#section_' + this.sectionId);
    this.isGraph = containsGraphCommand(task);
    this.isTablePlot = containsTableplotCommand(task);
    this.isPlot3d = task.indexOf('\\plot3d') >= 0 || task.indexOf('\\paramPlot3d') >= 0;
    this.isPlot3dImplicit = task.indexOf('\\implicitPlot3d') >= 0;
    this.isPlot3dExplicit = task.indexOf('\\explicitPlot3d') >= 0;
    this.isPlot3dParametric = task.indexOf('\\parametricPlot3d') >= 0;
    this.isRenderMultipleSurfaces = task.indexOf('\\show3d') >= 0;
    this.imgPath = this.isGraph || this.isTablePlot ? util.getImageUrl(this.sectionId) : '';
    this.oldParameters = [];
    this.oldSettings = DEFAULT_SETTINGS;
    this.parameters = [];
    this.settings = [];
    this.ringParameters = require('sidebar').getSpaceParameters(this.isPlot3d || 
            this.isPlot3dImplicit || this.isPlot3dExplicit || this.isPlot3dParametric || 
            this.isRenderMultipleSurfaces);
    // Are parameters or settings changed since last \replot() call?
    this.paramSettingsChanged = false;
    this.el = this.section.children('.graph-additional');
  };

  Graphics.prototype.init = function () {
    var
        this_ = this,
        replotBtn,
        parametersButtons,
        slider,
        withParameters = !this.isTablePlot && this.ringParameters.length > 0;

    this.el.remove();
    
    if (!(this.isGraph || this.isPlot3d || this.isPlot3dImplicit || 
            this.isPlot3dExplicit || this.isPlot3dParametric || this.isTablePlot ||
            this.isRenderMultipleSurfaces)) {
      return;
    }
    
    this.el = $('<div class="graph-additional">');
    this.graphAddonsTplData = {
      msg: msg,
      imgPath: this.imgPath,
      withParameters: withParameters,
      ringParameters: this.ringParameters,
      isTablePlot: this.isTablePlot
    };
    this.el.html(graphAddonsTpl(this.graphAddonsTplData));
    this.graphImg = this.el.find('.plot');
    replotBtn = this.el.find('.btn-replot');
    this.parametersButtons = parametersButtons = this.el.find('.parameters');
    slider = this.el.find('.parameter-slider');

    this.el.on('click', '.btn-download', _.bind(this._handleBtnDownload, this));
    if (!this.isTablePlot) {
      replotBtn.on('click', _.bind(this._handleBtnReplotNonTableplot, this));
    }

    if (withParameters) {
      // Reset active parameter switch to the first.
      parametersButtons.find('.btn.active').removeClass('active');
      parametersButtons.find('.btn').eq(0).addClass('active');
      parametersButtons.find('input[name="parameters"]').on('change', function () {
        slider.slider('setValue', parseFloat($(this).siblings('.val').text()));
      });
      // Delay slider stuff as it can be not rendered yet after filling
      // graph addons with html from template.
      _.defer(function () {
        slider.slider({min: 0, max: 1, step: 0.01, value: 1, tooltip: 'hide'});
        slider.on('slide', function (ev) {
          parametersButtons.find('.btn.active').find('.val').text(ev.value.toFixed(2));
        });
      });
    }

    if (this.isTablePlot) {
      this.tablePlotUi = new TablePlotUi(this.sectionId);
      this.lineTypeButtons = this.el.find('input[name="linetype"]');
      this.lineTypeButtons.on('change', function () {
        this_.tablePlotUi.setLineType(this_.lineTypeButtons.index($(this)));
      });
      replotBtn.on('click', _.bind(this._handleBtnReplotClickTableplot, this));
      this.tablePlotUi.init();
      this.tablePlotUi.appendCanvasTo(this.graphImg);
    }

    // Resize canvas to fit the image.
    this.graphImg.on('load', function () {
      $(window).trigger('resize');
    });
    this.el.appendTo(this.section);
    // Enable 3D stuff.
    if (this.isPlot3d) {
      this.mathpar3d = new Mathpar3d(this.sectionId, this.graphImg);
      this.mathpar3d.init();
    }
    if (this.isPlot3dImplicit) {
      this.plot3dImplicit = new Plot3dImplicit(this.sectionId, this.graphImg);
    }
    if (this.isPlot3dExplicit && this.isRenderMultipleSurfaces === false) {
      this.plot3dExplicit = new Plot3dExplicit(this.sectionId, this.graphImg);
    }
    if (this.isPlot3dParametric && this.isRenderMultipleSurfaces === false) {
      this.plot3dParametric = new Plot3dParametric(this.sectionId, this.graphImg);
    }
    
    if (this.isRenderMultipleSurfaces) {
      console.log('RenderMultipleSurfaces');
      this.RenderMultipleSurfaces = new RenderMultipleSurfaces(this.sectionId, this.graphImg);
    }
  };

  Graphics.prototype._handleBtnDownload = function (ev) {
    var framesNumber = parseInt(this.el.find('.frames-number input').val(), 10);
    ev.preventDefault();
    if (framesNumber > 1) {
      for (var i = 0; i < framesNumber; i++) {
        window.open(util.getImageUrl(this.sectionId, i) + '&download=true');
      }
    } else {
      window.open(this.imgPath + '&download=true');
    }
  };

  Graphics.prototype._getCommonSettings = function () {
    var el = this.el;
    return [
      el.find('.is-eqscale').prop('checked'),
      el.find('.is-bw').prop('checked'),
      el.find('.font-size input').val(),
      el.find('.line-thickness input').val(),
      el.find('.axes-thickness input').val()
    ];
  };

  Graphics.prototype._handleBtnReplotNonTableplot = function (ev) {
    var paramSettingsChanged,
        el = this.el,
        this_ = this;
    ev.preventDefault();
    this.settings = this._getCommonSettings();
    this.settings.push(el.find('.frames-number input').val());
    this.parameters = [];
    el.find('.parameters .val').each(function () {
      this_.parameters.push($(this).text());
    });

    paramSettingsChanged = this._areGraphParametersChanged();
    if (paramSettingsChanged) {
      this.oldParameters = this.parameters.slice(0);
      this.oldSettings = this.settings.slice(0);
      util.submitToCalc({
        sectionId: this.sectionId,
        task: '\\replot([' + this.parameters.join() + '], ' + this.settings.join() + ');'
      }).done(_.bind(this.okNonTableplotReplot, this, paramSettingsChanged));
    } else {
      this.okNonTableplotReplot(paramSettingsChanged, {});
    }
  };

  Graphics.prototype.loadFrames = function (paramSettingsChanged, framesNumberStr) {
    var
        framesNumber = parseInt(framesNumberStr, 10),
        images = paramSettingsChanged ? [this.graphImg.get(0)] : this.el.find(SEL_FRAME).get(),
        loadedImagesCnt = 0,
        loadedDfd = $.Deferred(),
        promiseData = {
          framesNumber: framesNumber,
          images: images,
          paramSettingsChanged: paramSettingsChanged
        };
    if (framesNumber > 1 && paramSettingsChanged) {
      for (var i = 1; i < framesNumber; i++) {
        images.push(new Image());
        images[i].src = util.getImageUrl(this.sectionId, i);
        images[i].className = CLASS_FRAME;
        images[i].onload = function () {
          loadedImagesCnt++;
          if (loadedImagesCnt === framesNumber - 1) {
            loadedDfd.resolve(promiseData);
          }
        };
      }
    } else {
      loadedDfd.resolve(promiseData);
    }
    return loadedDfd.promise();
  };

  /**
   * Animate images by showing them sequently with timeout.
   * @param {Object} data with frames number and Image array.
   * @returns {undefined}
   */
  Graphics.prototype.animate = function (data) {
    var
        graphsAddons_ = this.el.get(0),
        framesNumber = data.framesNumber,
        paramSettingsChanged = data.paramSettingsChanged,
        images = data.images,
        $images = this.graphImg.siblings(SEL_FRAME),
        currFrame = 0,
        timer;
    this.graphImg.removeClass(CLASS_FRAME).addClass('plot');
    $images.removeClass('active');
    if (paramSettingsChanged) {
      $images.remove();
      for (var i = framesNumber - 1; i >= 1; i--) {
        graphsAddons_.appendChild(images[i]);
      }
    }
    // TODO: proper animation when parameters didn't change (now it's reversed).
    timer = setInterval(function () {
      if (currFrame === framesNumber - 1) {
        clearInterval(timer);
        return;
      } else {
        images[currFrame].className = CLASS_FRAME;
        currFrame++;
      }
      images[currFrame].className = 'plot-frame active';
    }, 100);
  };

  Graphics.prototype.okNonTableplotReplot = function (paramSettingsChanged, resp) {
    // Don't load frames number if we just want to replay animation;
    // current frames number is this.settings[5].
    var firstPromise;
    if (util.showError(resp, this.section)) {
      return;
    }
    firstPromise = paramSettingsChanged ? util.getFramesNumber(this.sectionId) :
        $.Deferred().resolve(this.settings[5]).promise();
    if (paramSettingsChanged) {
      this.graphImg.attr('src', util.getImageUrl(this.sectionId));
    }
    this.parametersButtons.find('button').removeClass('active');
    this.parametersButtons.find('button').eq(0).addClass('active');
    firstPromise
        .then(_.bind(this.loadFrames, this, paramSettingsChanged))
        .done(_.bind(this.animate, this));
  };

  Graphics.prototype._handleBtnReplotClickTableplot = function (ev) {
    var
        constraints = this.tablePlotUi.getAllX(),
        settings = this._getCommonSettings();
    ev.preventDefault();
    util.submitToCalc({
      sectionId: this.sectionId,
      task: '\\replot(' + constraints.toString() + ', ' + settings.join() + ');'
    }).done(_.bind(this.okTableplotReplot, this));
  };

  Graphics.prototype.okTableplotReplot = function (resp) {
    // Reset active linetype to 'Add'
    if (util.showError(resp, this.section)) {
      return;
    }
    this.lineTypeButtons.removeClass('active');
    this.lineTypeButtons.eq(1).addClass('active');
    this.tablePlotUi = new TablePlotUi(this.sectionId);
    this.tablePlotUi.init();
    this.tablePlotUi.appendCanvasTo(this.graphImg);
    this.graphImg.attr('src', util.getImageUrl(this.sectionId));
  };

  /**
   * Checks if it were parameters for graphics plots changed.
   * @returns {Boolean} true if settings or parameters changed, false otherwise.
   */
  Graphics.prototype._areGraphParametersChanged = function () {
    return !(_.isEqual(this.oldParameters, this.parameters)
        && _.isEqual(this.oldSettings, this.settings));
  };

  /**
   * @param {String} task input task.
   * @returns {Boolean} true if task contains graphics command, false otherwise.
   */
  function containsGraphCommand(task) {
    return  task.indexOf('\\plot') >= 0
            || task.indexOf('\\textPlot') >= 0
            || task.indexOf('\\paramPlot') >= 0
            || task.indexOf('\\plotGraph') >= 0
            || task.indexOf('\\showPlots') >= 0
            || task.indexOf('\\paintElement') >= 0
            || task.indexOf('\\pointsPlot') >= 0
            || task.indexOf('\\arrowPlot') >= 0;
  }

  /**
   * @param {String} task input task.
   * @returns {Boolean} true if task contains tableplot command, false otherwise
   */
  function containsTableplotCommand(task) {
    return task.indexOf('\\tablePlot') >= 0
        && task.indexOf('\\showPlots') < 0
        && task.indexOf('\\tablePlot2') < 0
        && task.indexOf('\\tablePlot4') < 0;
  }

  return Graphics;
});
