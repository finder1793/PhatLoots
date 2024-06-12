package com.codisimus.plugins.phatloots.listeners;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.PhatLootsConfig;
import com.codisimus.plugins.phatloots.loot.LootBundle;

/**
 * Listens for block break events for obtaining loot on breaking a block
 *
 * @author Redned
 */
public class BlockLootListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player.getGameMode() == GameMode.CREATIVE)
            return; // don't drop blocks if player is in creative mode

        // Used namespaced name here just to prevent possible compatibility issues
        PhatLoot phatLoot = PhatLoots.getPhatLoot("minecraft-" + block.getType().name().toLowerCase());
        if (phatLoot == null)
            return;

        int enchantBonus = 0;
        if (player.getInventory().getItemInMainHand() != null && PhatLootsConfig.blockLootEnchantBonus) {
            enchantBonus = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FORTUNE);
        }

        LootBundle bundle = phatLoot.rollForLoot(enchantBonus);
        event.setExpToDrop(bundle.getExp());
        event.setDropItems(false);
        bundle.getItemList().forEach(item -> player.getWorld().dropItemNaturally(block.getLocation(), item));
        bundle.getCommandList().forEach(command -> command.execute(player));
    }
}
