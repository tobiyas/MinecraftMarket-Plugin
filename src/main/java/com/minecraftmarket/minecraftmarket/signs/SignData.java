package com.minecraftmarket.minecraftmarket.signs;

import com.google.common.collect.Lists;
import com.minecraftmarket.minecraftmarket.Market;
import com.minecraftmarket.minecraftmarket.json.JSONException;
import com.minecraftmarket.minecraftmarket.util.Chat;
import com.minecraftmarket.minecraftmarket.util.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import java.util.List;

public class SignData {

	private static List<SignData> signs = Lists.newArrayList();
	private Location location;
	private Sign sign;
	private Integer number;
	private String date;
	private Block block;
	private String username;
	private String item; // Package name

	public SignData(Location location, int number) {
		this.location = location;
		this.block = location.getBlock();
		this.number = number;
		create();
	}

	public void create() {
		SignData signData = null;
		for (SignData sd : signs) {
			if (sd.getLocation() == this.location) {
				signData = sd;
			}
		}
		if (signData != null) {
			signData.remove();
		}

		signs.add(this);
		if (isSign()) {
			saveAllToConfig();
			this.sign = (Sign) block.getState();
			try {
				update();
			} catch (JSONException e) {
			}
		}
	}

	public void update() throws JSONException {
		try {
			this.username = Signs.getJsonArray().getJSONObject(number).getString("username");
			this.item = Signs.getJsonArray().getJSONObject(number).getString("item");
			this.date = Signs.getJsonArray().getJSONObject(number).getString("date");
			this.date = date.split(" ")[0];

            Bukkit.broadcastMessage("Found purchase of " + item + " from " + username + " on " + date);

			if (sign != null) {

				sign.setLine(0, ChatColor.UNDERLINE + getMsg("signs.header"));
				sign.setLine(1, username);
				sign.setLine(2, item);
				sign.setLine(3, date);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Market.getPlugin(), new Runnable() {
					public void run() {
						sign.update();
						//Just encase
						sign.update(true, true);
					}
				}, 20L);
				updateHead();
			}
		} catch (JSONException e) {

            Bukkit.broadcastMessage("Awaiting Purchase: " + Market.getAwaitingPurchase());

			sign.setLine(0, Market.getAwaitingPurchase());
			sign.setLine(1, "");
            sign.setLine(2, "");
            sign.setLine(3, "");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Market.getPlugin(), new Runnable() {
                public void run() {
                    sign.update();
                    //Just encase
                    sign.update(true, true);
                }
            }, 20L);
            Skull p = getSkull(block);
            p.setOwner(Market.getHeadName());
        }
    }

	public boolean isSign() {
		return this.block.getState() instanceof Sign;
	}

	public void updateHead() {
		Skull skull = getSkull(block);
		if (skull != null) {
			skull.setOwner(this.username);
			skull.update();
		}
	}

	public void remove() {
		signs.remove(this);
		saveAllToConfig();
	}

	public static void saveAllToConfig() {
		List<String> listSigns = Lists.newArrayList();
		for (SignData sd : signs) {
			Location loc = sd.getLocation();
			String str = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + sd.getNumber();
			listSigns.add(str);
		}
		Settings.get().getSignDatabase().set("recent", listSigns);
		Settings.get().saveSignDatabase();
	}

	public static void updateAllSigns() {
		try {
			List<SignData> toRemove = Lists.newArrayList();
			for (SignData sd : signs) {
				//if(sd.getLocation().getBlock().getType() == Material.SIGN || sd.getLocation().getBlock().getType() == Material.WALL_SIGN){
				if (sd.isSign()) {
					try {
						sd.update();
					} catch (JSONException e) {
					}
				} else {
					toRemove.add(sd);
				}
				/*}else{
					Bukkit.broadcastMessage(Chat.get().prefix + "Found invalid sign! Removing!");
					sd.getLocation().getBlock().setType(Material.AIR);
					toRemove.add(sd);
				}*/
			}
			for (SignData sd : toRemove) {
				sd.remove();
			}
		} catch (NullPointerException e) {
		}
	}

	public static SignData getSignByLocation(Location location) {
		for (SignData sd : signs) {
			if (location.getBlockX() == sd.getLocation().getBlockX() && location.getBlockY() == sd.getLocation().getBlockY() && location.getBlockZ() == sd.getLocation().getBlockZ()) {
				return sd;
			}
		}
		return null;
	}

	public Location getLocation() {
		return this.location;
	}

	public Integer getNumber() {
		return this.number;
	}

	private Skull getSkull(Block block) {
		Block b = block.getRelative(BlockFace.UP);
		for (BlockFace face : Signs.BLOCKFACES) {
			Block s = b.getRelative(face);
			if ((s.getState() instanceof Skull)) {
				return (Skull) s.getState();
			}
		}
		return null;
	}

	private String getMsg(String string) {
		return Chat.get().getLanguage().getString(string);
	}

}
