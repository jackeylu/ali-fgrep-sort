package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Main {

    protected static String[] call(String baseDir, String suffix, String keyword) {
        String[] filteredLines = listFilesAndGrep(baseDir, suffix, keyword);
        sortLines(filteredLines, false);
        String[] uniqueLines = uniqueAndCount(filteredLines);
        sortLines(uniqueLines, true);
        return uniqueLines;
    }

    public static void main(String[] args) {
        String[] filteredLines = call(args[1], args[2], args[3]);
        for (String filteredLine : filteredLines) {
            System.out.println(filteredLine);
        }
    }

    protected static String[] uniqueAndCount(String[] sortedLines) {
        if (sortedLines.length == 0) {
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<>(sortedLines.length);
        String current = sortedLines[0];
        int count = 0;
        for (int i = 0; i < sortedLines.length; i++) {
            String line = sortedLines[i];
            if (line.equals(current)) {
                count += 1;
                if (i == sortedLines.length - 1) {
                    result.add(String.format("%d,%s", count, current));
                }
            } else {
                result.add(String.format("%d,%s", count, current));
                count = 1;
                current = line;
            }
        }
        return result.toArray(new String[0]);
    }

    protected static void sortLines(String[] lines, boolean reversed) {
        Arrays.sort(lines, (o1, o2) -> {
            int r = o1.compareTo(o2);
            return reversed ? -r : r;
        });
    }

    protected static String[] listFilesAndGrep(String directory, String suffix, String keyword) {
//        String convertedPattern = pattern.replace("*", "\\*");
        File baseDir = new File(directory);
        if (baseDir.exists()) {
            if (baseDir.isDirectory()) {
//                File[] files = baseDir.listFiles((dir, name) -> Pattern.matches(convertedPattern, name));
                File[] files = baseDir.listFiles((dir, name) -> name.endsWith(suffix));
                ExecutorService executor = Executors.newCachedThreadPool();
                LinkedBlockingQueue<String[]> linesList = new LinkedBlockingQueue<>();
                for (File file: files) {
                    executor.submit(() -> {
                        String[] lines;
                        try {
                            lines = extractKeywordLines(file, keyword);
                            linesList.put(lines);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
                try {
                    executor.shutdown();
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

                    Object[] allOfLines = new Object[files.length];
                    String[] lines = null;
                    int totalSize = 0;
                    int j = 0;
                    while ((lines = linesList.poll()) != null){
                        totalSize += lines.length;
                        allOfLines[j] = lines;
                        j++;
                    }
                    String[] result = new String[totalSize];
                    int begin = 0;
                    for (Object allOfLine : allOfLines) {
                        lines = (String[]) allOfLine;
                        System.arraycopy(lines, 0, result, begin, lines.length);
                        begin += lines.length;
                    }
                    return result;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return new String[0];
            }
        } else {
            return new String[0];
        }
        return new String[0];
    }

    protected static String[] extractKeywordLines(File file, String keyword)  {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            LinkedList<String> list = new LinkedList<>();
            while ((line = reader.readLine())!=null){
                if (line.contains(keyword)){
                    list.add(line);
                }
            }
            return list.toArray(new String[0]);
        } catch (IOException e) {
            System.err.println(file.getAbsolutePath());
            e.printStackTrace();
        }
        return new String[0];
    }
}