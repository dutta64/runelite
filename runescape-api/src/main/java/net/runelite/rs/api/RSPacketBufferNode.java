package net.runelite.rs.api;

import net.runelite.mapping.Import;

public interface RSPacketBufferNode
{
	@Import("clientPacket")
	RSClientPacket getClientPacket();

	@Import("packetBuffer")
	RSPacketBuffer getPacketBuffer();
}
