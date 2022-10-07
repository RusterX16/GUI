# GUI
  
GUI is a tiny API for Bukkit/Spigot/Paper that helps you to build and manage Bukkit inventory way much easier.
  
## Build GUI
  
Old way :

```java
Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Inventory Title");
```
  
New way :

```java
GUI gui = new GUI("GUI Title", 3);
// or when you need an owner
GUI gui2 = new GUI("Owned GUI Title", 4, owner);
```
  
Notice that you no longer need to calculate the number of slots. GUI takes this number of row only between 1 and 6 included.
Like the Bukkit one, the inventory owner isn't needed.
  
## Add and remove items
  
Bukkit Way :

```java
ItemStack sword = new ItemStack(Material.IRON_SWORD);

inventory.addItem(sword);
inventory.remove(sword);
inventory.setItem(3, sword);

// Multiple set
int[] slots = new int[]{4, 5, 7, 8};

for (int i : slots) {
  inventory.setItem(i, new ItemStack(Material.GOLD_INGOT));
}
```
  
GUI way :

```java
ItemStack sword = new ItemStack(Material.IRON_SWORD);

gui.add(sword);
gui.remove(sword);
gui.set(sword, 3);
gui.set(new ItemStack(Material.GOLD_INGOT), 4, 5, 7, 8);
```
  
## Fill inventory
  
Bukkit way :

```java
for (int i = 0; i < 9; i++) {
  inventory.setItem(i, new ItemStack(Material.DIAMOND));
}
```
  
GUI way :

```java
gui.fill(1, 9, new ItemStack(Material.DIAMOND));
```
  
There is more than 10 overloaded fill methods, do not hesitate to search for the most suitable one for what you need.

## Horizontal and Vertical fills

If you need to fill automatiquely an item inline, there is some existing methods to do it in GUI API.
  
Fill the first row of the GUI of red stained glass panes.

```java
gui.horizontalFill(0, new ItemStack(Material.RED_STAINED_GLASS_PANE));
```
  
Fill the last column of the GUI of yellow stained glass pane. Does not replace item if one's already present

```java
gui.verticalFill(8, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), false);
```
  
Notice that both of these methods overrides existing items by default.
  
## Using of previous and next inventories
  
Depending of what you build, maybe you figured about navigation inside inventories. GUI API manages to it, here's a way :

```java
GUI first = new GUI("First GUI", 3);
GUI second = new GUI("Second GUI", 1);
first.setNextGUI(second);

first.getNextGUI().open(player);
```
  
Previous GUIs are basically the same but in reverse order.
  
## Some methods to complete
  
Get an item from GUI :

```java
int slot = 0;
ItemStack item = gui.get(slot);
```
  
Retrivered slots from an ItemStack :

```java
int firstSlot = gui.slot(item);
Set<Integer> allSlots = gui.slots(item);
```
  
Verify if slot is free (doesn't contains not null item) :

```java
int slot = 8;
boolean isSlotEmpty = gui.isEmpty(slot);
// gui.isEmpty() check the entire inventory
```
  
Open the GUI to a player :

```java
// You can still do :
player.openInventory(gui.getInventory());
// But a there's a shorter way :
gui.open(player);
// If you want to open the gui to his owner, you can do :
gui.open();
// Will throw an exception if owner is null
```
