# Arcane Flame Texture Instructions

To complete the Arcane Flame block implementation, you need to create two texture files:

1. `arcane_flame_0.png` - The first frame of the arcane flame animation
2. `arcane_flame_1.png` - The second frame of the arcane flame animation

These files should be placed in the following directory:
```
c:\dev\AncientSpellcraft\src\main\resources\assets\ancientspellcraft\textures\blocks\
```

## Texture Design Guidelines

For the arcane flame textures, I recommend creating 16x16 pixel textures with the following characteristics:

### For Green Arcane Flame:
- Use a color palette of greens (#00FF00, #33FF33, #66FF66) with some lighter yellows (#FFFFCC) in the center
- Create a flame-like pattern with transparency around the edges
- The first frame should have the flame in a lower, more compact position
- The second frame should have the flame slightly taller, giving the appearance of flickering when animated

### For Blue Arcane Flame (optional alternative):
- Use a palette of blues (#3333FF, #6666FF, #9999FF) with some lighter cyan (#CCFFFF) in the center
- Follow the same flame pattern as the green version

You can use any image editor that supports transparent PNGs, such as GIMP, Photoshop, or even online tools like Pixilart or Piskel.

## Alternative Approach

If creating custom textures is challenging, you can temporarily borrow from vanilla Minecraft by:
1. Extracting the `flame_layer_0.png` and `flame_layer_1.png` from the Minecraft jar
2. Recoloring them to the appropriate arcane flame colors (green/blue)
3. Saving them as `arcane_flame_0.png` and `arcane_flame_1.png` in your mod's texture directory

## Minecraft Configuration

Also remember that to make the textures animate, you'll need to add an entry in `assets/ancientspellcraft/textures/blocks/blocks.mcmeta` with appropriate animation settings.
