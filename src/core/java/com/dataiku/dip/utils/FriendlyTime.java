package com.dataiku.dip.utils;

import java.util.Date;

public class FriendlyTime {
	public static String friendlyDelayFromNow(long epoch) {
		return friendlyDelta(new Date(epoch), new Date());
	}
	public static String friendlyDelayFromNow(Date dateTime) {
		return friendlyDelta(dateTime, new Date());
	}
	/** SO 635935 */
	public static String friendlyDelta(Date dateTime, Date current) {
		StringBuffer sb = new StringBuffer();
		long diffInSeconds = (current.getTime() - dateTime.getTime()) / 1000;

		long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
		long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
		long years = (diffInSeconds = (diffInSeconds / 12));

		if (years > 0) {
			if (years == 1) {
				sb.append("one year");
			} else {
				sb.append(years + " years");
			}
			if (years <= 6 && months > 0) {
				if (months == 1) {
					sb.append(" and one month");
				} else {
					sb.append(" and " + months + " months");
				}
			}
		} else if (months > 0) {
			if (months == 1) {
				sb.append("one month");
			} else {
				sb.append(months + " months");
			}
			if (months <= 6 && days > 0) {
				if (days == 1) {
					sb.append(" and a day");
				} else {
					sb.append(" and " + days + " days");
				}
			}
		} else if (days > 0) {
			if (days == 1) {
				sb.append("one day");
			} else {
				sb.append(days + " days");
			}
			if (days <= 3 && hrs > 0) {
				if (hrs == 1) {
					sb.append(" and one hour");
				} else {
					sb.append(" and " + hrs + " hours");
				}
			}
		} else if (hrs > 0) {
			if (hrs == 1) {
				sb.append("one hour");
			} else {
				sb.append(hrs + " hours");
			}
			if (min > 1) {
				sb.append(" and " + min + " minutes");
			}
		} else if (min > 0) {
			if (min == 1) {
				sb.append("one minute");
			} else {
				sb.append(min + " minutes");
			}
			if (sec > 1) {
				sb.append(" and " + sec + " seconds");
			}
		} else {
			if (sec <= 1) {
				sb.append("about a second");
			} else {
				sb.append("about " + sec + " seconds");
			}
		}

		sb.append(" ago");


		/*String result = new String(String.format(
    "%d day%s, %d hour%s, %d minute%s, %d second%s ago",
    diff[0],
    diff[0] > 1 ? "s" : "",
    diff[1],
    diff[1] > 1 ? "s" : "",
    diff[2],
    diff[2] > 1 ? "s" : "",
    diff[3],
    diff[3] > 1 ? "s" : ""));*/
		return sb.toString();
	}
    public static String elaspeTime(int time) {
        String res = "";
        for (int i = 0; i < 3; ++i) {
            if (time == 0) {
                if (i < 2) {
                    res = "0" + unit[i] + res;
                }
                break;
            } else {
                int t = time % div[i];
                time /= div[i];
                res = "" + t + unit[i] + res;
            }
        }
        return res;
    }
    private static int[] div = { 60, 60, 24, 7, 4, 12 };
    private static String[] unit = { "s", "m", "h", "w", "m", "y" };
}
