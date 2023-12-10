package dev.aurelium.auraskills.common.util.text;

import java.util.*;
import java.util.function.Supplier;

public class TextUtil {

    public static String replace(String source, String os, String ns) {
        if (source == null) {
            return null;
        }
        int i = 0;
        if ((i = source.indexOf(os, i)) >= 0) {
            char[] sourceArray = source.toCharArray();
            char[] nsArray = ns.toCharArray();
            int oLength = os.length();
            StringBuilder buf = new StringBuilder(sourceArray.length);
            buf.append(sourceArray, 0, i).append(nsArray);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = source.indexOf(os, i)) > 0) {
                buf.append(sourceArray, j, i - j).append(nsArray);
                i += oLength;
                j = i;
            }
            buf.append(sourceArray, j, sourceArray.length - j);
            source = buf.toString();
            buf.setLength(0);
        }
        return source;
    }

    public static String replace(String source, String... rep) {
        if (source == null) {
            return null;
        }
        if (rep.length % 2 != 0) {
            throw new IllegalArgumentException("The number of arguments must be even!");
        }
        for (int i = 0; i < rep.length; i += 2) {
            source = replace(source, rep[i], rep[i + 1]);
        }
        return source;
    }

    public static String replace(String source, String os1, String ns1, String os2, String ns2) {
        return replace(replace(source, os1, ns1), os2, ns2);
    }

    public static String replace(String source, String os1, String ns1, String os2, String ns2, String os3, String ns3) {
        return replace(replace(replace(source, os1, ns1), os2, ns2), os3, ns3);
    }

    public static String replace(String source, String os1, String ns1, String os2, String ns2, String os3, String ns3, String os4, String ns4) {
        return replace(replace(replace(replace(source, os1, ns1), os2, ns2), os3, ns3), os4, ns4);
    }

    public static String replace(String source, String os1, String ns1, String os2, String ns2, String os3, String ns3, String os4, String ns4, String os5, String ns5) {
        return replace(replace(replace(replace(replace(source, os1, ns1), os2, ns2), os3, ns3), os4, ns4), os5, ns5);
    }

    public static String replace(String source, String os1, String ns1, String os2, String ns2, String os3, String ns3, String os4, String ns4, String os5, String ns5, String os6, String ns6) {
        return replace(replace(replace(replace(replace(replace(source, os1, ns1), os2, ns2), os3, ns3), os4, ns4), os5, ns5), os6, ns6);
    }

    public static String replace(String source, Replacer rep) {
        if (source == null) {
            return null;
        }
        for (Map.Entry<String, Supplier<String>> entry : rep.getReplacements().entrySet()) {
            source = replace(source, entry.getKey(), entry.getValue());
        }
        return source;
    }

    public static String replace(String input, String target, Supplier<String> replacement) {
        if (input == null || target == null || replacement == null) {
            throw new IllegalArgumentException("Input, target or replacement cannot be null");
        }
        if (target.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < input.length()) {
            int next = input.indexOf(target, index);
            if (next == -1) {
                result.append(input.substring(index));
                break;
            }
            result.append(input, index, next);
            result.append(replacement.get());
            index = next + target.length();
        }
        return result.toString();
    }

    public static String replaceNonEscaped(String source, String os, String ns) {
        String replaced = replace(source, "\\" + os, "\uE000"); // Replace escaped characters with intermediate char
        replaced = replace(replaced, os, ns); // Replace normal chars
        return replace(replaced, "\uE000", os); // Replace intermediate with original
    }

    public static String removeEnd(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    public static String capitalize(final String str) {
        final int strLen = length(str);
        if (strLen == 0) {
            return str;
        }

        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            // already capitalized
            return str;
        }

        final int[] newCodePoints = new int[strLen]; // cannot be longer than the char array
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint; // copy the remaining ones
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return "";
        }
        final char[] buf = new char[repeat];
        Arrays.fill(buf, ch);
        return new String(buf);
    }

    private static Set<Integer> generateDelimiterSet(final char[] delimiters) {
        final Set<Integer> delimiterHashSet = new HashSet<>();
        if (delimiters == null || delimiters.length == 0) {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[] {' '}, 0));
            }

            return delimiterHashSet;
        }

        for (int index = 0; index < delimiters.length; index++) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }

    public static String capitalizeWord(final String str, final char... delimiters) {
        if (isEmpty(str)) {
            return str;
        }
        final Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for (int index = 0; index < strLen;) {
            final int codePoint = str.codePointAt(index);

            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String capitalizeWord(final String str) {
        return capitalizeWord(str, null);
    }

    public static boolean contains(String[] source, String target) {
        boolean matches = false;
        for (String str : source) {
            if (str.equalsIgnoreCase(target)) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    /**
     * Gets a list of all substrings between pairs of curly braces.
     *
     * @param text The text to get placeholders from
     * @return A list of each placeholder, not including curly braces
     */
    public static List<String> getPlaceholders(String text) {
        List<String> placeholders = new ArrayList<>();
        int index = 0;
        while (index < text.length()) {
            int openIndex = text.indexOf('{', index);
            if (openIndex == -1) {
                break;
            }
            int closeIndex = text.indexOf('}', openIndex);
            if (closeIndex == -1) {
                break;
            }
            String placeholder = text.substring(openIndex + 1, closeIndex);
            placeholders.add(placeholder);
            index = closeIndex + 1;
        }
        return placeholders;
    }

    public static String wrapText(String input, int maxLength, String insertion) {
        StringBuilder sb = new StringBuilder(input);

        int i = 0;
        List<String> lines = new ArrayList<>();
        while (i < input.length()) {
            String sub = substringIgnoreFormatting(sb.toString(), i, Math.min(i + maxLength, input.length()));
            int addedLength = 0;
            if (!sub.equals(" ")) {
                String added = substringIgnoreFormatting(sub, 0, Math.min(maxLength, sub.length()));
                int lastSpace = added.lastIndexOf(" ");
                if (lastSpace != -1) { // Check if section contains spaces
                    if (sb.charAt(Math.min(i + added.length(), sb.length() - 1)) == ' ' || i + added.length() == sb.length()) { // Complete word or last word
                        lines.add(added);
                        addedLength = added.length();
                    } else { // Cut off word
                        String addedCutOff = added.substring(0, lastSpace);
                        addedLength = addedCutOff.length();
                        lines.add(addedCutOff);
                    }
                } else { // Add the max number of characters and cut off word
                    lines.add(added);
                    addedLength = added.length();
                }
            }
            i = i + addedLength;
            if (i < sb.length()) {
                if (sb.charAt(i) == ' ') {
                    i++;
                }
            }
        }
        StringBuilder output = new StringBuilder();
        String lastInsertion = "";
        for (String line : lines) {
            lastInsertion = insertion;
            output.append(line).append(insertion);
        }
        output.replace(output.length() - lastInsertion.length(), output.length(), "");
        return output.toString();
    }

    private static String substringIgnoreFormatting(String input, int start, int end) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        if (start < 0 || end > input.length() || start > end) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean insideBrackets = false;
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '<') {
                insideBrackets = true;
            }
            if (c == '>') {
                insideBrackets = false;
            }
            if (!insideBrackets || count <= start) {
                count++;
            }
            if (count > start && count <= end) {
                result.append(c);
            }
            if (count > end) {
                break;
            }
        }

        return result.toString();
    }

}
