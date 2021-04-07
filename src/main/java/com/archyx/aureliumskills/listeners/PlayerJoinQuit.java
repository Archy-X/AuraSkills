/*     */ package com.archyx.aureliumskills.listeners;
/*     */ 
/*     */ import com.archyx.aureliumskills.AureliumSkills;
/*     */ import com.archyx.aureliumskills.configuration.Option;
/*     */ import com.archyx.aureliumskills.configuration.OptionL;
/*     */ import com.archyx.aureliumskills.lang.Lang;
/*     */ import com.archyx.aureliumskills.skills.PlayerSkill;
/*     */ import com.archyx.aureliumskills.skills.PlayerSkillInstance;
/*     */ import com.archyx.aureliumskills.skills.SkillLoader;
/*     */ import com.archyx.aureliumskills.stats.PlayerStat;
/*     */ import com.archyx.aureliumskills.util.MySqlSupport;
/*     */ import com.archyx.aureliumskills.util.UpdateChecker;
/*     */ import dev.dbassett.skullcreator.SkullCreator;
          import java.util.concurrent.TimeUnit;
/*     */ import java.util.Locale;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockState;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ 
/*     */ public class PlayerJoinQuit
/*     */   implements Listener
/*     */ {
/*     */   private final AureliumSkills plugin;
/*     */   private MySqlSupport mySqlSupport;
/*     */   private SkillLoader skillLoader;
/*     */   
/*     */   public PlayerJoinQuit(AureliumSkills plugin) {
/*  37 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.LOWEST)
/*     */   public void onPlayerJoin(PlayerJoinEvent event) {
	
			  Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
				  Player player = event.getPlayer();
/*  43 */		  if (!SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
/*  44 */         SkillLoader.playerSkills.put(player.getUniqueId(), new PlayerSkill(player.getUniqueId(), player.getName(), this.plugin));
/*  45 */         this.plugin.getLeaderboard().queueAdd(new PlayerSkillInstance((PlayerSkill)SkillLoader.playerSkills.get(player.getUniqueId())));
/*     */       } else {
/*  47 */         ((PlayerSkill)SkillLoader.playerSkills.get(player.getUniqueId())).setPlayerName(player.getName());
/*     */       } 
/*  49 */       if (!SkillLoader.playerStats.containsKey(player.getUniqueId())) {
/*  50 */         SkillLoader.playerStats.put(player.getUniqueId(), new PlayerStat(player.getUniqueId(), this.plugin));
/*     */       }
/*     */      
/*  53 */       this.skillLoader = new SkillLoader(this.plugin);
/*  54 */       if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
/*     */       
/*  56 */         this.mySqlSupport = new MySqlSupport(this.plugin);
/*  57 */         (new BukkitRunnable() {
/*     */             public void run() {
/*  60 */               PlayerJoinQuit.this.mySqlSupport.init();
/*     */             }
/*  62 */         }).runTaskAsynchronously((Plugin)this.plugin);
/*     */       } else {
/*     */       
/*  65 */         this.skillLoader.loadSkillData();
/*  66 */         this.skillLoader.startSaving();
/*     */       } 
/*     */ 
/*     */     
/*  70 */       Location playerLoc = player.getLocation();
/*     */     
/*  72 */       Location loc = playerLoc.clone(); loc.setY(0.0D);
/*     */     
/*  74 */       Block b = loc.getBlock();
/*  75 */       BlockState state = b.getState();
/*  76 */       SkullCreator.blockWithUuid(b, player.getUniqueId());
/*  77 */       state.update(true);
/*     */     
/*  79 */       if (player.isOp() && 
/*  80 */         System.currentTimeMillis() > this.plugin.getReleaseTime() + 21600000L)
/*     */       {
/*  82 */         (new UpdateChecker((Plugin)this.plugin, 81069)).getVersion(version -> {
/*     */               if (!this.plugin.getDescription().getVersion().contains("Pre-Release") && !this.plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
/*     */                 Locale locale = Lang.getLanguage(player);
/*     */                 player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.WHITE + "New update available! You are on version " + ChatColor.AQUA + this.plugin.getDescription().getVersion() + ChatColor.WHITE + ", latest version is " + ChatColor.AQUA + version);
/*     */                 player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.WHITE + "Download it on Spigot: " + ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "http://spigotmc.org/resources/81069");
/*     */               } 
/*     */             });
/*     */       }
/*     */ 	  }, 20*10L);
/*     */ 	  
/*  42 */     
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onPlayerQuit(PlayerQuitEvent event) {
/*  97 */       Player player = event.getPlayer();
/*  98 */       this.plugin.getActionBar().resetActionBar(player);
/*  99 */       if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
/* 100 */           if (this.mySqlSupport != null) {
/* 101 */           this.mySqlSupport.saveData(false);
/*     */           } else {
/*     */
/* 105 */               Bukkit.getLogger().warning("MySql wasn't enabled on server startup, saving data to file instead! MySql will be enabled next time the server starts.");
/* 106 */               if (this.skillLoader != null) {
/* 107 */                   this.skillLoader.saveSkillData(false);
/*     */               }

/*     */           }
/*     */     
/*     */       }
/* 112 */       else if (this.skillLoader != null) {
/* 113 */           this.skillLoader.saveSkillData(false);
/*     */       }
/*     */    }
/*     */ }


/* Location:              C:\Users\Louis\Desktop\AntikCube\Servs\Spawn\plugins\AureliumSkills-Alpha1.6.8.jar!\com\archyx\aureliumskills\listeners\PlayerJoinQuit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */