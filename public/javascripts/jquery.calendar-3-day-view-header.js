/*
Calendar 3-Day View Header JQuery plugin v.1.0
Copyright (c) 2013 Minh Tran - sddolphins@gmail.com
MIT License Applies
*/

/*
options {
    startDate: date
    cellWidth: number
    slideWidth: number
}
*/

(function($) {

  $.fn.calendarView3DayHeader = function() {
    var args = Array.prototype.slice.call(arguments);
    if (args.length == 1 && typeof(args[0]) == "object") {
        build.call(this, args[0]);
    }
  };

  function build(options) {
    var els = this;
    var defaults = {
      cellWidth: 7,
      slideWidth: 504
    };

    var opts = $.extend(true, defaults, options);

    if (opts.startDate) {
      render();
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

        new Header(div, opts).render();
        container.append(div);
      });
    }
  }

  var Header = function(div, opts) {
    function render() {
      var slideDiv = $("<div>", {
          "class": "calendarview-slide-container",
          "css": {
              "width": opts.slideWidth + "px"
          }
      });

      hours = getHours(opts.start, opts.end);
      addHzHeader(slideDiv, hours, opts.cellWidth);
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

    function addHzHeader(div, hours, cellWidth) {
      var headerDiv = $("<div>", {
          "class": "calendarview-hzheader"
      });
      var hoursDiv = $("<div>", {
          "class": "calendarview-hzheader-hours"
      });
      var totalW = 0;
      for (var m in hours) {
        for (var d in hours[m]) {
          var w = hours[m][d].length * cellWidth;
          totalW = totalW + w;
          for (var h in hours[m][d]) {
            if (h == 7 || h == 15 || h == 23) {
              if (h == 23) {
                hoursDiv.append($("<div>", {
                  "class": "calendarview-3day-hzheader-hour",
                  "css": {
                    "border-right": "1px solid #777",
                    "width": (cellWidth-0.3) + "px"
                  }
                }).append(hours[m][d][h]));
              }
              else {
                hoursDiv.append($("<div>", {
                  "class": "calendarview-3day-hzheader-hour",
                  "css": {
                    "width": (cellWidth-0.3) + "px"
                  }
                }).append(hours[m][d][h]));
              }
            }
            else {
              hoursDiv.append($("<div>", {
                "class": "calendarview-3day-hzheader-hour",
                "css": {
                  "width": (cellWidth-0.3) + "px"
                }
              }));
            }
          }
        }
      }
      hoursDiv.css("width", totalW + "px");
      headerDiv.append(hoursDiv);
      div.append(headerDiv);
    }

    function applyLastClass(div) {
      $("div.calendarview-hzheader-hours div.calendarview-3day-hzheader-hour:last-child", div).addClass("last");
    }

    return {
      render: render
    };
  };

  var DateUtils = {
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
