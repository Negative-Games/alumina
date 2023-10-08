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

package games.negative.alumina.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import games.negative.alumina.menu.base.AluminaMenu;
import games.negative.alumina.menu.base.MenuItem;
import games.negative.alumina.menu.holder.ChestMenuHolder;
import games.negative.alumina.util.ColorUtil;
import games.negative.alumina.util.MathUtil;
import games.negative.alumina.util.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

/**
 * Represents a intractable chest menu.
 */
public abstract class ChestMenu implements AluminaMenu {

    private String title = "Chest Menu";
    private int rows = 1;

    private Inventory inventory;
    private final Map<Integer, MenuItem> items;
    private final Map<String, MenuItem> byKey;

    private Queue<Integer> freeSlots;

    /**
     * Using this constructor will allow you to create a new Chest Menu!
     *
     * @param title The title of the menu.
     * @param rows The amount of rows the menu should have.
     * @apiNote The number of rows must be greater than 0 and less than or equal to 6.
     */
    public ChestMenu(@NotNull String title, int rows) {
        Preconditions.checkArgument(MathUtil.between(rows, 1, 6), "Rows must be greater than 0 and less than or equal to 6.");

        this.rows = rows * 9;
        this.title = title;

        this.inventory = Bukkit.createInventory(new ChestMenuHolder(this), this.rows, ColorUtil.translate(title));
        this.items = Maps.newHashMap();
        this.byKey = Maps.newHashMap();
    }

    /**
     * Using this constructor will allow you to create a new Chest Menu!
     */
    public ChestMenu() {
        this.items = Maps.newHashMap();
        this.byKey = Maps.newHashMap();
    }

    /**
     * This method will allow you to set the item in a slot.
     * @param slot The slot to set the item in.
     * @param item The item to set.
     * @param functionKey The function key to set.
     *                    When the function key is null, the item will not be functional.
     */
    @Override
    public void setItem(int slot, @NotNull ItemStack item, @Nullable String functionKey) {
        if (this.freeSlots != null)
            this.freeSlots.remove(slot);

        MenuItem removed = items.remove(slot);
        if (removed != null)
            byKey.remove(removed.key());

        applyFunction(item, functionKey);

        MenuItem menuItem = new MenuItem(item, functionKey);
        items.put(slot, menuItem);

        if (functionKey == null) return;

        byKey.put(functionKey, menuItem);
    }

    /**
     * This method will allow you to add an item to the menu.
     * @param item The item to add.
     * @param functionKey The function key to set.
     */
    @Override
    public void addItem(@NotNull ItemStack item, @Nullable String functionKey) {
        // We're using a set to store the free slots, so we do not need to loop through the entire inventory
        // whenever we need to find a free slot.
        // Even tho in the grand scheme of things, it's a micro-performance boost, it's still a boost, I guess.
        if (this.freeSlots == null) {
            this.freeSlots = new ArrayDeque<>();

            for (int i = 0; i < (9 * rows); i++) {
                if (this.items.containsKey(i)) continue;

                this.freeSlots.add(i);
            }
        }

        Integer available = this.freeSlots.poll();
        if (available == null) return;

        setItem(available, item, functionKey);
    }

    /**
     * Applies the function key to the item's data.
     * @param item The item.
     * @param key The function key.
     */
    private void applyFunction(@NotNull ItemStack item, @Nullable String key) {
        if (key == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NBTEditor.set(meta, AluminaMenu.FUNCTION_KEY, PersistentDataType.STRING, key);

        item.setItemMeta(meta);
    }

    /**
     * This method will allow you to clear a slot.
     * @param slot The slot to clear.
     */
    @Override
    public void clearSlot(int slot) {
        inventory.clear(slot);

        // Ensure to remove from the map.
        MenuItem removed = items.remove(slot);
        if (removed != null)
            byKey.remove(removed.key());
    }

    /**
     * This method will allow you to open the menu for a player.
     * @param player The player to open the menu for.
     */
    @Override
    public void open(@NotNull Player player) {
        refresh();

        player.openInventory(inventory);
    }

    /**
     * This method will allow you to refresh the menu.
     */
    @Override
    public void refresh() {
        if (inventory == null) this.inventory = Bukkit.createInventory(new ChestMenuHolder(this), rows, ColorUtil.translate(title));

        items.forEach((index, item) -> inventory.setItem(index, item.item()));
    }

    /**
     * This method will allow you to set the menu title.
     * @param title The title to set.
     */
    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public void setRows(int rows) {
        Preconditions.checkArgument(MathUtil.between(rows, 1, 6), "Rows must be greater than 0 and less than or equal to 6.");

        this.rows = rows * 9;
    }

    /**
     * This method is invoked when a player clicks the menu, which allows you to handle clicks.
     * @param player The player who clicked the menu.
     * @param event  The event.
     */
    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        // Override this method to handle clicks.
    }

    /**
     * This method is invoked when a player clicks a functional item in the menu.
     * @param player The player who clicked the menu.
     * @param item   The item that was clicked.
     * @param event  The event.
     */
    @Override
    public void onFunctionClick(@NotNull Player player, @NotNull MenuItem item, @NotNull InventoryClickEvent event) {
        // Override this method to handle function clicks.
    }

    /**
     * This method is invoked when a player closes the menu, which allows you to handle close.
     * @param player The player who closed the menu.
     * @param event  The event.
     */
    @Override
    public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent event) {
        // Override this method to handle close.
    }

    /**
     * This method is invoked when a player opens the menu, which allows you to handle open.
     * @param player The player who opened the menu.
     * @param event  The event.
     */
    @Override
    public void onOpen(@NotNull Player player, @NotNull InventoryOpenEvent event) {
        // Override this method to handle open.
    }

    /**
     * Get all menu items.
     * @return The menu items.
     */
    public Map<Integer, MenuItem> getItems() {
        return items;
    }

    /**
     * Get the menu title.
     * @return The menu title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the amount of rows.
     * @return The amount of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Get a menu item by function key.
     * @param key The function key.
     * @return The menu item.
     */
    @Nullable
    public MenuItem byKey(@NotNull String key) {
        return byKey.getOrDefault(key, null);
    }
}
