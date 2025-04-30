package org.myteam.server.match.util;

public class PercentageUtil {

	private static final int STANDARD = 100;

	public static int[] getCalculatedPercentages(int home, int away) {
		if (home == 0 && away == 0) {
			return new int[]{0, 0};
		}

		int homePercentage = getHomePercentage(home, away);
		int awayPercentage = getAwayPercentage(home, away);

		int totalPercentage = homePercentage + awayPercentage;

		if (totalPercentage > STANDARD) {
			homePercentage--;
		} else if (totalPercentage < STANDARD) {
			awayPercentage++;
		}

		return new int[]{homePercentage, awayPercentage};
	}

	private static int getHomePercentage(int home, int away) {
		return calculatePercentage(home, home + away);
	}

	private static int getAwayPercentage(int home, int away) {
		return calculatePercentage(away, home + away);
	}

	private static int calculatePercentage(int count, int total) {
		if (total == 0) {
			return 0;
		}

		double percentage = (double) count / total * 100;
		return (int) Math.round(percentage);
	}
}
