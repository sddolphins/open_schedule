/*
Calendar 1-Day View jQuery plugin v.1.0
Copyright (c) 2013 Minh Tran - sddolphins@gmail.com
MIT License Applies
*/

/*
Options {
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

(function(jQuery) {

  jQuery.fn.calendarView = function() {
      var args = Array.prototype.slice.call(arguments);
      if (args.length == 1 && typeof(args[0]) == "object") {
          build.call(this, args[0]);
      }
  };

  function build(options) {
    var els = this;
    var defaults = {
      cellWidth: 21,
      slideWidth: 525,
      behavior: {
          clickable: true,
          dblclickable: true,
          draggable: true,
          resizable: true
      }
    };

    var opts = jQuery.extend(true, defaults, options);
    if (opts.data && opts.data.length > 0) {
      render();
    }
    else if (opts.dataUrl) {
      jQuery.getJSON(opts.dataUrl, function(data) {
        if (data && data.length > 0) {
          opts.data = data;
          render();
        }
      });
    }

    function render() {
      var minHours = 24;
      var startEnd = DateUtils.getBoundaryHoursFromData(opts.data, minHours);
      opts.start = startEnd[0];
      opts.end = startEnd[1];

      els.each(function() {
        var container = jQuery(this);
        var div = jQuery("<div>", {
          "class": "calendarview"
        });

        new Chart(div, opts).render();
        container.append(div);

        var w = jQuery("div.calendarview-slide-container", container).outerWidth();
        container.css("width", (w + 2) + "px");

        new Behavior(container, opts).apply();
      });
    }
  }

  var Chart = function(div, opts) {
    function render() {
      var slideDiv = jQuery("<div>", {
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

      var last = start;
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
      var gridDiv = jQuery("<div>", {
        "class": "calendarview-grid"
      });
      var rowDiv = jQuery("<div>", {
        "class": "calendarview-grid-row"
      });
      for (var m in hours) {
        for (var d in hours[m]) {
          for (var h in hours[m][d]) {
            var cellDiv = jQuery("<div>", {
              "class": "calendarview-grid-row-cell",
              "css": {
                  "width": cellWidth-1 + "px"
              }
            });
            rowDiv.append(cellDiv);
          }
        }
      }
      var w = jQuery("div.calendarview-grid-row-cell", rowDiv).length * cellWidth;
      rowDiv.css("width", w + "px");
      gridDiv.css("width", w + "px");
      for (var i = 0; i < data.length; i++) {
        gridDiv.append(rowDiv.clone());
      }
      div.append(gridDiv);
    }

    function addBlockContainers(div, data) {
      var blocksDiv = jQuery("<div>", {
        "class": "calendarview-blocks"
      });
      for (var i = 0; i < data.length; i++) {
        blocksDiv.append(jQuery("<div>", {
            "class": "calendarview-block-container"
        }));
      }
      div.append(blocksDiv);
    }

    function addBlocks(div, data, cellWidth, start) {
      var rows = jQuery("div.calendarview-blocks div.calendarview-block-container", div);
      var rowIdx = 0;
      var block;
      for (var i = 0; i < data.length; i++) {
        var size = DateUtils.hoursBetween(data[i].start, data[i].end);
        var offset = DateUtils.hoursBetween(start, data[i].start);
        block = jQuery("<div>", {
          "class": "calendarview-block",
          "title": data[i].name + ", " + size + " hours",
          "css": {
            "width": ((size * cellWidth) - 5) + "px",
            "margin-left": ((offset * cellWidth) + 1) + "px"
          }
        });

        addBlockData(block, data[i]);
        if (data[i].color) {
          block.css("background-color", data[i].color);
        }

        var imgSrc = data[i].image;
        block.append(jQuery("<div>", {
          "class": "calendarview-block-image"
        }).append('<img src=' + imgSrc + ' />'));

        var s = data[i].name + " - " + size + "hrs";
        block.append(jQuery("<div>", {
          "class": "calendarview-block-text"
        }).text(s));

        jQuery(rows[rowIdx]).append(block);
        rowIdx = rowIdx + 1;
      }
    }

    function addBlockData(block, data) {
      // This allows custom attributes to be added to the data objects and makes
      // them available to the 'data' argument of click, resize, and drag handlers.
      var blockData = {
        id: data.id,
        name: data.name,
        start: data.start,
        end: data.end
      };
      jQuery.extend(blockData);
      block.data("block-data", blockData);
    }

    function applyLastClass(div) {
        jQuery("div.calendarview-grid-row div.calendarview-grid-row-cell:last-child", div).addClass("last");
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
        jQuery("div.calendarview-block", div).on("click", function() {
          if (callback) {
            callback(jQuery(this).data("block-data"));
          }
        });
    }

    function bindBlockDblClick(div, callback) {
      jQuery("div.calendarview-block", div).on("dblclick", function() {
        if (callback) {
          callback(jQuery(this).data("block-data"));
        }
      });
    }

    function bindBlockResize(div, cellWidth, startDate, callback) {
      jQuery("div.calendarview-block", div).resizable({
        grid: cellWidth,
        handles: "e, w",
        stop: function() {
          var block = jQuery(this);
          updateDataAndPosition(div, block, cellWidth, startDate);
          if (callback) {
            callback(block.data("block-data"));
          }
        }
      });
    }

    function bindBlockDrag(div, cellWidth, startDate, callback) {
      jQuery("div.calendarview-block", div).draggable({
        cursor: "move",
        axis: "x",
        grid: [cellWidth, cellWidth],
        stop: function() {
          var block = jQuery(this);
          updateDataAndPosition(div, block, cellWidth, startDate);
          if (callback) {
            callback(block.data("block-data"));
          }
        }
      });
    }

    function updateDataAndPosition(div, block, cellWidth, startDate) {
      var container = jQuery("div.calendarview-slide-container", div);
      var scroll = container.scrollLeft();
      var offset = block.offset().left - container.offset().left + scroll;

      // Set new start date.
      var hoursFromStart = Math.round(offset / cellWidth);
      var newStart = startDate.clone().addHours(hoursFromStart);
      block.data("block-data").start = newStart;

      // Set new end date.
      var width = block.outerWidth();
      var numberOfHours = Math.round(width / cellWidth);
      block.data("block-data").end = newStart.clone().addHours(numberOfHours);

      // Update block text.
      var data = block.data("block-data");
      var s = data.name + " - " + numberOfHours + "hrs";
      jQuery("div.calendarview-block-text", block).text(s);

      // Update title.
      //var data = block.data("block-data");
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

    getBoundaryHoursFromData: function(data, minHours) {
      /*
      var minStart = new Date();
      var maxEnd = new Date();
      for (var i = 0; i < data.length; i++) {
        var start = Date.parse(data[i].start);
        var end = Date.parse(data[i].end);
        if (i === 0) {
          minStart = start;
          maxEnd = end;
        }
        if (minStart.getTime() > start.getTime()) {
          minStart = start;
        }
        if (maxEnd.getTime() < end.getTime()) {
          maxEnd = end;
        }
      }

      // Insure that the width of the chart is the minimum number of hours
      // to avoid empty whitespace to the right of the grid.
      maxEnd = minStart.clone().addHours(minHours);
      */

      /*
      minStart and maxEnd must match the boundary hours in the header.
      */
      var minStart = Date.parse(data[0].start);
      minStart.clearTime();
      var maxEnd = minStart.clone();
      maxEnd.add(1).days().set({hour: 11, minute: 59, second: 59});

      return [minStart, maxEnd];
    }
  };

})(jQuery);
