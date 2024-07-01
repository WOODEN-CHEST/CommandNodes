package sus.keiger.plugincommon.player;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import sus.keiger.plugincommon.entity.EntityFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public final class PlayerFunctions
{
    // Static fields.
    /* Inventory slots, figured out through trial and error because online sources are wrong. */
    public static final int SLOT_ARMOR_HEAD = 39;
    public static final int SLOT_ARMOR_CHEST = 38;
    public static final int SLOT_ARMOR_LEGS = 37;
    public static final int SLOT_ARMOR_FEET = 36;

    public static final int SLOT_ROW1_COLUMN1 = 9;
    public static final int SLOT_ROW1_COLUMN2 = 10;
    public static final int SLOT_ROW1_COLUMN3 = 11;
    public static final int SLOT_ROW1_COLUMN4 = 12;
    public static final int SLOT_ROW1_COLUMN5 = 13;
    public static final int SLOT_ROW1_COLUMN6 = 14;
    public static final int SLOT_ROW1_COLUMN7 = 15;
    public static final int SLOT_ROW1_COLUMN8 = 16;
    public static final int SLOT_ROW1_COLUMN9 = 17;
    public static final int SLOT_ROW2_COLUMN1 = 18;
    public static final int SLOT_ROW2_COLUMN2 = 19;
    public static final int SLOT_ROW2_COLUMN3 = 20;
    public static final int SLOT_ROW2_COLUMN4 = 21;
    public static final int SLOT_ROW2_COLUMN5 = 22;
    public static final int SLOT_ROW2_COLUMN6 = 23;
    public static final int SLOT_ROW2_COLUMN7 = 24;
    public static final int SLOT_ROW2_COLUMN8 = 25;
    public static final int SLOT_ROW2_COLUMN9 = 26;
    public static final int SLOT_ROW3_COLUMN1 = 27;
    public static final int SLOT_ROW3_COLUMN2 = 28;
    public static final int SLOT_ROW3_COLUMN3 = 29;
    public static final int SLOT_ROW3_COLUMN4 = 30;
    public static final int SLOT_ROW3_COLUMN5 = 31;
    public static final int SLOT_ROW3_COLUMN6 = 32;
    public static final int SLOT_ROW3_COLUMN7 = 33;
    public static final int SLOT_ROW3_COLUMN8 = 34;
    public static final int SLOT_ROW3_COLUMN9 = 35;

    public static final int SLOT_HOTBAR1 = 0;
    public static final int SLOT_HOTBAR2 = 1;
    public static final int SLOT_HOTBAR3 = 2;
    public static final int SLOT_HOTBAR4 = 3;
    public static final int SLOT_HOTBAR5 = 4;
    public static final int SLOT_HOTBAR6 = 5;
    public static final int SLOT_HOTBAR7 = 6;
    public static final int SLOT_HOTBAR8 = 7;
    public static final int SLOT_HOTBAR9 = 8;

    public static final int SLOT_OFFHAND = 40;

    public static final int SLOT_CURSOR = -1;

    public static final int CRAFTING_SLOT_RESULT = 0;
    public static final int CRAFTING_SLOT_R1C1 = 1;
    public static final int CRAFTING_SLOT_R1C2 = 2;
    public static final int CRAFTING_SLOT_R2C1 = 3;
    public static final int CRAFTING_SLOT_R2C2 = 4;


    // Constructors.
    private PlayerFunctions()
    {
    }


    // Static functions.
    /* Sound. */
    public static void PlayErrorSound(Player mcPlayer, boolean isPrivate)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }

        float Volume = 0.8f;
        float Pitch = 0.8f;
        SoundCategory Category = SoundCategory.PLAYERS;
        Location TargetLocation = mcPlayer.getLocation();
        Sound TargetSound = Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;

        if (isPrivate)
        {
            mcPlayer.playSound(TargetLocation, TargetSound, Category, Volume, Pitch);
        } else
        {
            mcPlayer.getWorld().playSound(TargetLocation, TargetSound, Category, Volume, Pitch);
        }
    }


    /* Attacks. */
    public static boolean IsAttackCritical(Player mcPlayer)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }

        return (mcPlayer.getFallDistance() > 0f) && !mcPlayer.isSprinting()
                && (mcPlayer.getAttackCooldown() >= 0.9f);
    }


    /* Inventory. */
    public static boolean IsItemEmpty(ItemStack item)
    {
        return (item == null) || (item.equals(ItemStack.empty()));
    }

    public static ItemStack ItemOrNull(ItemStack item)
    {
        return IsItemEmpty(item) ? null : item;
    }

    public static ItemSearchResult FindItems(Player mcPlayer, Predicate<ItemStack> predicate)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }
        List<ItemStack> FoundItems = new ArrayList<>();
        List<Integer> FoundItemSlots = new ArrayList<>();

        for (int i = SLOT_CURSOR; i <= SLOT_OFFHAND; i++)
        {
            ItemStack TargetItem = GetItem(mcPlayer, i);
            if ((TargetItem != null) && predicate.test(TargetItem))
            {
                FoundItems.add(TargetItem);
                FoundItemSlots.add(i);
            }
        }
        return new ItemSearchResult(FoundItems, FoundItemSlots);
    }

    public static boolean ReplaceOrSetItems(Player mcPlayer,
                                            int maxItemCount,
                                            ItemStack item,
                                            int slot,
                                            Predicate<ItemStack> itemSearchPredicate)
    {
        ItemSearchResult SearchResult = FindItems(mcPlayer, itemSearchPredicate);

        if (SearchResult.GetItemCount() > maxItemCount)
        {
            return false;
        }
        if (SearchResult.IsEmpty())
        {
            return TrySetOrAddItem(item, slot, mcPlayer);
        }

        for (Integer Slot : SearchResult.GetSlots())
        {
            SetItem(mcPlayer, Slot, item);
        }
        return true;
    }

    public static void SetItem(Player mcPlayer, int slot, ItemStack item)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }

        if (slot == SLOT_CURSOR)
        {
            mcPlayer.setItemOnCursor(item);
        }
        else
        {
            mcPlayer.getInventory().setItem(slot, item);
        }
    }

    public static ItemStack GetItem(Player mcPlayer, int slot)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }

        ItemStack FoundItem;

        if (slot == SLOT_CURSOR)
        {
            FoundItem = mcPlayer.getItemOnCursor();
            return FoundItem.equals(ItemStack.empty()) ? null : FoundItem;
        }

        return ItemOrNull(mcPlayer.getInventory().getItem(slot));
    }

    public static HashMap<Integer, ItemStack> AddItem(Player mcPlayer, ItemStack ... items)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }
        return mcPlayer.getInventory().addItem(items);
    }

    public static int RemoveItems(Player mcPlayer, Predicate<ItemStack> predicate)
    {
        ItemSearchResult SearchResult = FindItems(mcPlayer, predicate);

        for (int Slot : SearchResult.GetSlots())
        {
            SetItem(mcPlayer, Slot, null);
        }

        return SearchResult.GetItemCount();
    }

    public static void ClearInventory(Player mcPlayer)
    {
        if (mcPlayer == null)
        {
            throw new IllegalArgumentException("mcPlayer is null");
        }
        mcPlayer.getInventory().clear();
        mcPlayer.setItemOnCursor(ItemStack.empty());
    }


    /* Attributes. */
    public static void ResetAttributes(Player mcPlayer)
    {
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_MAX_HEALTH, 20d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_MOVEMENT_SPEED, 0.1d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_LUCK, 0d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_ATTACK_DAMAGE, 1d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_ATTACK_SPEED, 4d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER, 1d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_GRAVITY, 0.08d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_SCALE, 1d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_SAFE_FALL_DISTANCE, 3d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_ARMOR, 0d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_ARMOR_TOUGHNESS, 0d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.PLAYER_BLOCK_BREAK_SPEED, 1d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 3d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.PLAYER_BLOCK_INTERACTION_RANGE, 4.5d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_STEP_HEIGHT, 0.6d);
        EntityFunctions.ResetAttribute(mcPlayer, Attribute.GENERIC_JUMP_STRENGTH, 0.42d);
    }


    // Private static methods.
    private static boolean TrySetOrAddItem(ItemStack item, int slot, Player mcPlayer)
    {
        if (GetItem(mcPlayer, slot) != null)
        {
            return mcPlayer.getInventory().addItem(item).isEmpty();
        }

        SetItem(mcPlayer, slot, item);
        return true;
    }
}