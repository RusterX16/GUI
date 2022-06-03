package dev.ruster;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class GUI {

    private final Inventory inventory;
    private final String name;
    private final int size;
    private Player owner;
    private GUI previousGUI;
    private GUI nextGUI;

    /**
     * Create a new GUI inventory for a owner
     *
     * @param name  The name displayed at the top of the inventory
     * @param rows  The number of row of the inventory : between 1 and 6
     * @param owner The owner of the inventory
     */
    @Contract(pure = true)
    public GUI(String name, int rows, Player owner) {
        if(rows < 0 || rows > 6) {
            throw new IllegalArgumentException("GUI must contain between 1 and 6 rows");
        }
        if(owner != null) {
            this.owner = owner;
        }
        this.name = name;
        this.size = rows * 9;
        inventory = Bukkit.createInventory(null, size, name);
    }

    /**
     * Create a new GUI inventory
     *
     * @param name The name displayed at the top of the inventory
     * @param rows The number of rows of the inventory : between 1 and 6
     */
    @Contract(pure = true)
    public GUI(String name, int rows) {
        this(name, rows, null);
    }

    /**
     * Create a new GUI inventory by copy
     *
     * @param gui The GUI to copy
     */
    @Contract(pure = true)
    public GUI(@NotNull GUI gui) {
        this.inventory = gui.inventory;
        this.owner = gui.owner;
        this.name = gui.name;
        this.size = gui.size;
        this.previousGUI = gui.previousGUI;
        this.nextGUI = gui.nextGUI;
    }

    /**
     * Add items to the inventory from the top left to the bottom right
     *
     * @param items The items you want to add
     */
    public void add(ItemStack... items) {
        if(Arrays.stream(items).anyMatch(i -> i == null || i.getType() == Material.AIR)) {
            throw new IllegalArgumentException("Use remove method to remove an item from inventory");
        }
        inventory.addItem(items);
    }

    /**
     * Add items to inventory from the top left to the bottom right
     *
     * @param materials The material of the item you want to add
     */
    public void add(Material... materials) {
        Arrays.stream(materials).forEach(m -> add(new ItemStack(m)));
    }

    /**
     * Add an item to the inventory at a precise slots
     *
     * @param item  The item you want to add
     * @param slots The slots where the item should be added
     */
    public void set(ItemStack item, int @NotNull ... slots) {
        if(item == null || item.getType() == Material.AIR) {
            throw new NullPointerException("Item is null");
        }
        for(int i : slots) {
            if(i < 0 || i > size) {
                throw new ArrayIndexOutOfBoundsException("slot is out of bounds");
            }
            inventory.setItem(i, item);
        }
    }

    /**
     * Add an item to the inventory at a precise slot
     *
     * @param material The material of the item
     * @param slots    The slots where the item should be added
     */
    public void set(Material material, int... slots) {
        set(new ItemStack(material), slots);
    }

    /**
     * Check if the GUI contains an item
     *
     * @param item The item to check
     * @return True if the GUI contains the item
     */
    public boolean contains(ItemStack item) {
        return inventory.contains(item);
    }

    public void fill(int start, int end, ItemStack[] items, boolean override) {
        if(start < 0 || start >= size || end >= size || end < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bound");
        }
        for(int i = Math.min(start, end); i < Math.max(start, end); i++) {
            for(ItemStack it : items) {
                if(override) {
                    inventory.setItem(i, it);
                }
            }
        }
    }

    /**
     * Fill the inventory with item type array from a start index to an end index
     *
     * @param start The start index
     * @param end   The end index
     * @param items The ItemStack array to fill
     */
    public void fill(int start, int end, ItemStack[] items) {
        fill(start, end, items, false);
    }

    /**
     * Fill the inventory with item type array on slots contains in an array
     *
     * @param slots    The array of index to place
     * @param items    The ItemStack array to fill
     * @param override Override if an item is already present
     */
    public void fill(int @NotNull [] slots, ItemStack[] items, boolean override) {
        if(slots.length == 0) {
            return;
        }
        Arrays.stream(slots).filter(i -> override).forEach(i -> Arrays.stream(items).forEach(it -> set(it, i)));
    }

    /**
     * Fill the inventory with item type array on slots contains in an array
     *
     * @param slots The array of index to place
     * @param items The ItemStack array to fill
     */
    public void fill(int @NotNull [] slots, ItemStack[] items) {
        fill(slots, items, false);
    }

    /**
     * Fill the inventory with an item from a start index to an end index
     *
     * @param start    The start index
     * @param end      The end index
     * @param item     The item to fill with
     * @param override Override if an item is already present
     */
    public void fill(int start, int end, ItemStack item, boolean override) {
        if(!override) {
            return;
        }
        fill(start, end, new ItemStack[]{item});
    }

    /**
     * Fill the inventory with an item from a start index to an end index
     *
     * @param start The start index
     * @param end   The end index
     * @param item  The item to fill with
     */
    public void fill(int start, int end, ItemStack item) {
        fill(start, end, new ItemStack[]{item}, false);
    }

    /**
     * Fill the inventory with an item on index contains in an array
     *
     * @param slots    The array of index
     * @param item     The item to fill with
     * @param override Override if an item is already present
     */
    public void fill(int @NotNull [] slots, ItemStack item, boolean override) {
        if(!override) {
            return;
        }
        fill(slots, new ItemStack[]{item});
    }

    /**
     * Fill the inventory on index contains in an array
     *
     * @param slots The array of index
     * @param item  The item to fill with
     */
    public void fill(int @NotNull [] slots, ItemStack item) {
        fill(slots, new ItemStack[]{item}, false);
    }

    /**
     * Fill the whole inventory of a given item
     * @param item The item to fill with
     * @param override Override if an item is already present
     */
    public void fill(ItemStack item, boolean override) {
        for(int i = 0; i < size; i++) {
            if(isEmpty(i) || override) {
                set(item, i);
            }
        }
    }

    /**
     * Fill the whole inventory of a given item
     * @param item The item to fill with
     */
    public void fill(ItemStack item) {
        fill(item, false);
    }

    /**
     * Place items in a straight horizontal line in the GUI
     *
     * @param row      The row where to place the items
     * @param items    The item to place 9 times
     * @param override Replace existing items if no null
     */
    public void horizontalFill(int row, ItemStack[] items, boolean override) {
        if(row < 0 || row > 5) {
            throw new IllegalArgumentException("Row must be between 0 and 5");
        }
        fill(row * 9, row * 9 + 9, items, override);
    }

    /**
     * Place items in a straight horizontal line in the GUI
     *
     * @param row   The row where to place the items
     * @param items The item to place 9 times
     */
    public void horizontalFill(int row, ItemStack[] items) {
        horizontalFill(row, items, false);
    }

    /**
     * Place items in a straight horizontal line in the GUI
     *
     * @param row      The row where to place the items
     * @param item     The item to place 9 times
     * @param override Replace existing items if not null
     */
    public void horizontalFill(int row, ItemStack item, boolean override) {
        ItemStack[] array = new ItemStack[9];
        Arrays.stream(array).forEach(i -> i = item);

        horizontalFill(row, array, override);
    }

    /**
     * Place items in a straight horizontal line in the GUI
     *
     * @param row  The row where to place the items
     * @param item The items to place 6 times
     */
    public void horizontalFill(int row, ItemStack item) {
        horizontalFill(row, item, false);
    }

    /**
     * Place items in a straight vertical line in the GUI
     *
     * @param column   The column where to place the items
     * @param items    The array of items to place.<br>
     *                 Will place the first 6 items at maximum and ignore the last items after the 6th index.
     * @param override Replace existing items if not null
     */
    public void verticalFill(int column, ItemStack[] items, boolean override) {
        if(column < 0 || column > 7) {
            throw new IllegalArgumentException("Column must be between 0 and 7");
        }
        for(int i = column; i < 6; i += 9) {
            if(isEmpty(i) || override) {
                set(items[i], i);
            }
        }
    }

    /**
     * Place items in a straight vertical line in the GUI
     *
     * @param column The column where to place the items
     * @param items  The array of items to place.<br>
     *               Will place the first 6 items at maximum and ignore the last items after the 6th index.
     */
    public void verticalFill(int column, ItemStack[] items) {
        verticalFill(column, items, false);
    }

    /**
     * Place items in a straight vertical line in the GUI
     *
     * @param column   The column where to place the items
     * @param item     The item to place 6 times
     * @param override Replace existing items if not null
     */
    public void verticalFill(int column, ItemStack item, boolean override) {
        ItemStack[] array = new ItemStack[size / 6];
        Arrays.stream(array).forEach(i -> i = item);

        verticalFill(column, array, override);
    }

    /**
     * Place items in a straight vertical line in the GUI
     *
     * @param column The column where to place the items
     * @param item   The item to place 8 times
     */
    public void verticalFill(int column, ItemStack item) {
        verticalFill(column, item, false);
    }

    /**
     * Remove an item from the inventory from the slot given
     *
     * @param slot The slot where the item should be removed
     */
    public void remove(int slot) {
        ItemStack item = getItem(slot);

        if(item != null && item.getType() != Material.AIR) {
            inventory.remove(item);
        }
    }

    /**
     * Remove items from the inventory by an array of ItemStack given
     *
     * @param items The items you want to remove
     */
    public void remove(ItemStack @NotNull ... items) {
        for(ItemStack i : items) {
            getSlots(i).forEach(this::remove);
        }
    }

    /**
     * Get the item by the slot where the item should be
     *
     * @param slot The slot where the item should be placed
     * @return The item of the slot where it's
     */
    public @Nullable ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    /**
     * Get the first slot where an item is present .<br>
     *
     * @param item The item you want to get slot at
     * @return The slot where the item is placed.<br>
     * Will return -1 if absent.
     */
    public int getSlot(ItemStack item) {
        return getSlots(item).stream().findFirst().orElse(-1);
    }

    /**
     * Get the slots where an item is present.<br>
     * Will be empty if your item is not present in the GUI.
     *
     * @param item The item you want to get slots at
     * @return A set of the slots where the item is placed
     */
    public Set<Integer> getSlots(ItemStack item) {
        Set<Integer> slots = new HashSet<>();

        IntStream.range(0, size).filter(i -> item == getItem(i)).forEach(slots::add);
        return slots;
    }

    public Set<Integer> getSlots(ItemStack[] items) {
        Set<Integer> slots = new HashSet<>();

        IntStream.range(0, size).filter(i -> {
            boolean b = getItem(i) == Arrays.stream(items).filter(is -> is == getItem(i)).findFirst().get();
            return b;
        }).forEach(slots::add);
        return slots;
    }

    /**
     * Open the GUI to a Player
     *
     * @param player The player who open the inventory
     */
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    /**
     * Open the GUI to the owner
     */
    public void open() {
        if(owner == null) {
            throw new NullPointerException("Owner of the GUI is null");
        }
        open(owner);
    }

    /**
     * Open the previous GUI of this GUI to a player
     *
     * @param player The player to open the next GUI
     */
    public void openPreviousGUI(Player player) {
        if(previousGUI == null) {
            throw new NullPointerException("Previous GUI is null");
        }
        previousGUI.open(player);
    }

    /**
     * Open the previous GUI of this GUI to the owner
     */
    public void openPreviousGUI() {
        openPreviousGUI(owner);
    }

    /**
     * Open the next GUI of this GUI to the player
     *
     * @param player The player to open the next GUI
     */
    public void openNextGUI(@NotNull Player player) {
        if(nextGUI == null) {
            throw new NullPointerException("Next GUI is null");
        }
        nextGUI.open(player);
    }

    /**
     * Open the next GUI of this GUI to the owner
     */
    public void openNextGUI() {
        openNextGUI(owner);
    }

    /**
     * Delete all the items in the GUI
     */
    public void clear() {
        inventory.clear();
    }

    /**
     * Check if the entier GUI contains is empty or not
     *
     * @return Empty or not
     */
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    /**
     * Check if a slot of the GUI is empty or not
     *
     * @param slot The slot where to check
     * @return The slot is empty or not
     */
    public boolean isEmpty(int slot) {
        ItemStack item = getItem(slot);
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Close the GUI for the owner
     */
    public void close() {
        if(owner == null) {
            throw new NullPointerException("Owner of the GUI is null");
        }
        close(owner);
    }

    /**
     * Close the GUI to a specific player
     * @param player The player to close the GUI
     */
    public void close(@NotNull Player player) {
        player.closeInventory();
    }

    /**
     * @return The last index of the GUI
     */
    public int lastIndex() {
        return size - 1;
    }

    /**
     * @return The Bukkit Inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @return The GUI name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The GUI size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return The GUI owner
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @param owner The GUI owner
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * @return The previous GUI
     */
    public GUI getPreviousGUI() {
        return previousGUI;
    }

    /**
     * @param previousGUI The previous GUI
     */
    public void setPreviousGUI(GUI previousGUI) {
        this.previousGUI = previousGUI;
    }

    /**
     * @return The next GUI
     */
    public GUI getNextGUI() {
        return nextGUI;
    }

    /**
     * @param nextGUI The next GUI
     */
    public void setNextGUI(GUI nextGUI) {
        this.nextGUI = nextGUI;
    }
}
