package com.minecraftmarket.minecraftmarket.signs;

import com.minecraftmarket.minecraftmarket.Market;
import com.minecraftmarket.minecraftmarket.json.JSONException;
import com.minecraftmarket.minecraftmarket.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

	public Market plugin = Market.getPlugin();
	private Chat chat = Chat.get();
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getBlock().getType() == Material.SKULL){
			SignData.updateAllSigns();
		}
	}

	@EventHandler
	public void onSignChange(final SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[Recent]")) {

			if (!event.getPlayer().hasPermission("minecraftmarket.admin")) {
				event.getPlayer().sendMessage(ChatColor.DARK_RED + Chat.get().getLanguage().getString(("signs.no-permissions")));
				return;
			}

			int id = 0;

			try {
				id = Integer.parseInt(event.getLine(1));
			} catch (NumberFormatException ex) {
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(chat.prefix + ChatColor.DARK_RED + "Wrong sign format");
			}

			Location loc = event.getBlock().getLocation();
			SignData sign = SignData.getSignByLocation(loc);

			if (sign != null) {
				sign.remove();
			}

			SignData signData = null;


			signData = new SignData(loc, id - 1);

			try {
				signData.update();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			event.getPlayer().sendMessage(chat.prefix + ChatColor.GREEN + Chat.get().getLanguage().getString(("signs.created")));

		}

	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		SignData sign = SignData.getSignByLocation(event.getBlock().getLocation());
		if (sign != null) {
			if (event.getPlayer().hasPermission("signs.remove")) {
				sign.remove();
				event.getPlayer().sendMessage(chat.prefix + ChatColor.RED + "Sign removed");
			} else {
				event.getPlayer().sendMessage(ChatColor.DARK_RED + Chat.get().getLanguage().getString(("signs.no-permissions")));
			}
		}
	}


	
}
