package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

class MainTest {

    @org.junit.jupiter.api.Test
    void call() {
        String baseDir = MainTest.class.getClassLoader().getResource("right_one").getPath();
        String suffix = ".log";
        String keyword = "Login";
        String[] lines = Main.call(baseDir, suffix, keyword);

        String[] expected = new String[]{
                "3,中文里面有Login的能找到吗？",
                "3,Login at first",
                "2,At last Login",
                "2,      Login"
        };
        StringBuilder builder = new StringBuilder();
        for (String line: lines) {
            builder.append(line + "\n");
        }
        Assertions.assertArrayEquals(expected, lines, builder.toString());
    }

    @org.junit.jupiter.api.Test
    void uniqueAndCount() {
        String[] sortedLines = new String[]{
                "", "", "123", "abc", "abc"
        };
        String[] uniqueLines = Main.uniqueAndCount(sortedLines);
        Assertions.assertArrayEquals(new String[]{
                "2,", "1,123", "2,abc"
        }, uniqueLines);

        sortedLines = new String[]{};
        uniqueLines = Main.uniqueAndCount(sortedLines);
        Assertions.assertArrayEquals(sortedLines, uniqueLines);

        sortedLines = new String[]{"abc"};
        uniqueLines = Main.uniqueAndCount(sortedLines);
        Assertions.assertArrayEquals(new String[]{"1,abc"}, uniqueLines);
    }

    @org.junit.jupiter.api.Test
    void sortLines() {
        String[] emptyLines = new String[]{};
        Main.sortLines(emptyLines, true);
        Assertions.assertArrayEquals(new String[]{}, emptyLines);

        String[] lines = new String[]{
                "",
                "abc",
                "123",
                ""
        };
        Main.sortLines(lines, false);
        String[] expected = new String[]{
                "", "", "123", "abc"
        };
        Assertions.assertArrayEquals(expected, lines);

        expected = new String[]{
                "abc", "123", "", ""
        };
        Main.sortLines(lines, true);
        Assertions.assertArrayEquals(expected, lines);
    }

    @org.junit.jupiter.api.Test
    void listFilesAndGrep() {
        String dir = MainTest.class.getClassLoader().getResource("has_target_files_but_no_content").getPath();
        String suffix = ".log";
        String keyword = "Login";
        String[] lines = Main.listFilesAndGrep(
                dir,
                suffix,
                keyword);
        String[] expected = new String[0];
        Assertions.assertArrayEquals(expected, lines);

        dir = MainTest.class.getClassLoader().getResource("no_target_files").getPath();
        lines = Main.listFilesAndGrep(
                dir,
                suffix,
                keyword);
        expected = new String[0];
        Assertions.assertArrayEquals(expected, lines);

        dir = MainTest.class.getClassLoader().getResource("right_one").getPath();
        lines = Main.listFilesAndGrep(
                dir,
                suffix,
                keyword);
        expected = new String[]{
                "Login at first",
                "中文里面有Login的能找到吗？",
                "      Login",
                "At last Login",
                "Login at first",
                "中文里面有Login的能找到吗？",
                "      Login",
                "At last Login",
                "Login at first",
                "中文里面有Login的能找到吗？"
        };
        // to avoid the random running sequence
        Arrays.sort(expected);
        Arrays.sort(lines);
        Assertions.assertArrayEquals(expected, lines);
    }

    @Test
    void extractKeywordLines() {
//        String[] lines = Main.extractKeywordLines(new File("not-exists.log"), "Login");
//
//        String[] expected = new String[]{};
//        Assertions.assertArrayEquals(expected, lines);

        String[] lines = Main.extractKeywordLines(new File(MainTest.class.getClassLoader()
                .getResource("no-content.log").getPath()), "Login");

        String[] expected = new String[]{};
        Assertions.assertArrayEquals(expected, lines);

        lines = Main.extractKeywordLines(new File(MainTest.class.getClassLoader()
                .getResource("no-matched.log").getPath()), "Login");

        expected = new String[]{};
        Assertions.assertArrayEquals(expected, lines);

        lines = Main.extractKeywordLines(new File(MainTest.class.getClassLoader()
                .getResource("matched.log").getPath()), "Login");

        expected = new String[]{
                "Login at first",
                "中文里面有Login的能找到吗？",
                "      Login",
                "At last Login"
        };
        Assertions.assertArrayEquals(expected, lines);
    }
}