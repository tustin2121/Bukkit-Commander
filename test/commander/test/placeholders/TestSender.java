package commander.test.placeholders;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class TestSender implements CommandSender {
	private Server server;
	
	public TestSender(Server server) {
		this.server = server;
	}
	
	/////////////////////////////////// Relevant Methods //////////////////////////////////////
	
	@Override public void sendMessage(String message) {
		System.out.println("[TestSender] sendMessage() : "+message);
	}

	@Override public void sendMessage(String[] messages) {
		System.out.println("[TestSender] sendMessage() :");
		for (String msg : messages) {
			System.out.println(" > "+msg);
		}
	}
	
	@Override public Server getServer() {
		return server;
	}

	@Override public String getName() {
		return "TestSender";
	}
	
	////////////////////////////////// Irrelevant Methods //////////////////////////////////////

	@Override public boolean isPermissionSet(String name) {
		return false;
	}

	@Override public boolean isPermissionSet(Permission perm) {
		return false;
	}

	@Override public boolean hasPermission(String name) {
		return false;
	}

	@Override public boolean hasPermission(Permission perm) {
		return false;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin,
			String name, boolean value) {
		return null;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin) {
		return null;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin,
			String name, boolean value, int ticks) {
		return null;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return null;
	}

	@Override public void removeAttachment(PermissionAttachment attachment) {}

	@Override public void recalculatePermissions() {}

	@Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override public boolean isOp() {
		return false;
	}

	@Override public void setOp(boolean value) {}
	
}
