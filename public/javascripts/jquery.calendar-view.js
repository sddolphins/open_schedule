/*
Calendar View JQuery plugin v.1.0
Copyright (c) 2013 Minh Tran - sddolphins@gmail.com
MIT License Applies
*/

/*
Options {
    startDate: date
    data: object
    days: number
    cellWidth: number
    slideWidth: number
    dataUrl: string
    behavior: {
        clickable: boolean,
        dblclickable: boolean,
    }
}
*/

(function($) {

  $.fn.calendarView = function() {
      var args = Array.prototype.slice.call(arguments);
      if (args.length == 1 && typeof(args[0]) == "object") {
          build.call(this, args[0]);
      }
  };

  function build(options) {
    var els = this;
    var defaults = {
      days: 14,
      cellWidth: 7,
      slideWidth: 504,
      behavior: {
          clickable: true,
          dblclickable: true,
          /* Day view is not draggable or resizable. */
          draggable: false,
          resizable: false
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
      var startEnd = DateUtils.getBoundaryDaysFromData(opts.startDate, opts.days);
      opts.start = startEnd[0];
      opts.end = startEnd[1];

      if (opts.cellWidth < 48)
        opts.cellWidth = 48;

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

      days = getDays(opts.start, opts.end);
      if (opts.days >  14) {
        addHzHeader(slideDiv, opts.start, days, opts.cellWidth);
      }
      addGrid(slideDiv, opts.data, days, opts.cellWidth);
      addBlockContainers(slideDiv, opts.data);
      addBlocks(slideDiv, opts);
      div.append(slideDiv);
      applyLastClass(div.parent());
    }

    // Creates a 2 dimensional array [month][date] between the given start and
    // end dates.
    function getDays(start, end) {
      var days = [];
      days[start.getMonth()] = [start.getDate()];

      var last = start.clone();
      while (last.getTime() < end.getTime()) {
        var next = last.clone().addDays(1);
        if (!days[next.getMonth()]) {
            days[next.getMonth()] = [];
        }
        days[next.getMonth()].push(next.getDate());
        last = next;
      }
      return days;
    }

    function addHzHeader(div, start, days, cellWidth) {
      var headerDiv = $("<div>", {
          "class": "calendarview-hzheader"
      });
      var daysDiv = $("<div>", {
          "class": "calendarview-hzheader-days"
      });

      var bgColor;
      var totalW = 0;
      var dateStart = start.clone();

      for (var m in days) {
        for (var d in days[m]) {
          totalW = totalW + cellWidth;

          // Format header label.
          var month = parseInt(m, 10);
          dateStart.set({
            month: month,
            day: days[m][d]
          });
          var dateLabel = dateStart.toString("ddd") + "<br>" + dateStart.toString("MM/dd");

          if (d % 2 === 1) {
            bgColor = "#fafafa";
          }
          else {
            bgColor = "#ffffff";
          }

          daysDiv.append($("<div>", {
            "class": "calendarview-hzheader-day",
            "css": {
              "background-color": bgColor,
              "width": (cellWidth-1) + "px"
            }
          }).append(dateLabel));
        }
      }
      daysDiv.css("width", totalW + "px");
      headerDiv.append(daysDiv);
      div.append(headerDiv);
    }

    function addGrid(div, data, days, cellWidth) {
      var gridDiv = $("<div>", {
        "class": "calendarview-grid"
      });
      var rowDiv = $("<div>", {
        "class": "calendarview-grid-row"
      });

      var bgColor;
      var totalW = 0;

      for (var m in days) {
        for (var d in days[m]) {
          totalW = totalW + cellWidth;

          if (d % 2 === 1) {
            bgColor = "#fafafa";
          }
          else {
            bgColor = "#ffffff";
          }

          cellDiv = $("<div>", {
            "class": "calendarview-grid-row-cell",
            "css": {
              "background-color": bgColor,
              "width": (cellWidth-1) + "px"
            }
          });
          rowDiv.append(cellDiv);
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

    function addBlocks(div, opts) {
      var rows = $("div.calendarview-blocks div.calendarview-block-container", div);
      var rowIdx = 0;
      for (var i = 0; i < opts.data.length; i++) {
        for (var j = 0; j < opts.data[i].shifts.length; j++) {
          var shift = opts.data[i].shifts[j];
          var size = DateUtils.hoursDaysBetween(shift.start, shift.end, true);
          var offset = DateUtils.hoursDaysBetween(opts.start, shift.start) - 1;
          var bottom;
          if (opts.days >  14) {
            bottom = (j-1) * 30;
          }
          else {
            bottom = (j * 25);
          }

          var block = $("<div>", {
            "id": shift.id,
            "popBgColor": shift.color,
            "class": "calendarview-block pop",
            "css": {
              "width": ((opts.cellWidth-0.3) - 4) + "px",
              "margin-left": (offset * (opts.cellWidth-0.3)) + "px",
              "bottom": bottom + "px"
            }
          });

          addBlockData(block, opts.data[i], shift);
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
        $("div.calendarview-hzheader-days div.calendarview-hzheader-day:last-child", div).addClass("last");
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

    return {
        apply: apply
    };
  };

  var DateUtils = {
    hoursDaysBetween: function(start, end, returnHours) {
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
        if (returnHours && returnHours === true) {
          date.addHours(1);
        }
        else {
          date.addDays(1);
        }
      }
      return count;
    },

    getBoundaryDaysFromData: function(startDate, days) {
      // Start at beginning of the start date (hour 0) instead of the hour of
      // the start date, so that every day is display with 24 hours.
      var minStart = startDate;
      minStart.clearTime();

      var maxEnd = minStart.clone();
      maxEnd.addDays(days-2);
      maxEnd.set({hour: 23, minute: 59, second: 59});

      return [minStart, maxEnd];
    }
  };

})($);
