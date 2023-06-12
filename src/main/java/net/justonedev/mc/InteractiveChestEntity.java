package net.justonedev.mc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class InteractiveChestEntity {

    public static String spawnChest(Player p)
    {

        Location l = p.getLocation();
        Location loc = new Location(l.getWorld(), l.getBlockX() + 0.5, l.getBlockY() - 1, l.getBlockZ() + 0.5, 0.15f, 0.5f);
        ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

        String p_name = p.getName() + "'";
        if(!p_name.toLowerCase().endsWith("s'")) p_name += "s";

        // properties
        stand.setVisible(false);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setGravity(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName("§8" + p_name + " Inventory");
        if(stand.getEquipment() != null) stand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
        stand.setCanPickupItems(false);
        stand.setCollidable(false);
        stand.setPersistent(true);
        stand.setVisualFire(false);
        stand.setInvulnerable(true);
    
        EulerAngle angle = new EulerAngle(135, 0, 0);
        stand.setBodyPose(angle);
        stand.setLeftLegPose(angle);
        stand.setRightLegPose(angle);

        // inv locks
        for(EquipmentSlot slot : EquipmentSlot.values())
        {
            stand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
            stand.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING); // pretty sure we dont need this
        }

        return stand.getUniqueId().toString();
    }

}
