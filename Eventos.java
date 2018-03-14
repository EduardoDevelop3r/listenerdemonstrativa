package dsv.eduardodeveloper.listener;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import me.netindev.Main;
import me.netindev.manager.Manager;
import me.netindev.timer.Iniciando;
import me.netindev.timer.Invencibilidade;
import me.netindev.timer.Jogo;
import me.netindev.utils.Estado;
import me.netindev.utils.Seletor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Eventos implements Listener {
	@EventHandler
	private void aoBotar(BlockPlaceEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Manager.espectadores.contains(evento.getPlayer().getName()))) {
			evento.setCancelled(true);
		}
	}

	@EventHandler
	private void aoQuebrar(BlockBreakEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Manager.espectadores.contains(evento.getPlayer().getName()))) {
			evento.setCancelled(true);
		}
	}

	@EventHandler
	private void aoDropar(PlayerDropItemEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Manager.espectadores.contains(evento.getPlayer().getName()))) {
			evento.setCancelled(true);
		}
	}

	@EventHandler
	private void aoItem(ItemSpawnEvent evento) {
		if (Main.estado == Estado.INICIANDO) {
			evento.setCancelled(true);
		}
	}
	
  @EventHandler
	private void aoXP(PlayerExpChangeEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Manager.espectadores.contains(evento.getPlayer().getName()))) {
			evento.setAmount(0);
		}
	}

	@EventHandler
	private void aoFome(FoodLevelChangeEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Main.estado == Estado.INVENCIBILIDADE)) {
			evento.setFoodLevel(20);
		}
	}

	@EventHandler
	private void aoDano(EntityDamageEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Main.estado == Estado.INVENCIBILIDADE) || (Manager.ganhou)) {
			evento.setCancelled(true);
		}
		if (((evento.getEntity() instanceof Player))
				&& (Manager.espectadores.contains(((Player) evento.getEntity()).getName()))) {
			evento.setCancelled(true);
		}
	}

	@EventHandler
	private void aoSpawn(CreatureSpawnEvent evento) {
		if ((Main.estado == Estado.INICIANDO) || (Main.estado == Estado.INVENCIBILIDADE)) {
			evento.setCancelled(true);
		}
	}

	private ArrayList<String> kangaroo = new ArrayList<String>();

	@EventHandler
	private void aoInteragir(PlayerInteractEvent evento) {
		if (Main.estado == Estado.INICIANDO) {
			if (evento.getPlayer().getItemInHand().getType() == Material.CHEST) {
				Seletor.seletorKits(evento.getPlayer());
			}
			evento.setCancelled(true);
		}
		if ((Main.estado == Estado.JOGO) && (Manager.espectadores.contains(evento.getPlayer().getName()))
				&& (evento.getPlayer().getItemInHand().getType() == Material.BOOK)) {
			Inventory inv = Bukkit.createInventory(null, 54, "§2Jogadores:");
			for (Player jogadores : Bukkit.getOnlinePlayers()) {
				if (Manager.jogadores.contains(jogadores.getName())) {
					ItemStack stack = new ItemStack(Material.SKULL_ITEM);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName("§7" + jogadores.getName());
					stack.setItemMeta(meta);
					inv.addItem(stack);
				}
			}
			evento.getPlayer().openInventory(inv);
		}
		if (evento.getPlayer().getItemInHand().getType() == Material.COMPASS) {
			boolean parar = false;
			for (Entity entidades : evento.getPlayer().getNearbyEntities(500.0D, 500.0D, 500.0D)) {
				if (((entidades instanceof Player))
						&& (evento.getPlayer().getLocation().distance(entidades.getLocation()) >= 30.0D)) {
					parar = true;
					evento.getPlayer().setCompassTarget(entidades.getLocation());
					evento.getPlayer()
							.sendMessage("§aApontando para o player: " + ((Player) entidades).getName() + ".");
					break;
				}
			}
			if (!parar) {
				evento.getPlayer().sendMessage("§cUm player não foi localizado, apontando para o spawn.");
				evento.getPlayer().setCompassTarget(new Location(Bukkit.getWorld("world"), 0.0D,
						Bukkit.getWorld("world").getHighestBlockYAt(0, 0), 0.0D));
				return;
			}
		}
		if ((Main.estado == Estado.INICIANDO) && (evento.getPlayer().getItemInHand().getType() == Material.FIREWORK)) {
			evento.setCancelled(true);
			if (!this.kangaroo.contains(evento.getPlayer().getName())) {
				this.kangaroo.add(evento.getPlayer().getName());
				Vector vec = evento.getPlayer().getEyeLocation().getDirection();
				if (evento.getPlayer().isSneaking()) {
					vec.multiply(1.8D);
					vec.setY(0.9D);
				} else {
					vec.multiply(1.2D);
					vec.setY(1.1D);
				}
				evento.getPlayer().setVelocity(vec);
			}
		} else if ((Manager.comKit(evento.getPlayer(), "kangaroo"))
				&& (evento.getPlayer().getItemInHand().getType() == Material.FIREWORK)) {
			evento.setCancelled(true);
			if (!this.kangaroo.contains(evento.getPlayer().getName())) {
				this.kangaroo.add(evento.getPlayer().getName());
				Vector vec = evento.getPlayer().getEyeLocation().getDirection();
				if (evento.getPlayer().isSneaking()) {
					vec.multiply(1.8D);
					vec.setY(0.9D);
				} else {
					vec.multiply(1.2D);
					vec.setY(1.1D);
				}
				evento.getPlayer().setVelocity(vec);
			}
		}
	}

	@EventHandler
	private void aoMapa(MapInitializeEvent evento) {
		evento.getMap().addRenderer(new MapRenderer() {
			public void render(MapView mapa, MapCanvas canvas, Player jogador) {
				canvas.drawText(30, 10, MinecraftFont.Font, "eduardodeveloper");
				canvas.drawText(30, 20, MinecraftFont.Font, "Voce ganhou:");
				canvas.drawText(30, 30, MinecraftFont.Font, jogador.getName());
				canvas.drawImage(15, 42,
						new ImageIcon(getClass().getResource("/dsv/eduardodeveloper2/resources/bolo.png")).getImage());
			}
		});
	}

	@EventHandler
	private void aoMover(PlayerMoveEvent evento) {
		if ((this.kangaroo.contains(evento.getPlayer().getName())) && (evento.getPlayer().getLocation().getBlock()
				.getRelative(BlockFace.DOWN).getType() != Material.AIR)) {
			this.kangaroo.remove(evento.getPlayer().getName());
		}
		if ((Main.estado == Estado.INICIANDO) || (Main.estado == Estado.INVENCIBILIDADE)
				|| (Manager.espectadores.contains(evento.getPlayer().getName()))) {
			if ((evento.getPlayer().getLocation().getBlockX() > 500)
					|| (evento.getPlayer().getLocation().getBlockX() < -500)
					|| (evento.getPlayer().getLocation().getBlockZ() > 500)
					|| (evento.getPlayer().getLocation().getBlockZ() < -500)) {
				int x = new Random().nextInt(450);
				int z = new Random().nextInt(450);
				int y = evento.getPlayer().getWorld().getHighestBlockYAt(x, z) + 5;
				evento.getPlayer().teleport(new Location(evento.getPlayer().getWorld(), x, y, z));
				return;
			}
			return;
		}		if (Main.estado == Estado.JOGO) {
			if ((evento.getPlayer().getLocation().getBlockX() > 500)
					|| (evento.getPlayer().getLocation().getBlockX() < -500)
					|| (evento.getPlayer().getLocation().getBlockZ() > 500)
					|| (evento.getPlayer().getLocation().getBlockZ() < -500)) {
				evento.getPlayer().getWorld().strikeLightning(evento.getPlayer().getLocation());
				evento.getPlayer().setFireTicks(60);
				return;
			}
			return;
		}
	}

	@EventHandler
	private void aoInventario(InventoryClickEvent evento) {
		Player jogador = (Player) evento.getWhoClicked();
		if ((evento.getInventory() != null) && (evento.getCurrentItem() != null)
				&& (evento.getCurrentItem().getType() != null) && (evento.getCurrentItem().getType() != Material.AIR)) {
			if (evento.getInventory().getTitle().equals("§2Seletor de Kits:")) {
				evento.setCancelled(true);
				if ((evento.getCurrentItem().getType() == Material.ENDER_PEARL)
						|| (evento.getCurrentItem().getType() == Material.THIN_GLASS)) {
					return;
				}
				String kit = evento.getCurrentItem().getItemMeta().getDisplayName().replace("§a", "");
				jogador.performCommand("kit " + kit);
				jogador.closeInventory();
			} else if (evento.getInventory().getTitle().equals("§2Jogadores:")) {
				evento.setCancelled(true);
				String item = evento.getCurrentItem().getItemMeta().getDisplayName().replace("§7", "");
				for (Player jogadores : Bukkit.getOnlinePlayers()) {
					if (item.equalsIgnoreCase(jogadores.getName())) {
						jogador.teleport(jogadores);
						jogador.sendMessage("§aVocê se teleportou para: " + jogadores.getName() + ".");
						return;
					}
				}
			}
		}
	}

	@EventHandler
	private void aoMOTD(ServerListPingEvent evento) {
		if (Main.estado == Estado.INICIANDO) {
			evento.setMotd("§aIniciando partida em: " + Manager.stringTimer(Iniciando.tempo) + ".");
			return;
		}
		if (Main.estado == Estado.INVENCIBILIDADE) {
			evento.setMotd("§cEncerrando invencibilidade em: " + Manager.stringTimer(Invencibilidade.tempo) + ".");
			evento.setMaxPlayers(Bukkit.getOnlinePlayers().length);
			return;
		}
		if (Main.estado == Estado.JOGO) {
			evento.setMotd("§cTorneio em andamento, já se passaram: " + Manager.stringTimer(Jogo.passou)
					+ ".\nJogadores espectando: " + Manager.espectadores.size() + ".");
			evento.setMaxPlayers(Bukkit.getOnlinePlayers().length);
			return;
		}
	}

	@EventHandler
	private void aoLogar(PlayerLoginEvent evento) {
		if (Main.estado == Estado.INVENCIBILIDADE) {
			if ((evento.getPlayer().hasPermission("tutorialhg.entrar"))
					|| (Manager.jogadores.contains(evento.getPlayer().getName()))) {
				evento.allow();
			} else {
				evento.disallow(PlayerLoginEvent.Result.KICK_OTHER,
						"§cCompre vip para entrar, no site: eduardodeveloper.com.br.");
			}
		} else if (Main.estado == Estado.JOGO) {
			if ((evento.getPlayer().hasPermission("tutorialhg.entrar"))
					|| (Manager.jogadores.contains(evento.getPlayer().getName()))) {
				evento.allow();
				if (Jogo.tempo <= 3300) {
					Manager.espectadores.add(evento.getPlayer().getName());
				}
			} else if (Jogo.tempo <= 3300) {
				evento.disallow(PlayerLoginEvent.Result.KICK_OTHER,
						"§cCompre vip para espectar, no site: eduardodeveloper.com.br.");
			} else {
				evento.disallow(PlayerLoginEvent.Result.KICK_OTHER,
						"§cCompre vip para entrar, no site: eduardodeveloper.com.br.");
			}
		}
	}

	@EventHandler
	private void aoEntrar(PlayerJoinEvent evento) {
		evento.setJoinMessage(null);
		Player jogador = evento.getPlayer();
		if (Main.estado == Estado.INICIANDO) {
			Manager.setarKit(jogador, null, "Nenhum");
			Manager.jogadores.add(jogador.getName());
			jogador.getInventory().setItem(0, Seletor.criarItem(Material.CHEST, "§aSeletor de Kits"));
			jogador.getInventory().setItem(8, Seletor.criarItem(Material.FIREWORK, "§aKangaroo"));
		} else if (Main.estado == Estado.INVENCIBILIDADE) {
			if (!Manager.jogadores.contains(evento.getPlayer().getName())) {
				Manager.setarKit(jogador, null, "Nenhum");
				Manager.jogadores.add(evento.getPlayer().getName());
				int x = new Random().nextInt(450);
				int z = new Random().nextInt(450);
				int y = evento.getPlayer().getWorld().getHighestBlockYAt(x, z) + 5;
				evento.getPlayer().teleport(new Location(evento.getPlayer().getWorld(), x, y, z));
				jogador.getInventory().addItem(new ItemStack(Material.COMPASS));
			}
		} else if ((Main.estado == Estado.JOGO) && (!Manager.jogadores.contains(evento.getPlayer().getName()))) {
			evento.getPlayer().getInventory().clear();
			if (Jogo.tempo <= 3300) {
				Manager.espectadores.add(evento.getPlayer().getName());
				jogador.setAllowFlight(true);
				jogador.getInventory().addItem(new ItemStack(Material.BOOK));
			} else {
				Manager.setarKit(jogador, null, "Nenhum");
				Manager.jogadores.add(evento.getPlayer().getName());
				int x = new Random().nextInt(450);
				int z = new Random().nextInt(450);
				int y = evento.getPlayer().getWorld().getHighestBlockYAt(x, z) + 5;
				evento.getPlayer().teleport(new Location(evento.getPlayer().getWorld(), x, y, z));
				jogador.getInventory().addItem(new ItemStack(Material.COMPASS));
			}
		}
	}

	@EventHandler
	private void aoRenascer(PlayerRespawnEvent evento) {
		if (Manager.espectadores.contains(evento.getPlayer().getName())) {
			int x = new Random().nextInt(450);
			int z = new Random().nextInt(450);
			int y = evento.getPlayer().getWorld().getHighestBlockYAt(x, z) + 5;
			evento.setRespawnLocation(new Location(evento.getPlayer().getWorld(), x, y, z));
			evento.getPlayer().getInventory().addItem(new ItemStack(Material.BOOK));
		}
		if (Manager.jogadores.contains(evento.getPlayer().getName())) {
			int x = new Random().nextInt(450);
			int z = new Random().nextInt(450);
			int y = evento.getPlayer().getWorld().getHighestBlockYAt(x, z) + 5;
			evento.setRespawnLocation(new Location(evento.getPlayer().getWorld(), x, y, z));
			evento.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
			if (Manager.itens.containsKey(Manager.kit.get(evento.getPlayer().getName()))) {
				evento.getPlayer().getInventory()
						.addItem(Manager.itens.get(Manager.kit.get(evento.getPlayer().getName())));
			}
		}
	}

	@EventHandler
	private void aoMorrer(PlayerDeathEvent evento) {
		Player jogador = evento.getEntity();
		String morte = null;
		String kit = Manager.pegarKit(jogador);
		if (jogador.hasPermission("eduardodeveloper.entrar")) {
			if (Jogo.tempo < 3300) {
				Manager.jogadores.remove(jogador.getName());
				Manager.espectadores.add(jogador.getName());
				jogador.setAllowFlight(true);
			} else {
				jogador.sendMessage(
						"> Você possui apenas " + Manager.stringTimer(Jogo.tempo - 3300) + " para renascer.");
			}
		} else if (Manager.jogadores.contains(jogador.getName())) {
			Manager.jogadores.remove(jogador.getName());
			jogador.kickPlayer("§cVocê morreu.");
		}
		if ((jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") morreu de uma explosão.";
		}
		if (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.DROWNING) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") morreu afogado.";
		}
		if ((jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
				&& ((jogador.getLastDamageCause().getEntity() instanceof Player)) && (jogador.getKiller() != null)) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") morreu para " + jogador.getKiller().getName() + "("
					+ Manager.pegarKit(jogador.getKiller()) + ").";
		}
		if (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") caiu de um lugar alto e esqueceu os paraquedas.";
		}
		if ((jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FIRE)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LAVA)) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") morreu queimado.";
		}
		if (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") se matou.";
		}
		if (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.THORNS) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") morreu em um cacto.";
		}
		if (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") caiu no void.";
		}
		if ((jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CONTACT)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.MAGIC)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.MELTING)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.POISON)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.STARVATION)
				|| (jogador.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.WITHER)) {
			morte = "> §b" + jogador.getName() + "(" + kit + ") morreu.";
		}
		evento.setDeathMessage(morte);
	}

	@EventHandler
	private void aoSair(final PlayerQuitEvent evento) {
		evento.setQuitMessage(null);
		final Player jogador = evento.getPlayer();
		this.kangaroo.remove(jogador.getName());
		if (Manager.espectadores.contains(jogador.getName())) {
			Manager.espectadores.remove(jogador.getName());
		}
		if (Main.estado == Estado.INICIANDO) {
			Manager.jogadores.remove(jogador.getName());
		}
		if (Main.estado != Estado.INICIANDO) {
			new BukkitRunnable() {
				public void run() {
					if ((evento.getPlayer() == null) || (!evento.getPlayer().isOnline())) {
						Manager.jogadores.remove(jogador.getName());
					}
				}
			}.runTaskLater(Main.plugin, 400L);
		}
	}
}
