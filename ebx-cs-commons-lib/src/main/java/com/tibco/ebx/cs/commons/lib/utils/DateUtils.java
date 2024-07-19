/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.utils;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;

/**
 * The Class DateUtils.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class DateUtils {
	/** Date fields */
	public enum DateConstant {
		YEAR, HALFYEAR, QUARTER, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND
	}

	/** Roll up policy constants */
	public enum RollUpPolicy {
		ROLL_NONE, ROLL_DOWN, ROLL_UP
	}

	public static final String DATE_MATCH_OLDEST = "oldest";

	public static final String DATE_MATCH_NEWEST = "newest";

	public static final long MILLISECONDS_PER_DAY = Long.valueOf(24) * 60 * 60 * 1000;

	private static final int YEAR = 4;

	private static final int DAY = 3;

	private static final int HOUR = 2;
	private static final int MINUTE = 1;
	private static final int SECOND = 0;

	private static final int[] _MAX = new int[] { 60, 60, 24 };

	/**
	 * Adds the specified number of periods to the date/time.
	 *
	 * @param aDate        The date/time to which to add the specified periods.
	 * @param periodType   The kind of period to add (Year, Half-Year, Quarter, Month, Week, Day, Hour, Minute, or Second).
	 * @param periodsToAdd The number of periods to add.
	 * @return The resulting temporal type or null if the input and period type are inconsistent.
	 */
	public static Date add(final java.util.Date aDate, final DateConstant periodType, final int periodsToAdd) {
		return DateUtils.addUsingRollPolicy(aDate, periodType, periodsToAdd, RollUpPolicy.ROLL_DOWN);
	}

	/**
	 * Adds the specified number of periods to the date/time.<br>
	 *
	 * @param aDate          The date/time to which to add the specified periods.
	 * @param periodType     The kind of period to add (Year, Half-Year, Quarter, Month, Week, Day, Hour, Minute, or Second).
	 * @param periodsToAdd   The number of periods to add.
	 * @param roundingPolicy The rounding policy to use (Roll-up or Roll-down).
	 * @return The resulting temporal type or null if the input and period type are inconsistent.
	 */
	public static Date addUsingRollPolicy(final java.util.Date aDate, DateConstant periodType, int periodsToAdd, RollUpPolicy roundingPolicy) {
		if (aDate == null) {
			return null;
		}
		if (roundingPolicy == null) {
			roundingPolicy = RollUpPolicy.ROLL_NONE;
		}
		if (periodType == null) {
			periodType = DateConstant.YEAR;
		}
		Calendar cal = DateUtils.getCalendar(aDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int milliSeconds = cal.get(Calendar.MILLISECOND);
		int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

		int minFieldType = DateUtils.YEAR;
		if (DateConstant.YEAR == periodType) {
			periodsToAdd = periodsToAdd * 12;
		} else if (DateConstant.HALFYEAR == periodType) {
			periodsToAdd = periodsToAdd * 6;
		} else if (DateConstant.QUARTER == periodType) {
			periodsToAdd = periodsToAdd * 3;
		} else if (DateConstant.MONTH == periodType) {
			// nothing to do yet
		} else if (DateConstant.WEEK == periodType) {
			dayOfYear += periodsToAdd * 7;
			minFieldType = DateUtils.DAY;
		} else if (DateConstant.DAY == periodType) {
			dayOfYear += periodsToAdd;
			minFieldType = DateUtils.DAY;
		} else if (DateConstant.HOUR == periodType) {
			hour += periodsToAdd;
			minFieldType = DateUtils.HOUR;
		} else if (DateConstant.MINUTE == periodType) {
			minute += periodsToAdd;
			minFieldType = DateUtils.MINUTE;
		} else { // if (SECOND.equals(periodType)) {
			second += periodsToAdd;
			minFieldType = DateUtils.SECOND;
		}
		long millis = 0;
		if (minFieldType < DateUtils.YEAR) {
			int[] fields = new int[] { second, minute, hour, dayOfYear, year };
			DateUtils.makeCanonical(fields, minFieldType);
			// dealing with time and day change -- ignores roll
			millis = DateUtils.getTimeInMillisForDayOfYear(fields, true);
		} else {
			// normalize month
			year += periodsToAdd / 12;
			month += periodsToAdd % 12;
			if (month > 12) {
				month -= 12;
				year++;
			} else if (month < 1) {
				month += 12;
				year--;
			}
			// roll conditions -- roll for leap year if we are changing year
			if (month == 2 && day == 29 && !DateUtils.isLeapYear(year)) {
				if (RollUpPolicy.ROLL_UP == roundingPolicy) {
					month = 3;
					day = 1;
				} else {
					day = 28;
				}
			} else {
				int daysInMonth = DateUtils.daysInMonth(year, month);
				if (RollUpPolicy.ROLL_NONE != roundingPolicy) {
					if (day > daysInMonth) {
						if (RollUpPolicy.ROLL_UP == roundingPolicy) {
							month++;
							day = 1;
						} else { // roundingPolicy == ROLL_DOWN
							day = daysInMonth;
						}
					}
				} else {
					// normalize day
					if (day > daysInMonth) {
						day -= daysInMonth;
						month++;
					}
				}
			}
			millis = DateUtils.getTimeInMillis(year, month, day, hour, minute, second, true);
		}
		// Preserve the original input type if one of our Date or Time types.
		// All other types
		// become DateTime (corresponds to conversion to java.util.Date in
		// releases < 8.5).
		millis += milliSeconds;
		return new Date(millis);
	}

	/**
	 * Return whether the first date is after the second date
	 *
	 * @param date1 date to check is after other date
	 * @param date2 date to check is before other date
	 * @return whether date1 &gt; date2 or true if either date, but not both, are null
	 */
	public static boolean afterExclusive(final Date date1, final Date date2) {
		if (date2 == null) {
			return date1 != null;
		}
		return date1 == null || date1.after(date2);
	}

	/**
	 * Return whether the first date is after or the same as the second date
	 *
	 * @param date1 date to check is after other date
	 * @param date2 date to check is before other date
	 * @return whether date1 &gt;= date2 or true if either date is null
	 */
	public static boolean afterInclusive(final Date date1, final Date date2) {
		if (Objects.equals(date1, date2)) {
			return true;
		}
		return DateUtils.afterExclusive(date1, date2);
	}

	/**
	 * Return whether the first date is before the second date
	 *
	 * @param date1 date to check is before other date
	 * @param date2 date to check is after other date
	 * @return whether date1 &lt; date2 or true if either date, but not both, are null
	 */
	public static boolean beforeExclusive(final Date date1, final Date date2) {
		if (date2 == null) {
			return date1 != null;
		}
		return date1 == null || date1.before(date2);
	}

	/**
	 * Return whether the first date is before or the same as the second date
	 *
	 * @param date1 date to check is before other date
	 * @param date2 date to check is after other date
	 * @return whether date1 &lt;= date2 or true if either date is null
	 */
	public static boolean beforeInclusive(final Date date1, final Date date2) {
		if (Objects.equals(date1, date2)) {
			return true;
		}
		return DateUtils.beforeExclusive(date1, date2);
	}

	/**
	 * Return whether the between date is between the Start and End Date (exclusive)
	 *
	 * @param betweenDate date to check between other two dates
	 * @param startDate   early date
	 * @param endDate     later date
	 * @return whether startDate &lt; betweenDate &lt; endDate or true if either start or end date is null
	 */
	public static boolean betweenExclusive(final Date betweenDate, final Date startDate, final Date endDate) {
		if (betweenDate == null) {
			return false;
		}

		return DateUtils.beforeExclusive(startDate, betweenDate) && DateUtils.beforeExclusive(betweenDate, endDate);
	}

	/**
	 * Return whether the between date is between the Start and End Date (inclusive)
	 *
	 * @param betweenDate date to check between other two dates
	 * @param startDate   early date
	 * @param endDate     later date
	 * @return whether startDate &lt;= betweenDate &lt;= endDate or true if either start or end date is null
	 */
	public static boolean betweenInclusive(final Date betweenDate, final Date startDate, final Date endDate) {
		if (betweenDate == null) {
			return false;
		}

		return DateUtils.beforeInclusive(startDate, betweenDate) && DateUtils.beforeInclusive(betweenDate, endDate);
	}

	/**
	 * Statically construct a date from the input-fields.
	 *
	 * @param date the input date
	 * @param time the input time
	 * @return new DateTime
	 */
	public static Date createDateTimeFromDateAndTime(final Date date, final Date time) {
		if (date == null || time == null) {
			return null;
		}
		Calendar calendar = DateUtils.getCalendar(time);
		Calendar datePart = DateUtils.getCalendar(date);
		calendar.set(Calendar.ERA, datePart.get(Calendar.ERA));
		calendar.set(Calendar.YEAR, datePart.get(Calendar.YEAR));
		calendar.set(Calendar.DAY_OF_YEAR, datePart.get(Calendar.DAY_OF_YEAR));
		return calendar.getTime();
	}

	/**
	 * Return the Current System Date (DatePortion only)
	 *
	 * @return current date
	 */
	public static Date currentDate() {
		return org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
	}

	/**
	 * Return the number of days in the given month for the given year.<br>
	 * 
	 * @param year  year
	 * @param month month
	 * @return 28, 29, 30, or 31
	 */
	public static int daysInMonth(final int year, final int month) {
		if (month == 2) {
			return DateUtils.isLeapYear(year) ? 29 : 28;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		}
		return 31;
	}

	/**
	 * Return the number of days until another date.
	 * 
	 * @param aDate     date
	 * @param otherDate other date
	 * @return number of days
	 */
	public static Integer daysUntil(final java.util.Date aDate, final java.util.Date otherDate) {
		return DateUtils.periodsBetween(aDate, otherDate, DateConstant.DAY);
		/*
		 * alternate implementation if (aDate == null) return 0; long d1 = aDate.getTime(); long d2 = otherDate.getTime(); long diff = d2 - d1; // Account for daylight savings time. if (diff > 0) diff
		 * += MILLISECONDS_PER_DAY / 2; else diff -= MILLISECONDS_PER_DAY / 2; return (int)(diff / MILLISECONDS_PER_DAY);
		 */
	}

	/**
	 * Return a temporal representing the first day of the month that contains the given date. The return type corresponds to the input type (i.e. a {@link Date} or {@link DateTime} or
	 * <code>null</code> if the input type is a {@link Time} - which has no date component).
	 * 
	 * @param aDate date
	 * @return temporal
	 */
	public static Date firstDayOfMonth(final java.util.Date aDate) {
		if (aDate == null) {
			return null;
		}
		Calendar cal = DateUtils.getCalendar(aDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * Gets the age from today.
	 *
	 * @param pBirthDate the birth date
	 * @return the age
	 * @since 1.0.0
	 */
	public static Integer getAge(final Date pBirthDate) {
		return DateUtils.getAge(pBirthDate, null);
	}

	/**
	 * Gets the age from the date passed as argument.
	 *
	 * @param pBirthDate   the birth date
	 * @param pCurrentDate the current date to get the age from (can be null, new Date() will be used)
	 * @return the age
	 * @since 1.0.0
	 */
	public static Integer getAge(final Date pBirthDate, Date pCurrentDate) {
		if (pBirthDate == null) {
			return null;
		}
		if (pCurrentDate == null) {
			pCurrentDate = new Date();
		}
		Calendar current = Calendar.getInstance();
		current.setTime(pCurrentDate);
		Calendar birth = Calendar.getInstance();
		birth.setTime(pBirthDate);

		int age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		current.add(Calendar.YEAR, -age);
		if (birth.after(current)) {
			age = age - 1;
		}

		return age;
	}

	/**
	 * Return month (plus one since we want 1 = January).<br>
	 * 
	 * @param aDate date
	 * @return month base 1
	 */
	public static int getMonth(final Date aDate) {
		return DateUtils.getField(aDate, Calendar.MONTH) + 1;
	}

	/**
	 * Get the date from the record's list of fields that is either the newest or the oldest. This is equivalent of {@link #getOldestOrNewestOfDates(ValueContext, Path[], String, String)} except uses
	 * an <code>Adaptation</code>.
	 *
	 * @param record         the record
	 * @param dateFieldPaths a list of paths for the date fields
	 * @param oldestOrNewest either {@link #DATE_MATCH_OLDEST} or {@link #DATE_MATCH_NEWEST} representing whether you're looking for the oldest or newest match
	 * @param nullConfig     indicates how null values should be treated: {@link #DATE_MATCH_OLDEST} means null is the oldest possible date, {@link #DATE_MATCH_NEWEST} means null is the newest
	 *                       possible date, null means null values should be ignored when determining oldest or newest date
	 * @return the oldest or newest date
	 */
	public static Date getOldestOrNewestOfDates(final Adaptation record, final Path[] dateFieldPaths, final String oldestOrNewest, final String nullConfig) {
		return DateUtils.getOldestOrNewestOfDates(record, null, dateFieldPaths, oldestOrNewest, nullConfig);
	}

	/**
	 * Get the date from the record's list of fields that is either the newest or the oldest. This is equivalent of {@link #getOldestOrNewestOfDates(Adaptation, Path[], String, String)} except uses a
	 * <code>ValueContext</code>.
	 *
	 * @param recordContext  the <code>ValueContext</code> for the record
	 * @param dateFieldPaths a list of paths for the date fields
	 * @param oldestOrNewest either {@link #DATE_MATCH_OLDEST} or {@link #DATE_MATCH_NEWEST} representing whether you're looking for the oldest or newest match
	 * @param nullConfig     indicates how null values should be treated: {@link #DATE_MATCH_OLDEST} means null is the oldest possible date, {@link #DATE_MATCH_NEWEST} means null is the newest
	 *                       possible date, null means null values should be ignored when determining oldest or newest date
	 * @return the oldest or newest date
	 */
	public static Date getOldestOrNewestOfDates(final ValueContext recordContext, final Path[] dateFieldPaths, final String oldestOrNewest, final String nullConfig) {
		return DateUtils.getOldestOrNewestOfDates(null, recordContext, dateFieldPaths, oldestOrNewest, nullConfig);
	}

	// ///////////////////////////////////////////////////////////
	// expression functions
	// ///////////////////////////////////////////////////////////

	/**
	 * Return this year.<br>
	 * 
	 * @param aDate date
	 * @return year
	 */
	public static int getYear(final Date aDate) {
		return DateUtils.getField(aDate, Calendar.YEAR);
	}

	/**
	 * Increment date with the given amount of days.
	 *
	 * @param pDate       the initial date
	 * @param pAmountDays the amount of days (positive or negative)
	 * @return the incremented date
	 * @since 1.0.0
	 */
	public static Date incrementDate(final Date pDate, final int pAmountDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(pDate);
		cal.add(Calendar.DAY_OF_MONTH, pAmountDays);
		return cal.getTime();
	}

	/**
	 * Compares a Date's time to that of the String passed in.
	 * <p>
	 * The String passed in should be in the format:
	 * <p>
	 * <ul>
	 * <li><code>HH:mm:ss</code></li>
	 * </ul>
	 *
	 * @param date Instance of Date whose time value will be compared against the string passed in.
	 * @param time Instance of String representing the time to use for the comparison.
	 * @return true if the time portion matches the string provided
	 */
	public static boolean isDateTimeEqualToTime(final Date date, final String time) {
		boolean isEqual = false;

		SimpleDateFormat parser = new SimpleDateFormat(CommonsConstants.EBX_TIME_FORMAT);

		String timeToCompare = parser.format(date);

		isEqual = timeToCompare.equalsIgnoreCase(time);

		return isEqual;
	}

	/**
	 * Calculating if a year is a leap year
	 * 
	 * @param year year
	 * @return Boolean if leap year
	 */
	public static boolean isLeapYear(final int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
	}

	/**
	 * Checks if the two ranges overlap. Start and end date of the ranges are inclusives.
	 *
	 * @param pRangeAStartDate the start date of the range A
	 * @param pRangeAEndDate   the range end date of the range A
	 * @param pRangeBStartDate the range start date of the range B
	 * @param pRangeBEndDate   the range end date of the range B
	 * @return true, if the ranges overlap
	 * @throws IllegalArgumentException if at least one date argument is null or if the start date and end of a range are equal
	 * @since 1.4.0
	 */
	public static boolean isOverlapped(final Date pRangeAStartDate, final Date pRangeAEndDate, final Date pRangeBStartDate, final Date pRangeBEndDate) throws IllegalArgumentException {
		return DateUtils.isOverlapped(pRangeAStartDate, pRangeAEndDate, pRangeBStartDate, pRangeBEndDate, false);
	}

	/**
	 * Checks if the two ranges overlap. pIsExclusive argument allows to choose if the start and end date of the ranges are exclusive or not.
	 *
	 * @param pRangeAStartDate the start date of the range A
	 * @param pRangeAEndDate   the range end date of the range A
	 * @param pRangeBStartDate the range start date of the range B
	 * @param pRangeBEndDate   the range end date of the range B
	 * @param pIsExclusive     whether the dates are exclusive or not
	 * @return true, if the ranges overlap
	 * @throws IllegalArgumentException if at least one date argument is null or if the start date and end of a range are equal
	 * @since 1.4.0
	 */
	public static boolean isOverlapped(final Date pRangeAStartDate, final Date pRangeAEndDate, final Date pRangeBStartDate, final Date pRangeBEndDate, final boolean pIsExclusive)
			throws IllegalArgumentException {
		if (pRangeAStartDate == null || pRangeAEndDate == null || pRangeBStartDate == null || pRangeBEndDate == null) {
			throw new IllegalArgumentException("The date arguments shall not be null");
		}
		if (pRangeAStartDate.equals(pRangeAEndDate) || pRangeBStartDate.equals(pRangeBEndDate)) {
			throw new IllegalArgumentException("The start date and end date of ranges shall not be equal");
		}

		if (pRangeBStartDate.equals(pRangeAStartDate) && pRangeBEndDate.equals(pRangeAEndDate) || pRangeBStartDate.equals(pRangeAEndDate) && pRangeBEndDate.equals(pRangeAStartDate)) {
			return true;
		}

		if (DateUtils.isWithin(pRangeAStartDate, pRangeAEndDate, pRangeBStartDate, pIsExclusive) || DateUtils.isWithin(pRangeAStartDate, pRangeAEndDate, pRangeBEndDate, pIsExclusive)
				|| DateUtils.isWithin(pRangeBStartDate, pRangeBEndDate, pRangeAStartDate, pIsExclusive) || DateUtils.isWithin(pRangeBStartDate, pRangeBEndDate, pRangeAEndDate, pIsExclusive)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Compares only the time setting (HH:mm:ss) of the two Dates passed in.
	 * <p>
	 * <strong>NOTE:</strong> This method only compares the time between the two Dates. Not the actual calendar day.
	 * <p>
	 *
	 * @param firstDateTime  Instance of Date
	 * @param secondDateTime Instance of Date
	 *
	 * @return True if the Time of the firstDateTime is equal to the secondDateTime.
	 */
	public static boolean isTimeEqual(final Date firstDateTime, final Date secondDateTime) {

		boolean isEqual = false;

		SimpleDateFormat parser = new SimpleDateFormat(CommonsConstants.EBX_TIME_FORMAT);

		if (firstDateTime != null && secondDateTime != null) {
			String time1 = parser.format(firstDateTime);
			String time2 = parser.format(secondDateTime);

			isEqual = time1.equalsIgnoreCase(time2);
		}

		return isEqual;
	}

	/**
	 * Checks if the tested date is within the range. Start and end date of the range are inclusives.
	 *
	 * @param pRangeStartDate the start date of the range
	 * @param pRangeEndDate   the end date of the range
	 * @param pTestedDate     the tested date
	 * @return true, if the date is within the range
	 * @throws IllegalArgumentException if at least one argument is null or if the start and end date of the range are equal
	 * @since 1.4.0
	 */
	public static boolean isWithin(final Date pRangeStartDate, final Date pRangeEndDate, final Date pTestedDate) throws IllegalArgumentException {
		return DateUtils.isWithin(pRangeStartDate, pRangeEndDate, pTestedDate, false);
	}

	/**
	 * Checks if the tested date is within the range. pIsExclusive argument allows to choose if the start and end date of the range are exclusive or not.
	 *
	 * @param pRangeStartDate the start date of the range
	 * @param pRangeEndDate   the end date of the range
	 * @param pTestedDate     the tested date
	 * @param pIsExclusive    whether the dates are exclusive or not
	 * @return true, if the date is within the range
	 * @throws IllegalArgumentException if at least one argument is null or if the start and end date of the range are equal
	 * @since 1.4.0
	 */
	public static boolean isWithin(final Date pRangeStartDate, final Date pRangeEndDate, final Date pTestedDate, final boolean pIsExclusive) throws IllegalArgumentException {
		if (pRangeStartDate == null || pRangeEndDate == null || pTestedDate == null) {
			throw new IllegalArgumentException("The date arguments shall not be null");
		}
		if (pRangeStartDate.equals(pRangeEndDate)) {
			throw new IllegalArgumentException("The start date and end date of the range shall not be equal");
		}

		if (pIsExclusive && (pTestedDate.equals(pRangeStartDate) || pTestedDate.equals(pRangeEndDate))) {
			return false;
		}

		if (pTestedDate.before(pRangeStartDate) && pTestedDate.before(pRangeEndDate)) {
			return false;
		} else {
			return (!(pTestedDate.after(pRangeStartDate) && pTestedDate.after(pRangeEndDate)));
		}
	}

	/**
	 * Return a temporal representing the last day of the month that contains the given date. The return type corresponds to the input type (i.e. a {@link Date} or {@link DateTime} or
	 * <code>null</code> if the input type is a {@link Time} - which has no date component).
	 * 
	 * @param aDate date
	 * @return last day of the month
	 */
	public static Date lastDayOfMonth(final java.util.Date aDate) {
		if (aDate == null) {
			return null;
		}
		Calendar cal = DateUtils.getCalendar(aDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DAY_OF_MONTH, DateUtils.daysInMonth(year, month));
		return DateUtils.createDate(cal);
	}

	/**
	 * Return the number of months between the first and second date
	 * 
	 * @param aDate     first date
	 * @param laterDate second date
	 * @return number of months between the first and second date
	 */
	public static Integer monthsUntil(final java.util.Date aDate, final java.util.Date laterDate) {
		return DateUtils.periodsBetween(aDate, laterDate, DateConstant.MONTH);
	}

	/**
	 * Parse the given string into a Date object, using the default EBX date format. Converts any <code>ParseException</code> into an <code>OperationException</code>. This is the same as calling
	 * {@link parseDateOrDateTime(String,String)} with a format of <code>CommonsConstants.EBX_DATE_FORMAT</code>.
	 *
	 * @param str the date string, i.e. "2017-12-31"
	 * @return the parsed Date
	 * @throws OperationException if a <code>ParseException</code> occurs while parsing.
	 */
	public static Date parseDate(final String str) throws OperationException {
		return DateUtils.parseDateOrDateTimeOrTime(str, CommonsConstants.EBX_DATE_FORMAT);
	}

	/**
	 * Parse the given string into a Date object, using the supplied format. Converts any <code>ParseException</code> into an <code>OperationException</code>.
	 *
	 * @param str    the date or date/time or time string, i.e. "2017-12-31" or "2017-12-31T22:00:00" or "22:00:00"
	 * @param format the format to apply to the string
	 * @return the parsed Date
	 * @throws OperationException if a <code>ParseException</code> occurs while parsing.
	 */
	public static Date parseDateOrDateTimeOrTime(final String str, final String format) throws OperationException {
		SimpleDateFormat parser = new SimpleDateFormat(format);
		try {
			return parser.parse(str);
		} catch (ParseException ex) {
			throw OperationException.createError("Error parsing " + str + " into Date using format " + format + ".", ex);
		}
	}

	/**
	 * Parse the given string into a Date object, using the default EBX date/time format. Converts any <code>ParseException</code> into an <code>OperationException</code>. This is the same as calling
	 * {@link parseDateOrDateTime(String,String)} with a format of <code>CommonsConstants.EBX_DATE_TIME_FORMAT</code>.
	 *
	 * @param str the date/time string, i.e. "2017-12-31T22:00:00"
	 * @return the parsed Date
	 * @throws OperationException if a <code>ParseException</code> occurs while parsing.
	 */
	public static Date parseDateTime(final String str) throws OperationException {
		return DateUtils.parseDateOrDateTimeOrTime(str, CommonsConstants.EBX_DATE_TIME_FORMAT);
	}

	// ///////////////////////////////////////////////////////
	// private utility methods for dealing with calendars
	// ///////////////////////////////////////////////////////

	/**
	 * Parse the given string into a Date object, using the default EBX time format. Converts any <code>ParseException</code> into an <code>OperationException</code>. This is the same as calling
	 * {@link parseDateOrDateTimeOrTime(String,String)} with a format of <code>CommonsConstants.EBX_TIME_FORMAT</code>.
	 *
	 * @param str the time string, i.e. "22:00:00"
	 * @return the parsed Date
	 * @throws OperationException if a <code>ParseException</code> occurs while parsing.
	 */
	public static Date parseTime(final String str) throws OperationException {
		return DateUtils.parseDateOrDateTimeOrTime(str, CommonsConstants.EBX_TIME_FORMAT);
	}

	/**
	 * Computes the number of periods between this date/time and another (later) date/time.<br>
	 * Note: This method probably needs a more efficient implementation.
	 *
	 * @param firstDate  The first date.
	 * @param lastDate   The last date.
	 * @param periodType The kind of date period to use in making the computation.
	 * @return The number of periods between the two date/time instances or null if the inputs and period type are inconsistent.
	 */
	public static Integer periodsBetween(final java.util.Date firstDate, final java.util.Date lastDate, final DateConstant periodType) {
		if (firstDate == null || lastDate == null) {
			return null;
		}
		int addsBeforeLaterDatePassed = 0;
		java.util.Date interimDate = firstDate;
		while (interimDate != null && (interimDate.equals(lastDate) || interimDate.before(lastDate))) {
			interimDate = DateUtils.add(interimDate, periodType, 1);
			addsBeforeLaterDatePassed++;
		}
		return addsBeforeLaterDatePassed - 1;
	}

	/**
	 * Subtracts the specified number of periods from the date/time.<br>
	 * 
	 * @param aDate             date
	 * @param periodType        The kind of period to subtract (YEAR, HALFYEAR, QUARTER, MONTH, WEEK, or DAY).
	 * @param periodsToSubtract The number of periods to subtract.
	 * @return The resulting temporal type or null if the input and period type are inconsistent.
	 */
	public static Date subtract(final java.util.Date aDate, final DateConstant periodType, final int periodsToSubtract) {
		return DateUtils.subtractUsingRollPolicy(aDate, periodType, periodsToSubtract, RollUpPolicy.ROLL_DOWN);
	}

	/**
	 * Subtracts the specified number of periods from the date/time.<br>
	 * 
	 * @param aDate             date
	 * @param periodType        The kind of period to subtract (YEAR, HALFYEAR, QUARTER, MONTH, WEEK, or DAY).
	 * @param periodsToSubtract The number of periods to subtract.
	 * @param roundingPolicy    The rounding policy to use (ROLL_UP or ROLL_DOWN).
	 * @return The resulting temporal type or null if the input and period type are inconsistent.
	 */
	public static Date subtractUsingRollPolicy(final java.util.Date aDate, final DateConstant periodType, final int periodsToSubtract, final RollUpPolicy roundingPolicy) {
		return DateUtils.addUsingRollPolicy(aDate, periodType, -periodsToSubtract, roundingPolicy);
	}

	/**
	 * Return the current time as a Date.
	 * 
	 * @return the current time as a Date.
	 */
	public static Date today() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * Return the number of years between the first and second date instances
	 * 
	 * @param aDate     first date
	 * @param laterDate second date
	 * @return number of years between the first and second date instances
	 */
	public static Integer yearsUntil(final Date aDate, final java.util.Date laterDate) {
		return DateUtils.periodsBetween(aDate, laterDate, DateConstant.YEAR);
	}

	/**
	 * Return a {@link Date} representing yesterday.
	 * 
	 * @return Date representing yesterday
	 */
	public static Date yesterday() {
		return DateUtils.subtract(new Date(), DateConstant.DAY, 1);
	}

	private static Calendar getCalendar(final java.util.Date aDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(aDate);
		return cal;
	}

	/**
	 * Return the indicated Calendar field.
	 *
	 * @param aDate The date to parse.
	 * @param field One of the Calendar field values
	 */
	private static Integer getField(final java.util.Date aDate, final int field) {
		if (aDate == null) {
			return null; // no date in time or time in date
		}
		Calendar cal = DateUtils.getCalendar(aDate);
		return cal.get(field);
	}

	/**
	 * A private method that takes in either an adaptation or a value context.<br>
	 * It would be cleaner code to simply always use value context and call createValueContext() <br>
	 * when we have an adaptation, but this is used by value functions so trying to make it as efficient as possible.
	 */
	private static Date getOldestOrNewestOfDates(final Adaptation record, final ValueContext recordContext, final Path[] dateFieldPaths, final String oldestOrNewest, final String nullConfig) {
		Path fieldPath = dateFieldPaths[0];
		Date returnVal = recordContext == null ? record.getDate(fieldPath) : (Date) recordContext.getValue(fieldPath);
		// Matching on null if the nulls indicate the type of date we're looking for
		boolean matchOnNull = oldestOrNewest.equals(nullConfig);
		// If the first date is null and we're matching on nulls, then no need to look
		// further
		if (returnVal == null && matchOnNull) {
			return returnVal;
		}
		// Look through the rest of the dates
		for (int i = 1; i < dateFieldPaths.length; i++) {
			fieldPath = dateFieldPaths[i];
			Date value = recordContext == null ? record.getDate(fieldPath) : (Date) recordContext.getValue(fieldPath);
			// If the new value is a better match (i.e. older or newer depending on what
			// we're looking for)
			if (DateUtils.newDateMatchFound(returnVal, value, oldestOrNewest, nullConfig)) {
				// If the new value is null and we're matching on null then just return it since
				// no need to look further
				// (null is the oldest or newest you can get)
				if (value == null && matchOnNull) {
					return value;
				}
				// Otherwise, this is the new value to beat
				returnVal = value;
			}
		}
		return returnVal;
	}

	private static void makeCanonical(final int[] fields, final int fieldType) {
		if (fieldType == DateUtils.DAY) {
			int year = fields[DateUtils.YEAR];
			int dayOfYear = fields[DateUtils.DAY];
			// now we know the year, we can normalize dayOfYear
			int daysInYear = DateUtils.isLeapYear(year) ? 366 : 365;
			while (dayOfYear > daysInYear) {
				dayOfYear -= daysInYear;
				year++;
				daysInYear = DateUtils.isLeapYear(year) ? 366 : 365;
			}
			while (dayOfYear < 1) {
				year--;
				daysInYear = DateUtils.isLeapYear(year) ? 366 : 365;
				dayOfYear += daysInYear;
			}
			fields[DateUtils.YEAR] = year;
			fields[DateUtils.DAY] = dayOfYear;
		} else {
			int small = fields[fieldType];
			int large = fields[fieldType + 1];
			int max = DateUtils._MAX[fieldType];
			large += small / max;
			small = small % max;
			fields[fieldType] = small;
			fields[fieldType + 1] = large;
			DateUtils.makeCanonical(fields, fieldType + 1);
		}
	}

	// Private method utilized by the getOldestOrNewestOfDates methods
	private static boolean newDateMatchFound(final Date date1, final Date date2, final String oldestOrNewest, final String nullConfig) {
		if (date2 == null) {
			// If they're both null or we're ignoring nulls then no new match was found
			if (date1 == null || nullConfig == null) {
				return false;
			}
			// Otherwise, a new match was found only if what we're looking for is what null
			// represents
			return oldestOrNewest.equalsIgnoreCase(nullConfig);
		}
		// If there's a date2 but date1 is null, then it's a new match only if nulls
		// don't represent what we're looking for
		// (which also will handle if nulls are ignored)
		if (date1 == null) {
			return !oldestOrNewest.equalsIgnoreCase(nullConfig);
		}
		// Both aren't null so if we're looking for oldest then we found a new match if
		// date2 is before date1
		if (DateUtils.DATE_MATCH_OLDEST.equalsIgnoreCase(oldestOrNewest)) {
			return date2.before(date1);
		}
		// Otherwise we found a new match if date2 is after date1
		return date2.after(date1);
	}

	protected static Date createDate(final Calendar cal) {
		return cal.getTime();
	}

	/**
	 * Construct a {@link DateTime} from a year, month, day.
	 *
	 * @return new DateTime
	 */
	protected static Date getDateTime(final int year, final int month, final int day) {
		return new Date(DateUtils.getTimeInMillis(year, month, day, 0, 0, 0));
	}

	/**
	 * Construct a {@link DateTime} from a year, month, day, hour, minute, second.
	 *
	 * @return new DateTime
	 */
	protected static Date getDateTime(final int year, final int month, final int day, final int hour, final int minute, final int second) {
		return new Date(DateUtils.getTimeInMillis(year, month, day, hour, minute, second));
	}

	protected static long getTimeInMillis(final int year, final int month, final int day, final int hour, final int minute, final int second) {
		return DateUtils.getTimeInMillis(year, month, day, hour, minute, second, true);
	}

	/**
	 * Return millisecond equivalent to year, month, day, hour, minute, second. Month is 1-based.
	 */
	protected static long getTimeInMillis(final int year, final int month, final int day, final int hour, final int minute, final int second, final boolean lenient) {
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(lenient);
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTimeInMillis();
	}

	/**
	 * Return millisecond equivalent to year, dayOfYear, hour, minute, second.
	 */
	protected static long getTimeInMillisForDayOfYear(final int[] fields, final boolean lenient) {
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(lenient);
		calendar.set(Calendar.YEAR, fields[DateUtils.YEAR]);
		calendar.set(Calendar.DAY_OF_YEAR, fields[DateUtils.DAY]);
		calendar.set(Calendar.HOUR_OF_DAY, fields[DateUtils.HOUR]);
		calendar.set(Calendar.MINUTE, fields[DateUtils.MINUTE]);
		calendar.set(Calendar.SECOND, fields[DateUtils.SECOND]);
		return calendar.getTimeInMillis();
	}

	private DateUtils() {
	}
}
