package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketKillauraEvent;
import funkemunky.Daedalus.packets.events.PacketPlayerType;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class KillAuraD extends Check {
	
	public static Map<UUID, Map.Entry<Double, Double>> packetTicks;
	public static Map<UUID, Long> time;
	
	public KillAuraD(Daedalus Daedalus) {
		super("KillAuraD", "KillAura (Packet)", Daedalus);
		
		this.setMaxViolations(5);
		this.setViolationResetTime(60000);
		
		this.packetTicks = new HashMap<UUID, Map.Entry<Double, Double>>();
		this.time = new HashMap<UUID, Long>();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void log(PlayerQuitEvent e) {
		if(this.packetTicks.containsKey(e.getPlayer().getUniqueId())) {
			this.packetTicks.remove(e.getPlayer().getUniqueId());
		}
		if(this.time.containsKey(e.getPlayer().getUniqueId())) {
			this.time.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void packet(PacketKillauraEvent e) {
		if(!getDaedalus().isEnabled()) {
			return;
		}
		if(e.getPlayer().hasPermission("daedalus.bypass")) {
			return;
		}
		double Count = 0;
		double Other = 0;
		long Time = System.currentTimeMillis();
		if(packetTicks.containsKey(e.getPlayer().getUniqueId())) {
			Count = packetTicks.get(e.getPlayer().getUniqueId()).getKey();
			Other = packetTicks.get(e.getPlayer().getUniqueId()).getValue();
		}
		if(time.containsKey(e.getPlayer().getUniqueId())) {
			Time = time.get(e.getPlayer().getUniqueId());
		}
		if(e.getType() == PacketPlayerType.ARM_SWING) {
			Other++;
		}
		
		if(e.getType() == PacketPlayerType.USE) {
			Count++;
		}
		
		if(UtilTime.elapsed(Time, 1000L)) {
			if(Count > Other) {
				getDaedalus().logCheat(this, e.getPlayer(), Count + " Use : " + Other + " Arm", Chance.HIGH, new String[] {"Experimental"});
			}
			Time = UtilTime.nowlong();
		}
		
		this.packetTicks.put(e.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<Double, Double>(Count, Other));
		this.time.put(e.getPlayer().getUniqueId(), Time);
	}

}
