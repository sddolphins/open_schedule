package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.data.validation.*;
import play.templates.TemplateLoader;

import helpers.StatusMessage;
import models.CalendarViewShift;
import models.CalendarViewShift3Day;
import models.EditShift;
import models.JobTitle;
import models.Location;
import models.Member;
import models.OpenShift;
import models.Schedule;
import models.ScheduledShift;
import models.SelfScheduleShift;
import models.Shift;
import models.ShiftRestriction;
import models.ShiftShift;
import models.ShiftStatus;
import models.ShiftType;
import models.WorkSubtype;
import models.WorkType;

@Secure
public class Shifts extends BaseController {

    public static void index(int scheduleId) {
        // @debug.
        //System.out.println("Shifts.index()");
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        List<Location> locations = Location.findByAccountId(schedule.account.id.intValue());
        List<JobTitle> jobTitles = JobTitle.findByAccountId(schedule.account.id.intValue());
        render(schedule, locations, jobTitles);
    }

    public static void create(int scheduleId) {
        Schedule schedule = Schedule.findById(new Long(scheduleId));
        List<ShiftShift> workShifts = ShiftShift.findAll();
        List<WorkType> workTypes = WorkType.findAll();
        List<WorkSubtype> workSubtypes = WorkSubtype.findAll();
        List<JobTitle> jobTitles = JobTitle.findByAccountId(schedule.account.id.intValue());
        List<Location> locations = Location.findByAccountId(schedule.account.id.intValue());
        render(schedule, workShifts, workTypes, workSubtypes, locations, jobTitles);
    }

    public static void createShift(Long scheduleId) {
        // @debug.
        //System.out.println("Shifts.createShift() - schedule id = " + scheduleId);

        // Parse shift info from form.
        Map<String, String[]> values = params.all();
        String sStartDate = values.get("startDate")[0];
        String sStartTime = values.get("startTime")[0];
        String sEndDate = values.get("endDate")[0];
        String sEndTime = values.get("endTime")[0];
        String shiftLocation = values.get("shiftLocation")[0];
        String contact = values.get("contact")[0];
        String sWorkShift = values.get("workShift")[0];
        String sWorkType = values.get("workType")[0];
        String sWorkSubtype = values.get("workSubtype")[0];
        String comment = values.get("comment")[0];
        String sShiftType = values.get("shiftType")[0];
        String sMember = values.get("member")[0];
        String numNeeded = values.get("numNeeded")[0];
        String signupDate = values.get("signupDate")[0];
        String ssNumNeeded = values.get("ssNumNeeded")[0];
        String[] locations = values.get("location");
        String[] jobTitles = values.get("jobTitle");

        // Perform validation.
        validation.required("Shift start date", sStartDate);
        validation.required("Shift start time", sStartTime);
        validation.required("Shift end date", sStartDate);
        validation.required("Shift end time", sStartTime);
        validation.required("Shift location", shiftLocation);

        int shiftType = Integer.parseInt(sShiftType);
        switch (shiftType) {
            case 1:
                validation.required("Number of positions needed", numNeeded);
                validation.min("Number of positions needed", numNeeded, 1);
                break;
             case 2:
                validation.required("Assigned member", sMember);
                break;
            case 3:
                validation.required("Sign up date", signupDate);
                validation.required("Number of position needed", ssNumNeeded);
                validation.min("Number of positions needed", ssNumNeeded, 1);
                break;
        }

        // Convert shift start/end date time.
        Date start = new Date();
        Date end = new Date();
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            start = df.parse(sStartDate + " " + sStartTime);
            end = df.parse(sEndDate + " " + sEndTime);
            System.out.println("start: " + start.toString() + ", end: " + end.toString());
        }
        catch (ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            renderJSON(new StatusMessage(StatusMessage.ERROR, "Error parsing shift start/end date time.", ""));
        }

        // Make sure that end date/time is after start date/time.
        validation.future("Shift end date time", end, start);

        // TODO: Must validate shift overlapped, safe schedule, etc.

        if (validation.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            ArrayList<play.data.validation.Error> errors = (ArrayList)validation.errors();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0)
                    sb.append("~");
                sb.append(errors.get(i).message());
            }
            renderJSON(new StatusMessage(StatusMessage.ERROR, sb.toString(), ""));
        }

        // Fetch required shift info.
        Schedule schedule = Schedule.findById(scheduleId);
        Location location = Location.findById(Long.valueOf(shiftLocation));
        ShiftType type = ShiftType.findById(Long.valueOf(shiftType));
        ShiftShift shiftshift = ShiftShift.findById(Long.valueOf(sWorkShift));
        WorkType workType = WorkType.findById(Long.valueOf(sWorkType));
        WorkSubtype workSubtype = WorkSubtype.findById(Long.valueOf(sWorkSubtype));

        ShiftStatus status = ShiftStatus.findByName("Unknown");
        switch (shiftType) {
            case 1:
                status = ShiftStatus.findByName("Open");
                break;
             case 2:
                status = ShiftStatus.findByName("Closed");
                break;
            case 3:
                status = ShiftStatus.findByName("Pending");
                break;
        }

        // Insert shift general info.
        Shift shift = new Shift(start, end, schedule, location, type, shiftshift,
                                status, workType, workSubtype, contact, comment);

        // Insert specific shift info.
        switch (shiftType) {
            case 1: // Postable/Open shift.
                new OpenShift(shift, Integer.parseInt(numNeeded));
                break;
             case 2: // Scheduled shift.
                Member member = Member.findById(Long.valueOf(sMember));
                new ScheduledShift(shift, member);
                break;
            case 3: // Self-schedule shift.
                try {
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    Date signup = df.parse(signupDate);
                    new SelfScheduleShift(shift, signup, Integer.parseInt(ssNumNeeded));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    renderJSON(new StatusMessage(StatusMessage.ERROR,
                                                 "Error parsing shift sign up date.", ""));
                }
                break;
        }

        // Build shift restrictions map.
        Map<Long, Long> map = new HashMap<Long, Long>();
        ArrayList<Long> al = new ArrayList<Long>();
        if (locations != null && locations.length > 0) {
            for (int i = 0; i < locations.length; i++) {
                Long locationId = Long.parseLong(locations[i]);
                //System.out.println("restrict to location id: " + locationId);
                al.add(locationId);
                map.put(locationId, new Long(1000+i));
            }
        }

        if (jobTitles != null && jobTitles.length > 0) {
            for (int i = 0; i < jobTitles.length; i++) {
                Long jtId = Long.parseLong(jobTitles[i]);
                //System.out.println("restrict to job title id: " + jtId);
                if (al.size() > i) {
                    map.put(al.get(i), jtId);
                }
                else {
                    map.put(new Long(1000+i), jtId);
                }
            }
        }

        // Insert shift restrictions.
        if (map.size() > 0) {
            for (Long locId : map.keySet()) {
                Long jtId = map.get(locId);

                Location loc;
                if (locId.intValue() < 1000)
                    loc = Location.findById(locId);
                else
                    loc = null;

                JobTitle jt;
                if (jtId.intValue() < 1000)
                    jt = JobTitle.findById(jtId);
                else
                    jt = null;

                new ShiftRestriction(shift, loc, jt, true);
            }
        }

        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void delete(int shiftId) {
        // @debug.
        System.out.println("Shifts.delete() - shift id: " + shiftId);

        // In-activate shift for historical reference.
        Shift shift = Shift.findById(new Long(shiftId));
        shift.active = false;
        shift.save();
        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void updateShiftDuration(int shiftId, String startStr, String endStr) {
        // @debug.
        System.out.println("Shifts.updateShiftDuration() - shift id: " + shiftId + ", start: " + startStr + ", end: " + endStr);

        // Convert start/end dates.
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            startDate = sdf.parse(startStr);
            endDate = sdf.parse(endStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        Shift shift = Shift.findById(new Long(shiftId));
        shift.dateStart = startDate;
        shift.dateEnd = endDate;
        shift.save();
        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void updateShiftInfo(int shiftId) {
        // @debug.
        System.out.println("Shifts.updateShiftInfo() - shift id: " + shiftId);

        // Parse shift info from form.
        Map<String, String[]> values = params.all();
        String sStartDate = values.get("startDate")[0];
        String sStartTime = values.get("startTime")[0];
        String sEndDate = values.get("endDate")[0];
        String sEndTime = values.get("endTime")[0];
        String shiftLocation = values.get("shiftLocation")[0];
        String contact = values.get("contact")[0];
        String sWorkShift = values.get("workShift")[0];
        String sWorkType = values.get("workType")[0];
        String sWorkSubtype = values.get("workSubtype")[0];
        String comment = values.get("comment")[0];

        // Perform validation.
        validation.required("Shift start date", sStartDate);
        validation.required("Shift start time", sStartTime);
        validation.required("Shift end date", sStartDate);
        validation.required("Shift end time", sStartTime);
        validation.required("Shift location", shiftLocation);

        // Convert shift start/end date time.
        Date start = new Date();
        Date end = new Date();
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            start = df.parse(sStartDate + " " + sStartTime);
            end = df.parse(sEndDate + " " + sEndTime);
            System.out.println("start: " + start.toString() + ", end: " + end.toString());
        }
        catch (ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            renderJSON(new StatusMessage(StatusMessage.ERROR, "Invalid shift start date time and/or shift end date time.", ""));
        }

        // Make sure that end date/time is after start date/time.
        validation.future("Shift end date time", end, start);

        if (validation.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            ArrayList<play.data.validation.Error> errors = (ArrayList)validation.errors();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0)
                    sb.append("~");
                sb.append(errors.get(i).message());
            }
            renderJSON(new StatusMessage(StatusMessage.ERROR, sb.toString(), ""));
        }

        Location location = Location.findById(Long.valueOf(shiftLocation));
        ShiftShift shiftshift = ShiftShift.findById(Long.valueOf(sWorkShift));
        WorkType workType = WorkType.findById(Long.valueOf(sWorkType));
        WorkSubtype workSubtype = WorkSubtype.findById(Long.valueOf(sWorkSubtype));

        // Save shift info.
        Shift shift = Shift.findById(new Long(shiftId));
        shift.dateStart = start;
        shift.dateEnd = end;
        shift.contact = contact;
        shift.comment = comment;
        shift.location = location;
        shift.shiftShift = shiftshift;
        shift.workType = workType;
        shift.workSubtype = workSubtype;
        shift.save();
        renderJSON(new StatusMessage(StatusMessage.SUCCESS, "", ""));
    }

    public static void getCalendarViewShifts(int scheduleId, int locationId, int jobTitleId,
                                             String dateStr, int viewByDays) {
        // @debug.
        System.out.println("Shifts.getCalendarViewShifts - start date: " + dateStr + ", days: " + viewByDays + ", job title id: " + jobTitleId);

        // Convert start date.
        Date startDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            startDate = sdf.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        // Find all shifts that match selected criterias.
        List<CalendarViewShift> calShifts = new ArrayList<CalendarViewShift>();
        calShifts = Shift.findCalendarViewShifts(startDate, viewByDays, scheduleId,
                                                 locationId, jobTitleId);
        renderJSON(calShifts);
    }

    public static void getCalendarViewShifts3Day(int scheduleId, int locationId, int jobTitleId,
                                                 String dateStr, int viewByDays) {
        // @debug.
        System.out.println("Shifts.getCalendarViewShifts3Day - start date: " + dateStr + ", days: " + viewByDays + ", job title id: " + jobTitleId);

        // Convert start date.
        Date startDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            startDate = sdf.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        // Find all shifts that match selected criterias.
        List<CalendarViewShift3Day> calShifts = new ArrayList<CalendarViewShift3Day>();
        calShifts = Shift.findCalendarViewShifts3Day(startDate, viewByDays, scheduleId,
                                                     locationId, jobTitleId);
        renderJSON(calShifts);
    }

    public static void getShift(int shiftId) {
        // @debug.
        System.out.println("Shifts.getShift() - shift id: " + shiftId);
        Shift shift = Shift.findById(new Long(shiftId));

        // Prepare template arguments.
        EditShift es = new EditShift(shift);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("shift", es);

        List<Location> locations = Location.findByAccountId(shift.schedule.account.id.intValue());
        List<ShiftShift> workShifts = ShiftShift.findAll();
        List<WorkType> workTypes = WorkType.findAll();
        List<WorkSubtype> workSubtypes = WorkSubtype.findAll();
        params.put("locations", locations);
        params.put("workShifts", workShifts);
        params.put("workTypes", workTypes);
        params.put("workSubtypes", workSubtypes);

        // Load template.
        String html = TemplateLoader.load("Shifts/editShift.html").render(params);
        renderText(html);
    }
}