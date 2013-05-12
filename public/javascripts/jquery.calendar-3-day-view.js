/*
Calendar 1-Day View JQuery plugin v.1.0
Copyright (c) 2013 Minh Tran - sddolphins@gmail.com
MIT License Applies
*/

/*
Options {
    startDate: date
    data: object
    cellWidth: number
    slideWidth: number
    dataUrl: string
    behavior: {
        header-day
        clickable: boolean,
        draggable: boolean,
        resizable: boolean,
        onClick: function,
        onDblClick: function,
        onDrag: function,
        onResize: function
    }
}
*/

(function($) {

  $.fn.calendarView3Day = function() {
      var args = Array.prototype.slice.call(arguments);
      if (args.length == 1 && typeof(args[0]) == "object") {
          build.call(this, args[0]);
      }
  };

  function build(options) {
    var els = this;
    var defaults = {
      cellWidth: 7,
      slideWidth: 504,
      behavior: {
          clickable: true,
          dblclickable: true,
          draggable: true,
          resizable: true
      }
    };

    var opts = $.extend(true, defaults, options);
    if (opts.startDate && opts.data && opts.data.length > 0) {
      render();
    }
    else if (opts.startDate && opts.dataUrl) {
      $.getJSON(opts.dataUrl, function(data) {
        if (data && data.length > 0) {
          opts.data = data;
          render();
        }
      });
    }

    function render() {
      var startEnd = DateUtils.getBoundaryHoursFromData(opts.startDate);
      opts.start = startEnd[0];
      opts.end = startEnd[1];

      els.each(function() {
        var container = $(this);
        var div = $("<div>", {
          "class": "calendarview"
        });

        new Chart(div, opts).render();
        container.append(div);

        var w = $("div.calendarview-slide-container", container).outerWidth();
        container.css("width", (w + 2) + "px");

        new Behavior(container, opts).apply();
      });
    }
  }

  var Chart = function(div, opts) {
    function render() {
      var slideDiv = $("<div>", {
        "class": "calendarview-slide-container",
        "css": {
          "width": opts.slideWidth + "px"
        }
      });

      hours = getHours(opts.start, opts.end);
      addGrid(slideDiv, opts.data, hours, opts.cellWidth);
      addBlockContainers(slideDiv, opts.data);
      addBlocks(slideDiv, opts.data, opts.cellWidth, opts.start);
      div.append(slideDiv);
      applyLastClass(div.parent());
    }

    // Creates a 3 dimensional array [month][date][hour] of every hour
    // between the given start and end dates.
    function getHours(start, end) {
      var hours = [];
      hours[start.getMonth()] = [];
      hours[start.getMonth()][start.getDate()] = [start.getHours()];

      var last = start.clone();
      while (last.getTime() < end.getTime()) {
        var next = last.clone().addHours(1);
        if (!hours[next.getMonth()]) {
          hours[next.getMonth()] = [];
        }
        if (!hours[next.getMonth()][next.getDate()]) {
          hours[next.getMonth()][next.getDate()] = [];
        }
        hours[next.getMonth()][next.getDate()].push(next.getHours());
        last = next;
      }
      return hours;
    }

    function addGrid(div, data, hours, cellWidth) {
      var gridDiv = $("<div>", {
        "class": "calendarview-grid"
      });
      var rowDiv = $("<div>", {
        "class": "calendarview-grid-row"
      });

      var bgColor;
      var totalW = 0;

      for (var m in hours) {
        for (var d in hours[m]) {
          var w = hours[m][d].length * cellWidth;
          totalW = totalW + w;

          if (d % 2 === 1) {
            bgColor = "#fafafa";
          }
          else {
            bgColor = "#ffffff";
          }

          for (var h in hours[m][d]) {
            var cellDiv;
            if (h == 23) {
              cellDiv = $("<div>", {
                "class": "calendarview-3day-grid-row-cell",
                "css": {
                  "background-color": bgColor,
                  "border-right": "1px solid #d0d0d0",
                  "width": (cellWidth-0.3) + "px"
                }
              });
            }
            else {
              cellDiv = $("<div>", {
                "class": "calendarview-3day-grid-row-cell",
                "css": {
                  "background-color": bgColor,
                  "width": (cellWidth-0.3) + "px"
                }
              });
            }
            rowDiv.append(cellDiv);
          }
        }
      }

      rowDiv.css("width", totalW + "px");
      gridDiv.css("width", totalW + "px");
      for (var i = 0; i < data.length; i++) {
        var row = rowDiv.clone();
        gridDiv.append(row);
      }
      div.append(gridDiv);
    }

    function addBlockContainers(div, data) {
      var blocksDiv = $("<div>", {
        "class": "calendarview-blocks"
      });
      for (var i = 0; i < data.length; i++) {
        blocksDiv.append($("<div>", {
            "class": "calendarview-block-container"
        }));
      }
      div.append(blocksDiv);
    }

    function addBlocks(div, data, cellWidth, start) {
      var rows = $("div.calendarview-blocks div.calendarview-block-container", div);
      var rowIdx = 0;
      for (var i = 0; i < data.length; i++) {
        for (var j = 0; j < data[i].shifts.length; j++) {
          var shift = data[i].shifts[j];
          var size = DateUtils.hoursBetween(shift.start, shift.end);
          var offset = DateUtils.hoursBetween(start, shift.start);

          var block = $("<div>", {
            "class": "calendarview-block",
            "title": shift.name + ", " + size + " hours",
            "css": {
              "width": ((size * (cellWidth-0.3) - 4)) + "px",
              "margin-left": (offset * (cellWidth-0.3)) + "px",
              "bottom": (j * 27) + "px"
            }
          });

          addBlockData(block, data[i], shift);
          if (shift.color) {
            block.css("background-color", shift.color);
          }

          var imgSrc = shift.image;
          block.append($("<div>", {
            "class": "calendarview-block-image"
          }).append('<img src=' + imgSrc + ' />'));

          block.append($("<div>", {
            "class": "calendarview-block-text"
          }).text(size));
          $(rows[rowIdx]).append(block);
        }
        rowIdx = rowIdx + 1;
      }
    }

    function addBlockData(block, data, shift) {
      // This allows custom attributes to be added to the data objects and makes
      // them available to the 'data' argument of click, resize, and drag handlers.
      var blockData = {
        id: shift.id,
        name: shift.name,
        start: shift.start,
        end: shift.end
      };
      $.extend(blockData);
      block.data("block-data", blockData);
    }

    function applyLastClass(div) {
        $("div.calendarview-grid-row div.calendarview-grid-row-cell:last-child", div).addClass("last");
    }

    return {
        render: render
    };
  };

  var Behavior = function(div, opts) {
    function apply() {
        if (opts.behavior.clickable) {
          bindBlockClick(div, opts.behavior.onClick);
        }

        if (opts.behavior.dblclickable) {
          bindBlockDblClick(div, opts.behavior.onDblClick);
        }

        if (opts.behavior.resizable) {
          bindBlockResize(div, opts.cellWidth, opts.start, opts.behavior.onResize);
        }

        if (opts.behavior.draggable) {
          bindBlockDrag(div, opts.cellWidth, opts.start, opts.behavior.onDrag);
        }
    }

    function bindBlockClick(div, callback) {
        $("div.calendarview-block", div).on("click", function() {
          if (callback) {
            callback($(this).data("block-data"));
          }
        });
    }

    function bindBlockDblClick(div, callback) {
      $("div.calendarview-block", div).on("dblclick", function() {
        if (callback) {
          callback($(this).data("block-data"));
        }
      });
    }

    function bindBlockResize(div, cellWidth, startDate, callback) {
      $("div.calendarview-block", div).resizable({
        grid: cellWidth,
        handles: "e, w",
        stop: function() {
          var block = $(this);
          updateDataAndPosition(div, block, cellWidth, startDate);
          if (callback) {
            callback(block.data("block-data"));
          }
        }
      });
    }

    function bindBlockDrag(div, cellWidth, startDate, callback) {
      $("div.calendarview-block", div).draggable({
        cursor: "move",
        axis: "x",
        grid: [cellWidth, cellWidth],
        stop: function() {
          var block = $(this);
          updateDataAndPosition(div, block, cellWidth, startDate);
          if (callback) {
            callback(block.data("block-data"));
          }
        }
      });
    }

    function updateDataAndPosition(div, block, cellWidth, startDate) {
      var container = $("div.calendarview-slide-container", div);
      var scroll = container.scrollLeft();
      var offset = block.offset().left - container.offset().left + scroll;

      // Set new start date.
      var hoursFromStart = Math.round(offset / cellWidth);
      var newStartHour = hoursFromStart - startDate.getHours();
      var newStart = startDate.clone().addHours(newStartHour);
      block.data("block-data").start = newStart;

      // Set new end date.
      var width = block.outerWidth();
      var numberOfHours = Math.round(width / cellWidth) + 1;
      block.data("block-data").end = newStart.clone().addHours(numberOfHours);

      // Update block text.
      //var s = numberOfHours + "hrs";
      $("div.calendarview-block-text", block).text(numberOfHours);

      // Update tooltip.
      var data = block.data("block-data");
      block.attr('title', data.name + ", " + numberOfHours + " hours");

      // Remove top and left properties to avoid incorrect block positioning,
      // set position to relative to keep blocks relative to scrollbar when
      // scrolling.
      block.css("top", "")
        .css("left", "")
        .css("position", "relative")
        .css("margin-left", offset + "px");
    }

    return {
        apply: apply
    };
  };

  var DateUtils = {
    hoursBetween: function(start, end) {
      if (!start || !end) {
        return 0;
      }
      start = Date.parse(start);
      end = Date.parse(end);
      if (start.getYear() == 1901 || end.getYear() == 8099) {
        return 0;
      }
      var count = 0,
          date = start.clone();
      while (date.getTime() < end.getTime()) {
        count = count + 1;
        date.addHours(1);
      }
      return count;
    },

    getBoundaryHoursFromData: function(startDate) {
      // Start at beginning of the start date (hour 0) instead of the hour of
      // the start date, so that every day is display with 24 hours.
      var minStart = startDate;
      minStart.clearTime();

      // To avoid empty white space to the right of the grid, we also display
      // part of day 4.
      var maxEnd = minStart.clone();
      maxEnd.addDays(3);
      maxEnd.set({hour: 14, minute: 59, second: 59});

      return [minStart, maxEnd];
    }
  };

})($);
