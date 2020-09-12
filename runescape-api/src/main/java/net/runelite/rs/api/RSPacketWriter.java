package net.runelite.rs.api;

import net.runelite.mapping.Import;

public interface RSPacketWriter
{
	@Import("addNode")
	void addNode(RSPacketBufferNode node);

	@Import("isaacCipher")
	RSIsaacCipher getIsaacCipher();
}
