define(['jquery'], function($) {
  var LineType = {
    CUT: 0,
    ADD: 1,
    REMOVE: 2,
    DELETE_MODE: 3
  };

  var TablePlotUi = function(sectionId) {
    this.sectionId = sectionId;
    this.lines = [];
    this.lineType = LineType.ADD;
    this.countrect = -1;
    this.current = -1;
    this.create = 0;
    this.drag = false;
    this.currentColor = "blue";
    this.flagDelete = false;
  };

  TablePlotUi.prototype.init = function() {
    var
            this_ = this,
            sectionId = this_.sectionId,
            canvasCheck = $('#section_' + sectionId + ' canvas.canvasPlot');

    canvasCheck.remove();
    this_.$canvas = $('<canvas class="canvasPlot">');
    this_.canvas = this_.$canvas.get(0);

    this_.$canvas.on('mousedown', function(evt) {
      mouseDown(evt, this_);
    });
    this_.$canvas.on('mouseup', function(evt) {
      mouseUp(evt, this_);
    });
    this_.$canvas.on('mousemove', function(evt) {
      mouseMove(evt, this_);
    });
    this_.$canvas.on('click', function(evt) {
      click(evt, this_);
    });

    setInterval(function() {
      this_.draw();
    }, 1000 / 60);
  };

  TablePlotUi.prototype.setLineType = function(lineType) {
    this.lineType = lineType;
    this.flagDelete = false;
    switch (lineType) {
      case LineType.CUT:
        this.currentColor = "green";
        break;
      case LineType.ADD:
        this.currentColor = "blue";
        break;
      case LineType.REMOVE:
        this.currentColor = "yellow";
        break;
      case LineType.DELETE_MODE:
        this.flagDelete = true;
        break;
    }
  };

  TablePlotUi.prototype.appendCanvasTo = function(imageBelow) {
    // Canvas has absolute position so append it before image to place on top.
    this.$canvas.insertBefore(imageBelow);
  };

  TablePlotUi.prototype.draw = function() {
    var canvas = this.canvas,
            context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);
    for (var i = 0, sz = this.lines.length; i < sz; i++) {
      this.lines[i].draw();
    }
  };

  /**
   * @returns {Array} X coordinates of all vertical markers (unsorted).
   */
  TablePlotUi.prototype.getAllX = function() {
    var cut = [],
            add = [],
            remove = [],
            lines = this.lines,
            widthFactor = 1366.0 / this.canvas.width, // magic number from showgraph.Plots
            sz = lines.length,
            i = 0,
            currX, currType, currLine;
    for (; i < sz; i++) {
      currLine = lines[i];
      currX = Math.round(currLine.x * widthFactor);
      currType = currLine.type;
      switch (currType) {
        case LineType.CUT:
          cut.push(currX);
          break;
        case LineType.ADD:
          add.push(currX);
          break;
        case LineType.REMOVE:
          remove.push(currX);
          break;
      }
    }
    return {
      cut: cut,
      add: add,
      remove: remove,
      linesCount: sz,
      toString: function() {
        return '[' + add.toString() + '],' + '[' + remove.toString() + '],'
                + '[' + cut.toString() + ']';
      }
    };
  };

// Метод срабатывающий на нажатие кнопки мыши
  function mouseDown(evt, tablePlotUi) {
    tablePlotUi.create = 0;
    var mouseX = relMouseCoords(tablePlotUi.canvas, evt).x;
    if (tablePlotUi.lines.length !== 0) {
      if (mouseX < tablePlotUi.lines[tablePlotUi.current].x + 5
              && mouseX > tablePlotUi.lines[tablePlotUi.current].x - 5) {
        tablePlotUi.drag = true;
        document.body.style.cursor = 'pointer';
        tablePlotUi.lines[tablePlotUi.current].color = "red";
      }
    }
  }

// Движение мыши
  function mouseMove(evt, tablePlotUi) {
    var mouseX = relMouseCoords(tablePlotUi.canvas, evt).x;
    if (tablePlotUi.drag) {
      tablePlotUi.create = 1;
      // Изменение координат фигуры
      tablePlotUi.lines[tablePlotUi.currLine].x = mouseX;
    } else {
      for (var k = 0; k < tablePlotUi.lines.length; k++) {
        if ((mouseX < tablePlotUi.lines[k].x + 5)
                && (mouseX > tablePlotUi.lines[k].x - 5)) {
          tablePlotUi.currLine = k;
          tablePlotUi.lines[tablePlotUi.currLine].color = "red";
          document.body.style.cursor = 'pointer';
          break;
        } else {
          document.body.style.cursor = 'default';
          tablePlotUi.lines[k].color = tablePlotUi.lines[k].oldColor;
        }
      }
    }
  }

  function click(ev, tablePlotUi) {
    var canvas = tablePlotUi.canvas,
            coord = relMouseCoords(canvas, ev);
    if (tablePlotUi.flagDelete) {
      // Remove current marker.
      tablePlotUi.countrect--;
      tablePlotUi.lines.splice(tablePlotUi.current, 1);
      tablePlotUi.current = 0;
    } else {
      if (tablePlotUi.create === 0) {
        tablePlotUi.countrect++;
        tablePlotUi.current = tablePlotUi.countrect;
        tablePlotUi.lines[tablePlotUi.countrect] =
                new Shape(coord.x, coord.y, tablePlotUi.currentColor, canvas, tablePlotUi.lineType);
      }
    }
  }

  function relMouseCoords(currentElement, event) {
    var
            totalOffsetX = 0,
            totalOffsetY = 0;
    do {
      totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
      totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
    } while (currentElement = currentElement.offsetParent)
    return {x: event.pageX - totalOffsetX, y: event.pageY - totalOffsetY};
  }

// Если отпущена кнопка мыши, то переменная drag принимает ложное значение
  function mouseUp(evt, tablePlotUi) {
    tablePlotUi.drag = false;
  }

// Класс, задающий линию
  function Shape(topX, topY, color, canvas, type) {
    this.color = color;
    this.oldColor = color;
    this.x = topX;
    this.y = topY;
    this.canvas = canvas;
    this.context = canvas.getContext("2d");
    this.type = type;
  }

  Shape.prototype.draw = function() {
    this.context.beginPath();
    this.context.lineWidth = 2;
    this.context.strokeStyle = this.color;
    this.context.moveTo(this.x, 0);
    this.context.lineTo(this.x, this.canvas.height);
    this.context.stroke();
  };

  return TablePlotUi;
});
