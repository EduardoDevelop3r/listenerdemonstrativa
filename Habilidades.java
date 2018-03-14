package dsv.eduardodeveloper.listener;

import dsv.eduardodeveloper.manager.Manager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@SuppressWarnings("deprecation")
public class Habilidades implements Listener {
	@EventHandler
	private void habThor(PlayerInteractEvent evento) {
		if ((Manager.comKit(evento.getPlayer(), "thor"))
				&& (evento.getPlayer().getItemInHand().getType() == Material.WOOD_AXE)) {
			if (Manager.comCooldown(evento.getPlayer(), "thor")) {
				evento.getPlayer().sendMessage("§cVocê está em cooldown.");
			} else {
				Manager.setarCooldown(evento.getPlayer(), "thor", 6);
				Location loc = evento.getPlayer().getTargetBlock(null, 20).getLocation();
				evento.getPlayer().getWorld().strikeLightning(loc);
			}
		}
	}
}
