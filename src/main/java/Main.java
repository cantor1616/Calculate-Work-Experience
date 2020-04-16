import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    public static void main(String[] args) {
        do {
            List<Map<String, Date>> jobDates = new ArrayList<>();

            boolean morePastExperience;
            System.out.println("\nType \"done\" at any time to calculate past work experience!");
            do {
                morePastExperience = getStartAndEndDates(jobDates);
            } while (morePastExperience);

            boolean overlap;
            do {
                overlap = removeOverlap(jobDates);
            } while (overlap);

            printResults(jobDates);
        } while (anotherCandidate());
    }

    private static boolean getStartAndEndDates(List<Map<String, Date>> jobDates) {
        try {
            Date startDate = getStartDate();
            if(Objects.isNull(startDate)) return false;
            Date endDate = getEndDate();
            if(Objects.isNull(endDate)) return false;

            Map<String, Date> dateMap = new HashMap<>();
            dateMap.put(START_DATE, startDate);
            dateMap.put(END_DATE, endDate);

            jobDates.add(dateMap);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Date getStartDate() {
        System.out.print("\nStart date (mm/yy): ");
        String startDate = SCANNER.nextLine();
        if(done(startDate)) {
            return null;
        }
        try {
            return parseDate(startDate);
        } catch (ParseException e) {
            System.out.println("Invalid date entered...");
            return getStartDate();
        }
    }

    private static Date getEndDate() {
        System.out.print("End date (mm/yy): ");
        String endDate = SCANNER.nextLine();
        if(done(endDate)) {k
            return null;
        }
        try {
            return parseDate(endDate);
        } catch (ParseException e) {
            System.out.println("Invalid date entered...");
            return getEndDate();
        }
    }

    private static boolean done(String date) {
        return date.equals("done");
    }

    private static Date parseDate(String date) throws ParseException {
        if(Integer.valueOf(date.split("/")[0]) > 12) {
            throw new ParseException("", 0);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy", Locale.ENGLISH);
        return simpleDateFormat.parse(date);
    }

    private static boolean anotherCandidate() {
        System.out.println("\nWould you like to calculate for another candidate (y/n)?");
        String anotherCandidate = SCANNER.nextLine();
        if(anotherCandidate.equals("y")) return true;
        else if(anotherCandidate.equals("n")) return false;
        else {
            System.out.println("Invalid input.");
            return anotherCandidate();
        }
    }

    private static boolean removeOverlap(List<Map<String, Date>> jobDates) {
        List<Map<String, Date>> jobDatesCopy = new ArrayList<>(jobDates);
        jobDatesCopy.sort(Comparator.comparing(dateRange -> dateRange.get(START_DATE)));
        for (int i = 0; i < jobDatesCopy.size(); i ++) {
            if (i == jobDatesCopy.size() - 1) {
                return false;
            }
            if (jobDatesCopy.get(i).get(END_DATE).getTime() >= jobDatesCopy.get(i + 1).get(START_DATE).getTime()) {
                Map<String, Date> combinedDateRange = new HashMap<>();
                combinedDateRange.put(START_DATE, jobDatesCopy.get(i).get(START_DATE));
                combinedDateRange.put(END_DATE, jobDatesCopy.get(i + 1).get(END_DATE));
                jobDates.add(combinedDateRange);
                jobDates.remove(jobDatesCopy.get(i));
                jobDates.remove(jobDatesCopy.get(i + 1));
                return true;
            }
        }
        return false;
    }

    private static void printResults(List<Map<String, Date>> jobDates) {
        jobDates.sort(Comparator.comparing(dateRange -> dateRange.get(START_DATE)));

        AtomicInteger totalYears = new AtomicInteger();
        AtomicInteger totalMonths = new AtomicInteger();

        System.out.println("\n----------------------------------------");
        jobDates.forEach(job -> {
            Date startDate = job.get(START_DATE);
            Date endDate = job.get(END_DATE);
            System.out.print((startDate.getMonth() + 1) + "/" + (startDate.getYear() + 1900)
                    + " - " + (endDate.getMonth() + 1) + "/" + (endDate.getYear() + 1900) + ": ");

            long diffInMillies = endDate.getTime() - startDate.getTime();
            long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            int years = (int) days / 365;
            int months = (int) ((days - years * 365) / 28);

            totalMonths.set(totalMonths.get() + months);
            totalYears.set(totalYears.get() + years);
            if (totalMonths.get() >= 12) {
                int yearsFromMonths = totalMonths.get() / 12;

                totalYears.set(totalYears.get() + yearsFromMonths);
                totalMonths.set(totalMonths.get() - yearsFromMonths * 12);
            }

            System.out.println(years + " years and " + months + " months");
        });
        System.out.println("----------------------------------------");
        System.out.println("Total work experience: " + totalYears.get() + " years and " + totalMonths.get() + " months");
        System.out.println("----------------------------------------");
    }
}
