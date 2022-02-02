package dev.ruster.gui;

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
     * Create a GUI inventory for a player with a previous GUI and a next GUI
     *
     * @param name        The name displayed at the top of the inventory
     * @param rows        The number of row of the inventory : between 1 and 6
     * @param owner       The owner of the inventory
     * @param previousGUI The GUI that come before this GUI
     * @param nextGUI     The GUI that come after this GUI
     */
    @SuppressWarnings("deprecation")
    public GUI(String name, int rows, Player owner, GUI previousGUI, GUI nextGUI) {
        if(rows < 0 || rows > 6) {
            throw new IllegalArgumentException("GUI must contain between 1 and 6 rows");
        }
        this.name = name;
        this.size = rows * 9;
        this.owner = owner;
        this.previousGUI = previousGUI;
        this.nextGUI = nextGUI;
        inventory = Bukkit.createInventory(owner, size, name);
    }

    /**
     * Create a new GUI inventory with a previous GUI and a next GUI
     *
     * @param name        The name displayed at the top of the inventory
     * @param rows        The number of row of the inventory : between 1 and 6
     * @param previousGUI The GUI that come before this GUI
     * @param nextGUI     The GUI that come after this GUI
     */
    public GUI(String name, int rows, GUI previousGUI, GUI nextGUI) {
        this(name, rows, null, previousGUI, nextGUI);
    }

    /**
     * Create a new GUI inventory for a player with a previous GUI
     *
     * @param name        The name displayed at the top of the inventory
     * @param rows        The number of row of the inventory : between 1 and 6
     * @param owner       The owner of the inventory
     * @param previousGUI The GUI that come before this GUI
     */
    public GUI(String name, int rows, Player owner, GUI previousGUI) {
        this(name, rows, owner, previousGUI, null);
    }

    /**
     * Create a new GUI inventory for a player
     *
     * @param name  The name displayed at the top of the inventory
     * @param rows  The number of row of the inventory : between 1 and 6
     * @param owner The owner of the inventory
     */
    public GUI(String name, int rows, Player owner) {
        this(name, rows, owner, null);
    }

    /**
     * Create a new GUI inventory
     *
     * @param name        The name displayed at the top of the inventory
     * @param rows        The number of row of the inventory : between 1 and 6
     * @param previousGUI The GUI that come before this GUI
     */
    public GUI(String name, int rows, GUI previousGUI) {
        this(name, rows, previousGUI, null);
    }

    /**
     * Create a new GUI inventory
     *
     * @param name The name displayed at the top of the inventory
     * @param rows The number of rows of the inventory : between 1 and 6
     */
    public GUI(String name, int rows) {
        this(name, rows, (Player) null);
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
            throw new IllegalArgumentException("Use remove method to remove an item from inventory");
        }
        for(int i : slots) {
            if(i < 0 || i > size) {
                throw new ArrayIndexOutOfBoundsException("slot is out of gui bounds");
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

    public void fill(int start, int end, ItemStack[] items, boolean override) {
        if(!override) {
            return;
        }
        if(start < 0 || start >= size || end >= size || end < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bound");
        }
        for(int i = Math.min(start, end); i < Math.max(start, end); i++) {
            for(ItemStack is : items) {
                set(is, i);
            }
        }
    }

    public void fill(int start, int end, ItemStack[] items) {
        fill(start, end, items, false);
    }

    public void fill(int @NotNull [] slots, ItemStack[] items, boolean override) {
        if(slots.length == 0 || !override) {
            return;
        }
        Arrays.stream(slots).forEach(i -> Arrays.stream(items).forEach(it -> set(it, i)));
    }

    public void fill(int @NotNull [] slots, ItemStack[] items) {
        fill(slots, items, false);
    }

    public void fill(int start, int end, ItemStack item, boolean override) {
        if(!override) {
            return;
        }
        fill(start, end, new ItemStack[]{item});
    }

    public void fill(int start, int end, ItemStack item) {
        fill(start, end, new ItemStack[]{item}, false);
    }

    public void fill(int @NotNull [] slots, ItemStack item, boolean override) {
        if(!override) {
            return;
        }
        fill(slots, new ItemStack[]{item});
    }

    public void fill(int @NotNull [] slots, ItemStack item) {
        fill(slots, new ItemStack[]{item}, false);
    }

    /**
     * Place items in a straight horizontal line of the GUI
     *
     * @param row      The row where to place the items
     * @param item     The item to place 6 times
     * @param override Replace existing items if not null
     */
    public void horizontalFill(int row, ItemStack item, boolean override) {
        if(row < 0 || row > 5) {
            throw new IllegalArgumentException("Row must be between 0 and 5");
        }
        fill(row * 9, row * 9 + 9, item, override);
    }

    /**
     * Place items in a straight horizontal line of the GUI
     *
     * @param row  The row where to place the items
     * @param item The items to place 6 times
     */
    public void horizontalFill(int row, ItemStack item) {
        horizontalFill(row, item, false);
    }

    /**
     * Place items in a straight vertical line of the GUI
     *
     * @param column   The column where to place the items
     * @param item     The item to place 8 times
     * @param override Replace existing items if not null
     */
    public void verticalFill(int column, ItemStack item, boolean override) {
        if(column < 0 || column > 7) {
            throw new IllegalArgumentException("Column must be between 0 and 7");
        }
        for(int i = column; i < 6; i += 9) {
            if(isEmpty(i) || override) {
                set(item, i);
            }
        }
    }

    /**
     * Place items in a straight vertical line of the GUI
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
     * Get the slots where an item is present.
     * Will be empty if your item is not present
     *
     * @param item The item you want to get slots at
     * @return A set of the slots where the item is placed
     */
    public Set<Integer> getSlots(ItemStack item) {
        Set<Integer> slots = new HashSet<>();

        IntStream.range(0, size).filter(i -> item == getItem(i)).forEach(slots::add);
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
    public void openNextGUI(Player player) {
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
     * Check if the GUI contains no items
     *
     * @return Empty or not
     */
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    /**
     * Check if a slot is empty or not
     *
     * @param slot The slot where to check
     * @return The slot is empty or not
     */
    public boolean isEmpty(int slot) {
        ItemStack item = getItem(slot);
        return item == null || item.getType() == Material.AIR;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setPreviousGUI(GUI previousGUI) {
        this.previousGUI = previousGUI;
    }

    public GUI getPreviousGUI() {
        return previousGUI;
    }

    public void setNextGUI(GUI nextGUI) {
        this.nextGUI = nextGUI;
    }

    public GUI getNextGUI() {
        return nextGUI;
    }
}
