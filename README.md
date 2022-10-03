# GUI

GUI is a tiny API that will help you to create and manage Bukkit inventory way much easier than Bukkit offers.  
This API is completely free, you're able to use it in your own project and contribute to it if you've any improvement ideas.  
Make sure to star this repository and let me know of any issues in there.
  
### Implements GUI in your Java Project  
Maven :
```
<dependencies>
    <dependency>
        <groupId>dev.ruster</groupId>
        <artifactId>GUI</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```
  
Gradle :
```
implementation group: "dev.ruster" name: "GUI" version: "1.0"
```

### Create a Inventory using GUI
  
Instead of doing this :
```
Inventory inventory = Bukkit.createInventory(null, 9*6, "Title");
```
  
You'll be able to do this :
```
GUI gui = new GUI("Title", 6);
```
  
### Set items in GUI
  
Bukkit version :
```
inventory.setItem(0, new ItemStack(Material.IRON_SWORD));

for (int i = 9; i < inventory.getSize(); i++) {
    inventory.setItem(i, new ItemStack(Material.GOLDEN_APPLE));
}
```
  
GUI version :
```
gui.set(new ItemStack(Material.IRON_SWORD), 0);
gui.fill(9, gui.getSize(), new ItemStack(Material.GOLDEN_APPLE));
```
  
### Recovering GUI from Bukkit Inventory
  
Use this static method to edit the inventory easier. Will return null if doesn't exists
```
GUI gui = GUI.getFromInventory(inventory);
```
