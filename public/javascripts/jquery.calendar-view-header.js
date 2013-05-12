/*
Calendar View Header JQuery plugin v.1.0
Copyright (c) 2013 Minh Tran - sddolphins@gmail.com
MIT License Applies
*/

/*
options {
    startDate: date
    days: number
    cellWidth: number
    slideWidth: number
}
*/

(function($) {

  $.fn.calendarViewHeader = function() {
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
      slideWidth: 504
    };

    var opts = $.extend(true, defaults, options);

    if (opts.startDate) {
      render();
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

      days = getDays(opts.start, opts.end);
      addHzHeader(slideDiv, opts.start, days, opts.cellWidth);
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

    function applyLastClass(div) {
      $("div.calendarview-hzheader-days div.calendarview-hzheader-day:last-child", div).addClass("last");
    }

    return {
      render: render
    };
  };

  var DateUtils = {
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
