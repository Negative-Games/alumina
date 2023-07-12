/*
 *
 *  *  MIT License
 *  *
 *  * Copyright (C) 2023 Negative Games
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package games.negative.alumina.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Represents a number utility class used to handle numbers, parse them, etc.
 */
public class NumberUtil {

    /*
     * Format a number to a fancy format.
     */
    private static final DecimalFormat FANCY_FORMAT = new DecimalFormat("###,###,###,###,###.##");

    /**
     * Parse a number to a fancy format.
     * @param number Number to parse
     * @return Parsed number
     */
    public static String parse(int number) {
        return FANCY_FORMAT.format(number);
    }

    /**
     * Parse a number to a fancy format.
     * @param number Number to parse
     * @return Parsed number
     */
    public static String parse(long number) {
        return FANCY_FORMAT.format(number);
    }

    /**
     * Parse a number to a fancy format.
     * @param number Number to parse
     * @return Parsed number
     */
    public static String parse(double number) {
        return FANCY_FORMAT.format(number);
    }

    /**
     * Parse a number to a fancy format.
     * @param number Number to parse
     * @return Parsed number
     */
    public static String parse(float number) {
        return FANCY_FORMAT.format(number);
    }

    /**
     * Parse a number to a fancy format.
     * @param number Number to parse
     * @return Parsed number
     */
    public static String parse(short number) {
        return FANCY_FORMAT.format(number);
    }

    /**
     * Parse a number to a fancy format.
     * @param number Number to parse
     * @return Parsed number
     */
    public static String parse(byte number) {
        return FANCY_FORMAT.format(number);
    }

    /**
     * This method will check if the provided text is a {@link Integer}.
     * @param text Text to check
     * @return If the text is a {@link Integer}
     */
    public static boolean isInteger(@NotNull String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method will get an {@link Integer} from the provided text.
     * @param text Text to get the {@link Integer} from
     * @return {@link Integer} from the text
     * @throws NullPointerException If the text is not an {@link Integer}
     */
    @Nullable
    public Integer getInteger(@NotNull String text) {
        if (!isInteger(text)) return null;

        return Integer.parseInt(text);
    }

    /**
     * This method will check if the provided text is a {@link Long}.
     * @param text Text to check
     * @return If the text is a {@link Long}
     */
    public static boolean isLong(@NotNull String text) {
        try {
            Long.parseLong(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method will get an {@link Long} from the provided text.
     * @param text Text to get the {@link Long} from
     * @return {@link Long} from the text
     * @throws NullPointerException If the text is not an {@link Long}
     */
    @Nullable
    public Long getLong(@NotNull String text) {
        if (!isLong(text)) return null;

        return Long.parseLong(text);
    }

    /**
     * This method will check if the provided text is a {@link Double}.
     * @param text Text to check
     * @return If the text is a {@link Double}
     */
    public static boolean isDouble(@NotNull String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method will get an {@link Double} from the provided text.
     * @param text Text to get the {@link Double} from
     * @return {@link Double} from the text
     * @throws NullPointerException If the text is not an {@link Double}
     */
    @Nullable
    public Double getDouble(@NotNull String text) {
        if (!isDouble(text)) return null;

        return Double.parseDouble(text);
    }

    /**
     * This method will check if the provided text is a {@link Float}.
     * @param text Text to check
     * @return If the text is a {@link Float}
     */
    public static boolean isFloat(@NotNull String text) {
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method will get an {@link Float} from the provided text.
     * @param text Text to get the {@link Float} from
     * @return {@link Float} from the text
     * @throws NullPointerException If the text is not an {@link Float}
     */
    @Nullable
    public Float getFloat(@NotNull String text) {
        if (!isFloat(text)) return null;

        return Float.parseFloat(text);
    }

    /**
     * This method will check if the provided text is a {@link Short}.
     * @param text Text to check
     * @return If the text is a {@link Short}
     */
    public static boolean isShort(@NotNull String text) {
        try {
            Short.parseShort(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method will get an {@link Short} from the provided text.
     * @param text Text to get the {@link Short} from
     * @return {@link Short} from the text
     * @throws NullPointerException If the text is not an {@link Short}
     */
    @Nullable
    public Short getShort(@NotNull String text) {
        if (!isShort(text)) return null;

        return Short.parseShort(text);
    }

}