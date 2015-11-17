package cl.buildersoft.framework.util;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSDateTimeUtil {
	private static final Logger LOG = Logger.getLogger(BSDateTimeUtil.class.getName());
	public static final String SQL_FORMAT = "yyyy-MM-dd";
	private static String formatDate = null;
	private static String[] months = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };

	public static String month2Word(Integer month) {
		return months[month];
	}

	public static String month2Word(Date date) {
		return months[date.getMonth()];
	}

	public static String calendar2String(Calendar calendar) {
		return date2String(calendar2Date(calendar), "yyyy-MM-dd hh:mm:ss.S");
	}

	public static String calendar2String(Calendar calendar, String format) {
		return date2String(calendar2Date(calendar), format);
	}

	public static Date calendar2Date(Calendar calendar) {
		Date date = new Date();
		date.setTime(calendar.getTimeInMillis());
		return date;
	}

	/**
	 * <code>
	public static java.sql.Date calendar2SQLDate(Calendar calendar) {
		java.sql.Date date = new java.sql.Date(calendar.getTimeInMillis());
		return date;
	}
</code>
	 */
	public static Timestamp calendar2Timestamp(Calendar calendar) {
		return new Timestamp(calendar.getTimeInMillis());
	}

	public static Calendar date2Calendar(java.util.Date date) {
		Calendar out = Calendar.getInstance();
		out.setTimeInMillis(date.getTime());
		return out;
	}

	public static Calendar string2Calendar(String dateString, String format) {
		LOG.entering(BSDateTimeUtil.class.getName(), "string2Calendar", BSUtils.array2ObjectArray(dateString, format));
		DateFormat formatter = new SimpleDateFormat(format);
		Calendar out = null;
		try {
			java.util.Date date = (java.util.Date) formatter.parse(dateString);
			out = date2Calendar(date);
			date = null;
		} catch (ParseException e) {
			throw new BSProgrammerException(e);
		}

		LOG.exiting(BSDateTimeUtil.class.getName(), "string2Calendar", out);
		return out;
	}

	public static Boolean isValidDate(String date, String format) {
		Boolean out = Boolean.TRUE;
		if (date.length() > 0 && date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);

			Date testDate = null;

			try {
				testDate = sdf.parse(date);
			} catch (ParseException e) {
				out = Boolean.FALSE;
			}

			if (!sdf.format(testDate).equals(date)) {
				out = Boolean.FALSE;
			}
		} else {
			out = false;
		}
		return out;
	}

	public static String getYear(Date date) {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
		return simpleDateformat.format(date);
	}

	public static String getMonth(Date date) {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM");
		return simpleDateformat.format(date);
	}

	public static String getFormatDatetime(Connection conn) {
		BSConfig config = new BSConfig();
		return config.getString(conn, "FORMAT_DATETIME");
	}

	public static String getFormatDatetime(HttpServletRequest request) {
		BSmySQL mysql = new BSmySQL();
		Connection conn = mysql.getConnection(request);
		String out = getFormatDatetime(conn);
		mysql.closeConnection(conn);
		return out;
		/*
		 * String out = request.getServletContext().getInitParameter(
		 * "bsframework.datetimeFormat"); if (out == null) { out =
		 * "dd/MM/yyyy HH:mm:ss"; } return out;
		 */
	}

	public static String getFormatDate(Connection conn) {
		if (formatDate == null) {
			BSConfig config = new BSConfig();
			formatDate = config.getString(conn, "FORMAT_DATE");
		}
		return formatDate;
	}

	public static String getFormatDate(HttpServletRequest request) {
		if (formatDate == null) {
			BSHttpServlet servlet = new BSHttpServlet();
			// BSmySQL mysql = new BSmySQL();
			Connection conn = servlet.getConnection(request);
			// Connection conn = mysql.getConnection(request);
			formatDate = getFormatDate(conn);
			// mysql.closeConnection(conn);
		}
		return formatDate;
	}

	public static String date2String(Object value, String format) {
		String out = "";
		if (value != null) {
			DateFormat formatter = new SimpleDateFormat(format);
			out = formatter.format((Date) value);
		}
		return out;
	}

	public static String date2String(HttpServletRequest request, Object value) {
		String out = "";
		if (value != null) {
			String format = getFormatDate(request);
			DateFormat formatter = new SimpleDateFormat(format);
			out = formatter.format((Date) value);
		}
		return out;
	}

	public static String date2LongString(Date date) {
		Calendar calendar = date2Calendar(date);
		String out = calendar.get(Calendar.DAY_OF_MONTH) + " de " + month2Word(date) + " de " + calendar.get(Calendar.YEAR);
		return out;
	}

	public static String dateTime2String(HttpServletRequest request, Object value) {
		String out = "";
		if (value != null) {
			String format = getFormatDatetime(request);
			DateFormat formatter = new SimpleDateFormat(format);
			out = formatter.format((Date) value);
		}
		return out;
	}

	public static Calendar string2Calendar(HttpServletRequest request, String date) {
		String format = getFormatDate(request);
		return string2Calendar(date, format);
	}

	public static Calendar timestamp2Calendar(Timestamp timestamp) {
		Calendar out = Calendar.getInstance();
		out.setTimeInMillis(timestamp.getTime());
		return out;
	}
}
