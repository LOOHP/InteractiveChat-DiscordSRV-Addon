package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

import java.awt.image.BufferedImage;

public class GeneratedTextureResource extends TextureResource {

    private BufferedImage image;

    public GeneratedTextureResource(BufferedImage image) {
        super(null, null, null, image);
        this.image = image;
    }

    public GeneratedTextureResource(ResourcePackFile file) {
        super(null, null, file, false);
    }

}
