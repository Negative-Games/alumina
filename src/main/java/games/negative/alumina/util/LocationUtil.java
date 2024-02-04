/*
 *  MIT License
 *
 * Copyright (C) 2024 Negative Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package games.negative.alumina.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A location utility to handle some location-related tasks.
 */
public class LocationUtil {

    /**
     * Check if a location is inside a cuboid.
     *
     * @param location The location to check.
     * @param min      The minimum location of the cuboid.
     * @param max      The maximum location of the cuboid.
     * @return Whether the location is inside the cuboid.
     */
    public static boolean isInside(@NotNull Location location, @NotNull Location min, @NotNull Location max) {
        Preconditions.checkNotNull(location, "'location' cannot be null!");
        Preconditions.checkNotNull(min, "'min' cannot be null!");
        Preconditions.checkNotNull(max, "'max' cannot be null!");

        World locationWorld = location.getWorld();
        World minWorld = min.getWorld();
        World maxWorld = max.getWorld();

        Preconditions.checkNotNull(locationWorld, "Location world cannot be null!");
        Preconditions.checkNotNull(minWorld, "Min world cannot be null!");
        Preconditions.checkNotNull(maxWorld, "Max world cannot be null!");

        Preconditions.checkArgument(minWorld.equals(maxWorld), "Min world must be the same as max world!");
        Preconditions.checkArgument(locationWorld.equals(minWorld), "Location world must be the same as min world!");
        Preconditions.checkArgument(locationWorld.equals(maxWorld), "Location world must be the same as max world!");

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        int x1 = Math.min(min.getBlockX(), max.getBlockX());
        int y1 = Math.min(min.getBlockY(), max.getBlockY());
        int z1 = Math.min(min.getBlockZ(), max.getBlockZ());

        int x2 = Math.max(min.getBlockX(), max.getBlockX());
        int y2 = Math.max(min.getBlockY(), max.getBlockY());
        int z2 = Math.max(min.getBlockZ(), max.getBlockZ());

        return (x >= x1 && x <= x2) && (y >= y1 && y <= y2) && (z >= z1 && z <= z2);
    }
}
