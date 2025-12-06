define([
  'jquery', 'util'
], function ($, util) {
  'use strict';

  /**
   * @param sectionId
   * @param imageBelow
   * @constructor
   */
  var Mathpar3D = function (sectionId, imageBelow) {
    this.sectionId = sectionId;
    this.$imageBelow = imageBelow;
    this.points3D = [[-20, -20, -20], [-20, 20, -20], [20, -20, -20], [20, 20, -20],
      [-20, -20, 20], [-20, 20, 20], [20, -20, 20], [20, 20, 20]];
    // Initial cube projection points.
    this.points2D = [[0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
      [0, 0]];
    // Initial matrix of transformations for plots.
    this.matrix = [[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]];
    this.oldX = 0;
    this.oldY = 0;
    this.mouseDown = false;
    this.shiftDown = false;
  };

// s(scale?) = 500; width = 1000; height = 700;
  Mathpar3D.prototype.SPEED = 100;
  Mathpar3D.prototype.MATRIX_PROJ = [
    [500, 0, 500, 250000],
    [0, 500, 350, 175000],
    [0, 0, 1, 0],
    [0, 0, 1, 500]
  ];

  Mathpar3D.prototype.init = function () {
    var
        this_ = this,
        sectionId = this_.sectionId,
        canvasCheck = $('#section_' + sectionId + '>canvas.plot3d');

    if (canvasCheck.length === 0) {
      this_.canvas = $('<canvas class="plot3d">');
    } else {
      this_.canvas = canvasCheck;
    }
    // Canvas has absolute position so append it before image to place on top.
    this_.canvas.insertBefore(this_.$imageBelow);
    this_.getMatrix3D();

    this_.canvas.on('mousedown', function (event) {
      this_.mouseDown = (event.button === 0 && event.which === 1);
    });
    this_.canvas.on('mouseup', function () {
      this_.mouseDown = false;
      this_.shiftDown = false;
      this_.update3d();
    });
    $(window).on('keydown', function (evt) {
      this_.shiftDown = evt.keyCode === 16;
    });
    $(window).on('keyup', function (evt) {
      this_.shiftDown = false;
    });
    this_.canvas.on('mousemove', function (event) {
      if (!this_.mouseDown) {
        return;
      }
      var parent = this.offsetParent;
      var newX = event.pageX - parent.offsetLeft;
      var newY = event.pageY - parent.offsetTop;
      var alpha = (newY - this_.oldY) / this_.SPEED;
      var betta = -(newX - this_.oldX) / this_.SPEED;
      if (alpha < 360 && betta < 360) {
        this_.calc(alpha, betta, -alpha);
      }
      this_.oldX = event.pageX - parent.offsetLeft;
      this_.oldY = event.pageY - parent.offsetTop;
    });

    // Animate cube.
    // TODO: replace setInterval() somehow.
    setInterval(function () {
      this_.draw();
    }, 100);
  };

  Mathpar3D.prototype.getMatrix3D = function () {
    var this_ = this;

    $.ajax({
      url: util.url('/servlet/matrix3d?section_number=' + this_.sectionId),
      type: 'GET',
      dataType: 'text',
      success: function (response) {
        var respParts = response.split('*');
        this_.matrix = $.parseJSON(respParts[0]);
        this_.points3D = $.parseJSON(respParts[1]);
        this_.points3D[7][0] += this_.points3D[6][0];
        this_.points3D[7][1] += this_.points3D[6][1];
        this_.points3D[7][2] += this_.points3D[5][2];
      }
    });
  };

  Mathpar3D.prototype.setMatrix3D = function () {
    var
        sectionId = this.sectionId,
        matrixString = this.matrix.toString();

    $.ajax({
      type: 'POST',
      url: util.url('/servlet/matrix3d?section_number=' + sectionId),
      dataType: 'text',
      data: 'matrix=' + matrixString
    });
  };

  Mathpar3D.prototype.draw = function () {
    var
        canvas = this.canvas.get(0), // Get DOM node.
        mouseDown = this.mouseDown,
        points2D = this.points2D,
        ctx = canvas && canvas.getContext('2d');

    if (!canvas || !ctx) {
      return;
    }
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (!mouseDown) {
      return;
    }
    ctx.fillStyle = "rgb(128,0,0)";
    ctx.strokeStyle = "black";
    ctx.beginPath();
    ctx.moveTo(points2D[0][0], points2D[0][1]);
    ctx.lineTo(points2D[1][0], points2D[1][1]);
    ctx.lineTo(points2D[2][0], points2D[2][1]);
    ctx.lineTo(points2D[6][0], points2D[6][1]);
    ctx.lineTo(points2D[0][0], points2D[0][1]);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(points2D[5][0], points2D[5][1]);
    ctx.lineTo(points2D[7][0], points2D[7][1]);
    ctx.lineTo(points2D[3][0], points2D[3][1]);
    ctx.lineTo(points2D[4][0], points2D[4][1]);
    ctx.lineTo(points2D[5][0], points2D[5][1]);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(points2D[5][0], points2D[5][1]);
    ctx.lineTo(points2D[0][0], points2D[0][1]);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(points2D[7][0], points2D[7][1]);
    ctx.lineTo(points2D[6][0], points2D[6][1]);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(points2D[3][0], points2D[3][1]);
    ctx.lineTo(points2D[2][0], points2D[2][1]);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(points2D[4][0], points2D[4][1]);
    ctx.lineTo(points2D[1][0], points2D[1][1]);
    ctx.stroke();
    for (var i = 0; i < 8; i++) {
      ctx.beginPath();
      ctx.arc(points2D[i][0], points2D[i][1], 2, 0, Math.PI * 2, false);
      ctx.closePath();
      ctx.fill();
    }
    ctx.restore();
  };

  Mathpar3D.prototype.projection = function () {
    var
        matrix = this.matrix,
        points3D = this.points3D,
        points2D = this.points2D;

    for (var j = 0; j < 8; j++) {
      var x = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];
      for (var i = 0; i < 4; i++) {
        x[i] = matrix[i][0] * points3D[j][0] + matrix[i][1] * points3D[j][1]
            + matrix[i][2] * points3D[j][2] + matrix[i][3];
      }
      points2D[j][0] = x[0] / x[3];
      points2D[j][1] = x[1] / x[3];
    }
  };

  Mathpar3D.prototype.calc = function (alpha, beta, scale) {
    var
        sin = 0,
        cos = 0,
        shiftDown = this.shiftDown,
        matrixOld;

    if (alpha !== 0 && !shiftDown) {
      cos = Math.cos(alpha);
      sin = Math.sin(alpha);
      var na = [[1, 0, 0, 0], [0, cos, -sin, 0], [0, sin, cos, 0], [0, 0, 0, 1]];
      this.matrixMult(na);
    }
    if (beta !== 0 && !shiftDown) {
      cos = Math.cos(beta);
      sin = Math.sin(beta);
      var nb = [[cos, 0, sin, 0], [0, 1, 0, 0], [-sin, 0, cos, 0], [0, 0, 0, 1]];
      this.matrixMult(nb);
    }
    if (shiftDown) {
      var ns = [[1 + scale, 0, 0, 0], [0, 1 + scale, 0, 0], [0, 0, 1 + scale, 0],
        [0, 0, 0, 1]];
      this.matrixMult(ns);
    }
    matrixOld = this.matrix;
    this.matrixMult(this.MATRIX_PROJ);
    this.projection();
    this.matrix = matrixOld;
  };

  Mathpar3D.prototype.update3d = function () {
    var
        this_ = this,
        section = $('#section_' + this_.sectionId);
    this_.setMatrix3D();
    util.submitToCalc({
      sectionId: this.sectionId,
      task: section.find('textarea').val()
    }).then(function (resp) {
      if (util.showError(resp, section)) {
        return;
      }
      section.find('img.plot').attr('src', util.getImageUrl(this_.sectionId));
    }, function (xhr, status, error) {
      util.showError({status: util.status.ERROR, error: status}, section);
    });
  };

  Mathpar3D.prototype.matrixMult = function (otherMatrix) {
    var M = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];
    for (var i = 0; i < 4; i++) {
      for (var j = 0; j < 4; j++) {
        for (var k = 0; k < 4; k++) {
          M[i][j] += this.matrix[k][j] * otherMatrix[i][k];
        }
      }
    }
    this.matrix = M;
  };

  return Mathpar3D;
});
