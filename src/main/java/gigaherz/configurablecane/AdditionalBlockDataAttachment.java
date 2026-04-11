package gigaherz.configurablecane;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AdditionalBlockDataAttachment
{
    private static AdditionalBlockDataAttachment get(ServerLevel level, BlockPos pos)
    {
        return level.getChunk(pos).getData(ConfigurableCane.CHUNK_DATA);
    }

    private static void remove(ServerLevel level, BlockPos pos)
    {
        level.getChunk(pos).removeData(ConfigurableCane.CHUNK_DATA);
    }

    public static boolean isTop(ServerLevel level, BlockPos pos)
    {
        var data = get(level, pos);

        return data.tops.contains(pos);
    }

    public static void setTop(ServerLevel level, BlockPos pos, boolean isTop)
    {
        var data = get(level, pos);
        if (isTop)
        {
            data.tops.add(pos);
        }
        else
        {
            data.tops.remove(pos);
            if (data.tops.isEmpty())
                remove(level, pos);
        }
    }

    public static void cleanup(ServerLevel level, BlockPos pos)
    {
        var data = get(level, pos);
        data.tops.remove(pos);
        if (data.tops.isEmpty())
            remove(level, pos);
    }

    private final HashSet<BlockPos> tops = new HashSet<>();

    public AdditionalBlockDataAttachment()
    {
    }

    private AdditionalBlockDataAttachment(Collection<BlockPos> loaded)
    {
        tops.addAll(loaded);
    }

    private List<BlockPos> asList()
    {
        return tops.stream().toList();
    }

    public static final MapCodec<AdditionalBlockDataAttachment> CODEC =
            BlockPos.CODEC.listOf().xmap(
                    AdditionalBlockDataAttachment::new,
                    AdditionalBlockDataAttachment::asList
            ).fieldOf("data");
}
