package dev.ruster;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.IntStream;

/**
 * <p><strong>Please do not remove these lines !!</strong></p>
 * <p>GUI is a tiny Java API for Spigot and Paper made to build inventory easier</p>
 * <p>You're free to use and contribute to this project</p>
 * <p>For any question, bug or info, contact me from my GitHub down below</p>
 * @author RusterX16
 * @link <a href="https://gitub.com/rusterx16/GUI">github</a>
 */
@ToString
@EqualsAndHashCode
@Getter
public class GUI {

    /**
     * A static List of GUI that contains all GUI instance to recover instances from inventories
     */
    private static final List<GUI> GUI_LIST = new LinkedList<>();
    /**
     * The bukkit inventory instance
     */
    private final Inventory inventory;
    /**
     * The name displayed at the top of the inventory
     */
    private final String name;
    /**
     * The inventory size in [9, 18, 27, 36, 45, 54]
     */
    private final int size;
    /**
     * The player owner of the inventory. Null means that the inventory doesn't have an owner
     */
    @Setter private Player owner;
    /**
     * The GUI that comes before this
     */
    @Setter private GUI previousGUI;
    /**
     * The GUI that comes after this
     */
    @Setter private GUI nextGUI;

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
        inventory = Bukkit.createInventory(owner, size, name);
        GUI_LIST.add(this);
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
        GUI_LIST.add(this);
    }

    public static GUI getFromInventory(@NotNull Inventory inventory) {
        return GUI_LIST.stream()
                .filter(gui -> gui.inventory.equals(inventory))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add items to the inventory from the top left to the bottom right
     *
     * @param items The items you want to add
     */
    public void add(@NotNull ItemStack... items) {
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
    public void add(@NotNull Material... materials) {
        Arrays.stream(materials).forEach(m -> add(new ItemStack(m)));
    }

    /**
     * Add an item to the inventory at a precise slots
     *
     * @param item  The item you want to add
     * @param slots The slots where the item should be added
     */
    public void set(@NotNull ItemStack item, int @NotNull ... slots) {
        if(item.getType() == Material.AIR) {
            throw new IllegalArgumentException("Use remove method to remove an item from inventory");
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
    public void set(@NotNull Material material, int... slots) {
        set(new ItemStack(material), slots);
    }

    /**
     * Get the ItemStack related to the given slot
     * 
     * @param slot The slot where to get the ItemStack
     * @return The ItemStack 
     */
    public ItemStack get(int slot) {
        if(slot < 0 || slot >= size) {
            throw new IllegalArgumentException("Given slot out of bound");
        }
        return inventory.getItem(slot);
    }

    /**
     * Get slots where an item is present as a Set
     * 
     * @param item The ItemStack on which to search for slots
     * @return     All the slots where the item is present 
     */
    public @NotNull Set<Integer> slots(@NotNull ItemStack item) {
        final Set<Integer> slots = new HashSet<>();
        IntStream.range(0, size).filter(i -> get(i) == item).forEach(slots::add);
        return slots;
    }

    /**
     * Check if the GUI contains an item
     *
     * @param item The item to check
     * @return True if the GUI contains the item
     */
    public boolean contains(@NotNull ItemStack item) {
        return inventory.contains(item);
    }

    /**
     * Fill the inventory with item type array from a start index to an end index and a given step
     * 
     * @param start    The start index
     * @param end      The end index
     * @param step     The step
     * @param items    The ItemStack array to fill with
     * @param override Override if an item is already present
     */
    public void fill(int start, int end, int step, ItemStack @NotNull [] items, boolean override) {
        if(start < 0 || start >= size || end >= size || end < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bound");
        }
        for(int i = Math.min(start, end); i < Math.max(start, end); i += step) {
            for(ItemStack it : items) {
                if(get(i) != null && !override) {
                    continue;
                }
                set(it, i);
            }
        }
    }

    /**
     * Fill the inventory with item type array from a start index to an index and a given step
     * 
     * @param start The start indenx
     * @param end   The end index
     * @param step  The step
     * @param items The ItemStack array to fill with
     */
    public void fill(int start, int end, int step, ItemStack @NotNull [] items) {
        fill(start, end, step, items, true);
    }

    /**
     * Fill the inventory with item type array from a start index to an end index
     * 
     * @param start    The start index
     * @param end      The end index
     * @param items    The ItemStack array to fill with
     * @param override Override if an item is already present
     */
    public void fill(int start, int end, ItemStack @NotNull [] items, boolean override) {
        fill(start, end, 1, items);
    }

    /**
     * Fill the inventory with item type array from a start index to an end index
     *
     * @param start The start index
     * @param end   The end index
     * @param items The ItemStack array to fill with
     */
    public void fill(int start, int end, ItemStack[] items) {
        fill(start, end, items, false);
    }

    /**
     * Fill the inventory with item type array on slots contains in an array
     *
     * @param slots    The array of index to place
     * @param items    The ItemStack array to fill with
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
     * @param items The ItemStack array to fill with
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
     *
     * @param item     The item to fill with
     * @param override Override if an item is already present
     */
    public void fill(ItemStack item, boolean override) {
        fill(0, size, item);
    }

    /**
     * Fill the whole inventory of a given item
     *
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
     * Remove items from the inventory by a given array of ItemStack
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

        IntStream.range(0, size).filter(i -> getItem(i) == Arrays.stream(items).filter(is -> is == getItem(i)).findFirst().orElse(null)).forEach(slots::add);
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
     *
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
     * @return All the items contained in the inventory
     */
    public ItemStack[] getContent() {
        return inventory.getContents();
    }
}
