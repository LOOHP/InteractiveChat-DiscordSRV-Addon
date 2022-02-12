package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class BackingEmptyFont extends LegacyUnicodeFont {

    private static final Int2ObjectMap<GlyphSize> EMPTY_SIZES = Int2ObjectMaps.singleton(' ', new GlyphSize((byte) 0, (byte) 6));

    public BackingEmptyFont(ResourceManager manager, FontProvider provider) {
        super(manager, provider, EMPTY_SIZES, null);
    }

    @Override
    public void reloadFonts() {
        super.reloadFonts();
        for (Entry<GlyphSize> entry : getSizes().int2ObjectEntrySet()) {
            int character = entry.getIntKey();
            GlyphSize size = entry.getValue();
            if (size.getEnd() - size.getStart() > 0) {
                charImages.put(character, Optional.of(new BufferedImage(size.getEnd() - size.getStart() + 1, 16, BufferedImage.TYPE_INT_ARGB)));
            }
        }
    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return true;
    }

}
