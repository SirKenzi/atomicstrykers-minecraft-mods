package atomicstryker.minions.common.network;

import atomicstryker.minions.common.MinionsCore;
import atomicstryker.minions.common.network.NetworkHelper.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CustomDigPacket implements IPacket
{

    private String user;
    private int x, y, z, xzsize, ysize;

    public CustomDigPacket()
    {
    }

    public CustomDigPacket(String username, int a, int b, int c, int d, int e)
    {
        user = username;
        x = a;
        y = b;
        z = c;
        xzsize = d;
        ysize = e;
    }

    @Override
    public void writeBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        ByteBufUtils.writeUTF8String(bytes, user);
        bytes.writeInt(x);
        bytes.writeInt(y);
        bytes.writeInt(z);
        bytes.writeInt(xzsize);
        bytes.writeInt(ysize);
    }

    @Override
    public void readBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        user = ByteBufUtils.readUTF8String(bytes);
        x = bytes.readInt();
        y = bytes.readInt();
        z = bytes.readInt();
        xzsize = bytes.readInt();
        ysize = bytes.readInt();
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new ScheduledCode());
    }
    
    class ScheduledCode implements Runnable
    {

        @Override
        public void run()
        {
            EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(user);
            if (player != null)
            {            
                if (MinionsCore.instance.hasPlayerWillPower(player))
                {
                    MinionsCore.instance.orderMinionsToDigCustomSpace(player, x, y, z, xzsize, ysize);
                    MinionsCore.instance.exhaustPlayerBig(player);
                }
            }
        }
    }

}
