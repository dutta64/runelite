package net.runelite.mixins;

import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.MethodHook;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSClientPacket;
import net.runelite.rs.api.RSPacketBuffer;
import net.runelite.rs.api.RSPacketBufferNode;
import net.runelite.rs.api.RSPacketWriter;
import net.runelite.rs.api.RSWidget;
import org.slf4j.Logger;

@Mixin(RSPacketWriter.class)
public abstract class RSPacketWriterMixin implements RSPacketWriter
{
	@Shadow("client")
	private static RSClient client;

	@Inject
	@MethodHook("addNode")
	public final void logAddNode(final RSPacketBufferNode node)
	{
		if (!client.isPrintPackets())
		{
			return;
		}

		final RSClientPacket packet = node.getClientPacket();

		if (packet == null)
		{
			return;
		}

		final int id = packet.getId();

		final Logger log = client.getLogger();

		switch (id)
		{
			case 10: // Canvas dimensions
				log.info("{} windowMode={} canvasWidth={} canvasHeight={}",
					"CanvasDimensions", client.isResized() ? 2 : 1, client.getCanvasWidth(), client.getCanvasHeight());
				break;
			case 26: // Focus
				log.info("{} focused={}", "Focus", client.hadFocus());
				break;
			case 63: // DragInvWidget
				log.info("{} srcId={} srcItemSlot={} destItemSlot={}",
					"DragInvWidget", client.getIf1DraggedWidget().getId(), client.getIf1DraggedItemIndex(), client.getDragItemSlotDestination());
				break;
			case 66: // MouseClick
				final RSPacketBuffer buffer = node.getPacketBuffer();

				if (buffer == null)
				{
					break;
				}

				final byte[] payload = buffer.getPayload();

				final int lsb = payload[2] & 1;

				final int delay = (((payload[1] & 0xFF) << 8 | (payload[2] & 0xFF)) - lsb) >> 1;

				final int xPos = ((payload[3] & 0xFF) << 8) | (payload[4] & 0xFF);
				final int yPos = ((payload[5] & 0xFF) << 8) | (payload[6] & 0xFF);

				log.info("{} xPos={} yPos={} mButton={} delay={}",
					"MouseClick", xPos, yPos, lsb, delay);
				break;
			case 83: // DragBankWidget
				final RSWidget draggedWidget = client.getDraggedWidget();
				final RSWidget draggedOnWidget = client.getDraggedOnWidget();

				if (draggedWidget == null || draggedOnWidget == null)
				{
					return;
				}

				log.info("{} srcId={} srcItemId={} srcIdx={} destId={} destItemId={} destIdx={} ",
					"DragBankWidget", draggedWidget.getId(), draggedWidget.getItemId(), draggedWidget.getIndex(),
					draggedOnWidget.getId(), draggedOnWidget.getItemId(), draggedOnWidget.getIndex());
				break;
			case 98: // SceneWalk
				final int runVal = client.getPressedKeys()[82] ? (client.getPressedKeys()[81] ? 2 : 1) : 0;

				log.info("{} tileX={} tileY={} runVal={}",
					"SceneWalk", client.getSelectedSceneTileX() + client.getBaseX(), client.getSelectedSceneTileY() + client.getBaseY(), runVal);
				break;
			default: // Unknown
				log.info("{} id={}", "UnknownPacket", id);
				break;
		}
	}
}