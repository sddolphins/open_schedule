/*
Calendar 1-Day View Header jQuery plugin v.1.0
Copyright (c) 2013 Minh Tran - sddolphins@gmail.com
MIT License Applies
*/

/*
options {
    data: object
    cellWidth: number
    slideWidth: number
}
*/

(function(jQuery) {

  jQuery.fn.calendarViewHeader = function() {
    var args = Array.prototype.slice.call(arguments);
    if (args.length == 1 && typeof(args[0]) == "object") {
        build.call(this, args[0]);
    }
  };

  function build(options) {
    var els = this;
    var defaults = {
      cellWidth: 21,
      slideWidth: 525
    };

    var opts = jQuery.extend(true, defaults, options);
    //if (opts.data) {
      render();
    //}

    function render() {
      /* Normally the start and end times are calculated based on the min and
         max hours of the data.  However to simplify things a little bit, let's
         set start and end times from 00:00 to 23:59.

      var minHours = 24;
      var startEnd = DateUtils.getBoundaryHoursFromData(opts.data, minHours);
      opts.start = startEnd[0];
      opts.end = startEnd[1];
      */
      opts.start = Date.today().set({hour: 0, minute: 0, second: 0});
      opts.end = Date.today().set({hour: 23, minute: 59, second: 59});

      els.each(function() {
        var container = jQuery(this);
        var div = jQuery("<div>", {
            "class": "calendarview"
        });

        new Header(div, opts).render();
        container.append(div);
      });
    }
  }

  var Header = function(div, opts) {
    function render() {
      var slideDiv = jQuery("<div>", {
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

    function addHzHeader(div, hours, cellWidth) {
      var headerDiv = jQuery("<div>", {
          "class": "calendarview-hzheader"
      });
      var hoursDiv = jQuery("<div>", {
          "class": "calendarview-hzheader-hours"
      });
      var totalW = 0;
      for (var m in hours) {
        for (var d in hours[m]) {
          var w = hours[m][d].length * cellWidth;
          totalW = totalW + w;
          for (var h in hours[m][d]) {
            hoursDiv.append(jQuery("<div>", {
                "class": "calendarview-hzheader-hour",
                "css": {
                    "width": cellWidth-1 + "px"
                }
            }).append(hours[m][d][h]));
          }
        }
      }
      hoursDiv.css("width", totalW + "px");
      headerDiv.append(hoursDiv);
      div.append(headerDiv);
    }

    function applyLastClass(div) {
      jQuery("div.calendarview-hzheader-hours div.calendarview-hzheader-hour:last-child", div).addClass("last");
    }

    return {
      render: render
    };
  };

  var DateUtils = {
    getBoundaryHoursFromData: function(data, minHours) {
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

      return [minStart, maxEnd];
    }
  };

})(jQuery);
