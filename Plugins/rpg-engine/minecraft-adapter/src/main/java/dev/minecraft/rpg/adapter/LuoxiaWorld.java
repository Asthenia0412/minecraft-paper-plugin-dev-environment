package dev.minecraft.rpg.adapter;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;

/** An opt-in, reproducible scenic world; never edits the existing survival world. */
public final class LuoxiaWorld extends ChunkGenerator implements Listener {
    public static final String NAME = "luoxia_peak_v1";
    private record Block(int x, int y, int z, BlockData data) {}
    private final Map<Long, List<Block>> chunks = new HashMap<>();
    private final Map<String, BlockData> palette = new HashMap<>();
    private final JavaPlugin plugin;
    private World world;
    private int buildings;

    public LuoxiaWorld(JavaPlugin plugin) {
        this.plugin = plugin;
        design();
    }

    public void open() {
        world = new WorldCreator(NAME).seed(20260906L).generator(this).generateStructures(false).createWorld();
        Objects.requireNonNull(world, "Cannot create Luoxia world");
        world.setSpawnLocation(0, 101, 0, 180);
        world.setTime(12000);
        world.setStorm(false);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(640);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Objects.requireNonNull(plugin.getCommand("luoxia")).setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player player) {
                if (args.length > 0 && args[0].equals("status")) {
                    Location p = player.getLocation();
                    boolean safe = !world.getBlockAt(0, 100, 0).isPassable()
                            && world.getBlockAt(0, 101, 0).isPassable()
                            && world.getBlockAt(0, 102, 0).isPassable();
                    player.sendMessage("Luoxia ready: world=" + p.getWorld().getName()
                            + ", buildings=" + buildings + ", spawnSafe=" + safe
                            + ", center=" + (Math.abs(p.getX()) < 3 && Math.abs(p.getZ()) < 3));
                } else {
                    arrive(player);
                }
            }
            return true;
        });
        for (Player player : plugin.getServer().getOnlinePlayers()) arrive(player);
        plugin.getLogger().info("Luoxia ready: " + buildings + " buildings, spawn 0 101 0");
    }

    private void arrive(Player player) {
        player.teleport(new Location(world, 0.5, 101, 0.5, 180, -7));
        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlight(true);
        player.setFlying(false);
        player.sendMessage(Component.text("落霞峰 · 云上仙居  |  /luoxia 回到中心  |  双击空格飞行"));
    }

    @EventHandler public void join(PlayerJoinEvent event) { arrive(event.getPlayer()); }
    @EventHandler public void respawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(new Location(world, 0.5, 101, 0.5, 180, -7));
    }

    private static long key(int x, int z) { return ((long) x << 32) ^ (z & 0xffffffffL); }
    @Override public BiomeProvider getDefaultBiomeProvider(WorldInfo info) {
        return new BiomeProvider() {
            @Override public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) { return Biome.CHERRY_GROVE; }
            @Override public List<Biome> getBiomes(WorldInfo worldInfo) { return List.of(Biome.CHERRY_GROVE); }
        };
    }
    private void set(int x, int y, int z, String material) {
        BlockData data = palette.computeIfAbsent(material, Bukkit::createBlockData);
        chunks.computeIfAbsent(key(Math.floorDiv(x, 16), Math.floorDiv(z, 16)), k -> new ArrayList<>())
                .add(new Block(Math.floorMod(x, 16), y, Math.floorMod(z, 16), data));
    }
    private void box(int x1, int y1, int z1, int x2, int y2, int z2, String material) {
        for (int x = x1; x <= x2; x++) for (int z = z1; z <= z2; z++)
            for (int y = y1; y <= y2; y++) set(x, y, z, material);
    }

    private static int height(int x, int z) {
        double radius = Math.hypot(x, z);
        if (radius < 114) return 100;
        double h = 57 + 42 * Math.exp(-Math.pow((radius - 110) / 45, 2));
        for (int i = 0; i < 11; i++) {
            double angle = i * Math.PI * 2 / 11;
            double d = Math.hypot(x - Math.cos(angle) * 190, z - Math.sin(angle) * 190);
            h += (58 + 28 * Math.sin(i * 7 + 2)) * Math.exp(-d * d / 1200);
        }
        return (int) (h + 2 * Math.sin(x * .11) * Math.cos(z * .09));
    }

    @Override public void generateNoise(WorldInfo info, Random random, int cx, int cz, ChunkData data) {
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
            int wx = cx * 16 + x, wz = cz * 16 + z, h = height(wx, wz);
            data.setRegion(x, info.getMinHeight(), z, x + 1, h - 3, z + 1, Material.STONE);
            data.setRegion(x, h - 3, z, x + 1, h, z + 1, Material.DIRT);
            data.setBlock(x, h, z, h > 125 ? Material.STONE : Material.GRASS_BLOCK);
            if (h < 62) data.setRegion(x, h + 1, z, x + 1, 63, z + 1, Material.WATER);
        }
        for (Block block : chunks.getOrDefault(key(cx, cz), List.of()))
            data.setBlock(block.x(), block.y(), block.z(), block.data());
    }

    private void roof(int x, int y, int z, int rx, int rz) {
        for (int a = -rx; a <= rx; a++) for (int b = -rz; b <= rz; b++) {
            int edge = Math.min(rx - Math.abs(a), rz - Math.abs(b));
            int rise = Math.min(edge, 6);
            int upturn = edge == 0 && (Math.abs(a) > rx - 3 || Math.abs(b) > rz - 3) ? 1 : 0;
            set(x + a, y + rise + upturn, z + b,
                    edge == 0 ? "dark_prismarine_slab[type=bottom]" : "dark_prismarine");
            if (edge == 1) set(x + a, y + rise - 1, z + b, "stripped_dark_oak_log[axis=x]");
        }
        box(x - Math.max(0, rx - rz), y + Math.min(rz, 6) + 1, z,
                x + Math.max(0, rx - rz), y + Math.min(rz, 6) + 1, z, "polished_blackstone_brick_slab");
        for (int s : new int[]{-1, 1}) {
            set(x + s * rx, y + 2, z - rz, "polished_blackstone_brick_wall");
            set(x + s * rx, y + 2, z + rz, "polished_blackstone_brick_wall");
        }
    }

    private void hall(int x, int z, int y, int rx, int rz, int stories) {
        buildings++;
        box(x-rx-2, 100, z-rz-2, x+rx+2, y, z+rz+2, "stone_bricks");
        box(x-rx-1, y, z-rz-1, x+rx+1, y, z+rz+1, "polished_andesite");
        for (int floor = 0; floor < stories; floor++) {
            int base = y + floor * 9;
            box(x-rx, base, z-rz, x+rx, base, z+rz, "spruce_planks");
            box(x-rx, base+1, z-rz, x+rx, base+5, z-rz, "white_terracotta");
            box(x-rx, base+1, z+rz, x+rx, base+5, z+rz, "white_terracotta");
            box(x-rx, base+1, z-rz, x-rx, base+5, z+rz, "white_terracotta");
            box(x+rx, base+1, z-rz, x+rx, base+5, z+rz, "white_terracotta");
            for (int a = -rx; a <= rx; a += 4) {
                for (int b : new int[]{-rz, rz}) {
                    box(x+a, base+1, z+b, x+a, base+6, z+b, "stripped_mangrove_log");
                    if (a + 2 < rx) box(x+a+1, base+2, z+b, x+a+2, base+4, z+b, "dark_oak_fence");
                }
            }
            for (int a : new int[]{-rx, rx}) {
                for (int b = -rz; b <= rz; b += 4)
                    box(x+a, base+1, z+b, x+a, base+6, z+b, "stripped_mangrove_log");
            }
            box(x-1, base+1, z+rz, x+1, base+3, z+rz, "air");
            box(x-1, base+1, z-rz, x+1, base+3, z-rz, "air");
            box(x-rx, base+6, z-rz, x+rx, base+6, z+rz, "dark_oak_planks");
            for (int a : new int[]{-rx+1, rx-1}) for (int b : new int[]{-rz-1, rz+1})
                set(x+a, base+5, z+b, "lantern[hanging=true]");
            box(x-rx+2, base+1, z-rz+2, x-rx+2, base+1, z+rz-2, "dark_oak_stairs[facing=west]");
            set(x, base+1, z, "chiseled_bookshelf");
            roof(x, base+7, z, rx+3, rz+3);
        }
        for (int step = 0; step < y-100; step++)
            box(x-3, 101+step, z+rz+2+(y-101-step), x+3, 101+step,
                    z+rz+2+(y-101-step), "stone_brick_stairs[facing=north]");
    }

    private void pavilion(int x, int z, int y, int radius) {
        buildings++;
        box(x-radius, y-1, z-radius, x+radius, y, z+radius, "stone_bricks");
        for (int a : new int[]{-radius+1, radius-1}) for (int b : new int[]{-radius+1, radius-1}) {
            box(x+a, y+1, z+b, x+a, y+6, z+b, "stripped_mangrove_log");
            set(x+a, y+5, z+b+(b<0?1:-1), "lantern[hanging=true]");
        }
        roof(x, y+7, z, radius+2, radius+2);
        set(x, y+1, z, "chiseled_quartz_block");
    }

    private void tree(int x, int z, int size, boolean cherry) {
        int y = height(x, z) + 1;
        String leaves = cherry ? "cherry_leaves[persistent=true]" : "spruce_leaves[persistent=true]";
        box(x, y, z, x, y+size, z, cherry ? "cherry_log" : "spruce_log");
        for (int branch : new int[]{-1,1})
            box(x+Math.min(0,branch*3), y+size-2, z, x+Math.max(0,branch*3), y+size-2, z, "cherry_log[axis=x]");
        for (int a=-size; a<=size; a++) for (int b=-size; b<=size; b++) for (int c=-2; c<=2; c++)
            if (a*a+b*b+c*c*7 < size*size && (a*17+b*13+c*7)%11 != 0)
                set(x+a, y+size+c, z+b, leaves);
    }

    private void lamp(int x, int z) {
        set(x,101,z,"stone_brick_wall");
        box(x,102,z,x,104,z,"dark_oak_fence");
        set(x,105,z,"dark_oak_slab");
        set(x+1,104,z,"lantern[hanging=true]");
    }

    private void design() {
        // Broad processional avenue, intersecting lanes and a radial inlaid court.
        box(-5,100,-104,5,100,103,"stone_bricks");
        box(-103,100,-4,103,100,4,"stone_bricks");
        box(-64,100,-88,-60,100,88,"stone_bricks");
        box(60,100,-88,64,100,88,"stone_bricks");
        for (int z : new int[]{-36,36,78}) box(-89,100,z-2,89,100,z+2,"stone_bricks");
        for (int x=-24;x<=24;x++) for(int z=-24;z<=24;z++) {
            double r=Math.hypot(x,z);
            if(r<24) set(x,100,z, r>22||r<5 ? "polished_blackstone" :
                    ((Math.abs(x)==Math.abs(z) || Math.abs(x)<2 || Math.abs(z)<2) ? "quartz_block" : "smooth_stone"));
            if(r>16 && r<19 && (Math.abs(x)<6 || Math.abs(z)<6)) set(x,100,z,"pink_terracotta");
        }
        // Elevated sanctuary closes the northward view from the spawn.
        hall(0,-72,107,17,10,2);
        hall(-37,-73,102,8,8,1);
        hall(37,-73,102,8,8,1);
        for(int x:new int[]{-37,37}) for(int z:new int[]{-39,36,71}) hall(x,z,101,8,7,1);
        for(int x:new int[]{-82,82}) for(int z:new int[]{-66,-36,36,70}) hall(x,z,101,7,6,1);
        // A three-bay ceremonial gate to the south.
        for(int x:new int[]{-12,-5,5,12}) box(x,101,97,x,109,97,"stripped_mangrove_log");
        box(-13,108,97,13,109,97,"red_terracotta");
        roof(0,110,97,15,4);
        buildings++;
        // Lotus lake, curved footbridge and waterside pavilion.
        for(int x=26;x<=57;x++) for(int z=-22;z<=22;z++) {
            if(Math.pow((x-42)/16.0,2)+Math.pow(z/21.0,2)<1) {
                set(x,98,z,"clay"); set(x,99,z,"water"); set(x,100,z,"air");
                if((x*31+z*17)%29==0) set(x,100,z,"lily_pad");
            }
        }
        for(int x=25;x<=59;x++) {
            int y=101+(int)(3*Math.sin((x-25)*Math.PI/34));
            box(x,y,-2,x,y,2,"spruce_planks");
            set(x,y+1,-2,"dark_oak_fence"); set(x,y+1,2,"dark_oak_fence");
            if(x%4==0) { box(x,98,-2,x,y,-2,"spruce_log"); box(x,98,2,x,y,2,"spruce_log"); }
        }
        pavilion(43,-13,100,4);
        pavilion(-46,0,100,5);
        // Multi-tier pagoda on a western belvedere.
        hall(-93,0,101,5,5,3);
        box(-106,100,-15,-72,100,-13,"stone_bricks");
        box(-106,101,-15,-72,101,-15,"stone_brick_wall");
        // Enclosed gardens and hanging lanterns along all avenues.
        for(int z=-94;z<=90;z+=12) { lamp(-7,z); lamp(7,z); }
        for(int x=-92;x<=92;x+=12) if(Math.abs(x)>24 && (x<25||x>59)) { lamp(x,-6); lamp(x,6); }
        for(int x:new int[]{-57,57}) for(int z=-90;z<=84;z+=15) tree(x,z,5,true);
        for(int x:new int[]{-20,20}) for(int z:new int[]{-49,-31,30,49,69,86}) tree(x,z,5,true);
        tree(-37,15,9,true);
        tree(28,18,6,true);
        Random random=new Random(42);
        for(int i=0;i<180;i++) {
            int x=random.nextInt(221)-110,z=random.nextInt(221)-110;
            if(Math.hypot(x,z)>96 && Math.abs(x)>12) tree(x,z,4+random.nextInt(3),i%3!=0);
        }
        for(int x=-53;x<-26;x++) for(int z=9;z<25;z++)
            if((x*7+z*13)%5==0) set(x,101,z,"pink_petals[flower_amount=4]");
        // Rocky horizon, forested shoulders and a distant summit pagoda.
        for(int i=0;i<130;i++) {
            int x=random.nextInt(501)-250,z=random.nextInt(501)-250;
            if(Math.hypot(x,z)>128 && height(x,z)>65 && height(x,z)<123) tree(x,z,5,false);
        }
        int summitY=height(190,0)+1;
        pavilion(190,0,summitY,6);
        for(int x=112;x<=190;x++) {
            int y=height(x,0)+1;
            box(x,y,-2,x,y,2,"stone_bricks");
        }
    }
}
