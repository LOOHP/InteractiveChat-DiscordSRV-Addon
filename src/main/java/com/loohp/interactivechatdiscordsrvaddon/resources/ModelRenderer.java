package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.loohp.blockmodelrenderer.render.Hexahedron;
import com.loohp.blockmodelrenderer.render.Model;
import com.loohp.blockmodelrenderer.render.Point3D;
import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechat.utils.CustomArrayUtils;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.Coordinates3D;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelElement;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelElement.ModelElementRotation;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace.ModelFaceSide;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelGUILight;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.TextureUV;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureAnimation;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureMeta;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.TintIndexData;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelRenderer implements AutoCloseable {

    public static final int INTERNAL_W = 64;
    public static final int INTERNAL_H = 64;

    public static final int QUALITY_THRESHOLD = 70;

    public static final int SKIN_RESOLUTION = 1600;
    public static final int TEXTURE_RESOLUTION = 800;

    public static final String CACHE_KEY = "ModelRender";
    public static final String MODEL_NOT_FOUND = "notfound";

    private static final double[] OVERLAY_ADDITION_FACTORS = new double[6];

    private static final String PLAYER_MODEL_RESOURCELOCATION = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + "player_model";
    private static final String PLAYER_MODEL_SLIM_RESOURCELOCATION = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + "player_model_slim";

    static {
        Arrays.fill(OVERLAY_ADDITION_FACTORS, ImageGeneration.ENCHANTMENT_GLINT_FACTOR);
    }

    private ThreadPoolExecutor modelResolvingService;
    private ThreadPoolExecutor renderingService;
    private ScheduledExecutorService controlService;
    private AtomicBoolean isValid;

    public ModelRenderer(Supplier<Integer> modelThreads, Supplier<Integer> renderThreads) {
        this.isValid = new AtomicBoolean(true);
        ThreadFactory factory0 = new ThreadFactoryBuilder().setNameFormat("InteractiveChatDiscordSRVAddon Async Model Resolving Thread #%d").build();
        this.modelResolvingService = new ThreadPoolExecutor(modelThreads.get(), modelThreads.get(), 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), factory0);
        ThreadFactory factory1 = new ThreadFactoryBuilder().setNameFormat("InteractiveChatDiscordSRVAddon Async Model Rendering Thread #%d").build();
        this.renderingService = new ThreadPoolExecutor(renderThreads.get(), renderThreads.get(), 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), factory1);
        ThreadFactory factory2 = new ThreadFactoryBuilder().setNameFormat("InteractiveChatDiscordSRVAddon Async Model Renderer Control Thread").build();
        this.controlService = Executors.newSingleThreadScheduledExecutor(factory2);

        this.controlService.scheduleAtFixedRate(() -> {
            this.modelResolvingService.setCorePoolSize(modelThreads.get());
            this.modelResolvingService.setMaximumPoolSize(modelThreads.get());
            this.renderingService.setCorePoolSize(renderThreads.get());
            this.renderingService.setMaximumPoolSize(renderThreads.get());
        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void close() {
        isValid.set(false);
        controlService.shutdown();
        modelResolvingService.shutdown();
        renderingService.shutdown();
    }

    public RenderResult renderPlayer(int width, int height, ResourceManager manager, boolean post1_8, boolean slim, Map<String, TextureResource> providedTextures, TintIndexData tintIndexData, Map<PlayerModelItemPosition, PlayerModelItem> modelItems) {
        String cacheKey = cacheKey(width, height, manager.getUuid(), slim, cacheKeyModelItems(modelItems), cacheKeyProvidedTextures(providedTextures));
        Cache<?> cachedRender = Cache.getCache(cacheKey);
        if (cachedRender != null) {
            RenderResult cachedResult = (RenderResult) cachedRender.getObject();
            if (cachedResult.isSuccessful()) {
                return cachedResult;
            }
        }

        BlockModel playerModel = manager.getModelManager().resolveBlockModel(slim ? PLAYER_MODEL_SLIM_RESOURCELOCATION : PLAYER_MODEL_RESOURCELOCATION, Collections.emptyMap());
        if (playerModel == null) {
            return new RenderResult(MODEL_NOT_FOUND, null);
        }
        Model playerRenderModel = generateStandardRenderModel(playerModel, manager, providedTextures, tintIndexData, false, true);

        for (PlayerModelItem playerModelItem : modelItems.values()) {
            BlockModel itemBlockModel = playerModelItem.getModelKey() == null ? null : manager.getModelManager().resolveBlockModel(playerModelItem.getModelKey(), playerModelItem.getPredicate());
            Model itemRenderModel = null;
            if (itemBlockModel != null) {
                if (itemBlockModel.getRawParent() == null || !itemBlockModel.getRawParent().contains("/")) {
                    itemRenderModel = generateStandardRenderModel(itemBlockModel, manager, playerModelItem.getProvidedTextures(), playerModelItem.getTintIndexData(), playerModelItem.isEnchanted(), false);
                } else if (itemBlockModel.getRawParent().equals(ModelManager.ITEM_BASE)) {
                    BufferedImage image = new BufferedImage(INTERNAL_W, INTERNAL_H, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = image.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    for (int i = 0; itemBlockModel.getTextures().containsKey(ModelManager.ITEM_BASE_LAYER + i); i++) {
                        String resourceLocation = itemBlockModel.getTextures().get(ModelManager.ITEM_BASE_LAYER + i);
                        if (!resourceLocation.contains(":")) {
                            resourceLocation = ResourceRegistry.DEFAULT_NAMESPACE + ":" + resourceLocation;
                        }
                        TextureResource resource = playerModelItem.getProvidedTextures().get(resourceLocation);
                        if (resource == null) {
                            resource = manager.getTextureManager().getTexture(resourceLocation);
                        }
                        BufferedImage texture = resource.getTexture();
                        texture = tintIndexData.applyTint(texture, 1);
                        if (resource.hasTextureMeta()) {
                            TextureMeta meta = resource.getTextureMeta();
                            if (meta.hasAnimation()) {
                                TextureAnimation animation = meta.getAnimation();
                                if (animation.hasWidth() && animation.hasHeight()) {
                                    texture = ImageUtils.copyAndGetSubImage(texture, 0, 0, animation.getWidth(), animation.getHeight());
                                } else {
                                    texture = ImageUtils.copyAndGetSubImage(texture, 0, 0, texture.getWidth(), texture.getWidth());
                                }
                            }
                        }
                        if (resourceLocation.equals(ResourceRegistry.MAP_MARKINGS_LOCATION)) {
                            ImageUtils.xor(image, ImageUtils.resizeImageAbs(texture, image.getWidth(), image.getHeight()), 200);
                        } else {
                            g.drawImage(texture, 0, 0, image.getWidth(), image.getHeight(), null);
                        }
                    }
                    g.dispose();
                    image = tintIndexData.applyTint(image, 0);
                    if (playerModelItem.isEnchanted()) {
                        image = ImageGeneration.getEnchantedImage(image);
                    }
                    itemRenderModel = generateItemRenderModel(16, 16, 16, image);
                }
            }

            if (itemRenderModel != null) {
                itemRenderModel.translate(-16 / 2.0, -16 / 2.0, -16 / 2.0);
                ModelDisplay displayData = itemBlockModel.getRawDisplay().get(playerModelItem.getPosition().getModelDisplayPosition());
                boolean flipX = playerModelItem.getPosition().isLiteralFlipped();
                boolean isMirror = false;
                if (displayData == null && playerModelItem.getPosition().getModelDisplayPosition().hasFallback()) {
                    displayData = itemBlockModel.getRawDisplay().get(playerModelItem.getPosition().getModelDisplayPosition().getFallback());
                    isMirror = true;
                }
                if (isMirror || !flipX) {
                    if (displayData != null) {
                        Coordinates3D scale = displayData.getScale();
                        itemRenderModel.scale(scale.getX(), scale.getY(), scale.getZ());
                        Coordinates3D rotation = displayData.getRotation();
                        itemRenderModel.rotate(rotation.getX(), rotation.getY(), rotation.getZ() + (post1_8 ? 10 : 0), false);
                        Coordinates3D transform = displayData.getTranslation();
                        itemRenderModel.translate(transform.getX(), transform.getY() + (post1_8 ? -10 : 0), transform.getZ() + (post1_8 ? -2.75 : 0));
                    }
                    if (flipX) {
                        itemRenderModel.flipAboutPlane(false, true, true);
                    }
                } else {
                    if (displayData != null) {
                        Coordinates3D scale = displayData.getScale();
                        itemRenderModel.scale(scale.getX(), scale.getY(), scale.getZ());
                        Coordinates3D rotation = displayData.getRotation();
                        itemRenderModel.rotate(rotation.getX(), -rotation.getY(), -rotation.getZ(), false);
                        Coordinates3D transform = displayData.getTranslation();
                        itemRenderModel.translate(-transform.getX(), transform.getY(), transform.getZ());
                    }
                }
                if (playerModelItem.getPosition().yIsZAxis()) {
                    if (post1_8) {
                        itemRenderModel.rotate(180, 180, 0, false);
                    } else {
                        itemRenderModel.rotate(90, 0, 0, true);
                    }
                }
                double scale = playerModelItem.getPosition().getScale();
                itemRenderModel.scale(scale, scale, scale);
                itemRenderModel.translate((16 * scale) / 2, (16 * scale) / 2, (16 * scale) / 2);
                Coordinates3D defaultTranslation = playerModelItem.getPosition().getDefaultTranslate();
                itemRenderModel.translate(defaultTranslation.getX(), defaultTranslation.getY(), defaultTranslation.getZ());
                playerRenderModel.append(itemRenderModel);
            }
        }

        playerRenderModel.translate(-16 / 2.0, -16 / 2.0, -16 / 2.0);
        playerRenderModel.rotate(0, 180, 0, false);
        playerRenderModel.translate(16 / 2.0, 16 / 2.0, 16 / 2.0);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        renderPlayerModel(playerRenderModel, image, playerModel.getGUILight());
        RenderResult result = new RenderResult(image, null);
        Cache.putCache(cacheKey, result, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
        return result;
    }

    public RenderResult render(int width, int height, ResourceManager manager, String modelKey, ModelDisplayPosition displayPosition, boolean enchanted) {
        return render(width, height, manager, modelKey, displayPosition, Collections.emptyMap(), Collections.emptyMap(), TintIndexData.EMPTY_INSTANCE, enchanted);
    }

    public RenderResult render(int width, int height, ResourceManager manager, String modelKey, ModelDisplayPosition displayPosition, Map<ModelOverrideType, Float> predicate, Map<String, TextureResource> providedTextures, TintIndexData tintIndexData, boolean enchanted) {
        String cacheKey = cacheKey(width, height, manager.getUuid(), modelKey, displayPosition, predicate, cacheKeyProvidedTextures(providedTextures), enchanted);
        Cache<?> cachedRender = Cache.getCache(cacheKey);
        if (cachedRender != null) {
            RenderResult cachedResult = (RenderResult) cachedRender.getObject();
            if (cachedResult.isSuccessful()) {
                return cachedResult;
            }
        }

        String rejectedReason = null;
        BlockModel blockModel = manager.getModelManager().resolveBlockModel(modelKey, predicate);
        if (blockModel == null) {
            return new RenderResult(MODEL_NOT_FOUND, null);
        }
        BufferedImage image = new BufferedImage(INTERNAL_W, INTERNAL_H, BufferedImage.TYPE_INT_ARGB);
        if (blockModel.getRawParent() == null || !blockModel.getRawParent().contains("/")) {
            renderBlockModel(generateStandardRenderModel(blockModel, manager, providedTextures, tintIndexData, enchanted, false), image, blockModel.getDisplay(displayPosition), blockModel.getGUILight());
        } else if (blockModel.getRawParent().equals(ModelManager.ITEM_BASE)) {
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            for (int i = 0; blockModel.getTextures().containsKey(ModelManager.ITEM_BASE_LAYER + i); i++) {
                String resourceLocation = blockModel.getTextures().get(ModelManager.ITEM_BASE_LAYER + i);
                if (!resourceLocation.contains(":")) {
                    resourceLocation = ResourceRegistry.DEFAULT_NAMESPACE + ":" + resourceLocation;
                }
                TextureResource resource = providedTextures.get(resourceLocation);
                if (resource == null) {
                    resource = manager.getTextureManager().getTexture(resourceLocation);
                }
                BufferedImage texture = resource.getTexture();
                if (resource.hasTextureMeta()) {
                    TextureMeta meta = resource.getTextureMeta();
                    if (meta.hasAnimation()) {
                        TextureAnimation animation = meta.getAnimation();
                        if (animation.hasWidth() && animation.hasHeight()) {
                            texture = ImageUtils.copyAndGetSubImage(texture, 0, 0, animation.getWidth(), animation.getHeight());
                        } else {
                            texture = ImageUtils.copyAndGetSubImage(texture, 0, 0, texture.getWidth(), texture.getWidth());
                        }
                    }
                }
                if (resourceLocation.equals(ResourceRegistry.MAP_MARKINGS_LOCATION)) {
                    ImageUtils.xor(image, ImageUtils.resizeImageAbs(texture, image.getWidth(), image.getHeight()), 200);
                } else {
                    g.drawImage(texture, 0, 0, image.getWidth(), image.getHeight(), null);
                }
            }
            g.dispose();
            image = tintIndexData.applyTint(image, 0);
            if (enchanted) {
                image = ImageGeneration.getEnchantedImage(image);
            }
        } else {
            rejectedReason = blockModel.getRawParent();
        }
        RenderResult result;
        if (rejectedReason == null) {
            result = new RenderResult(ImageUtils.resizeImageQuality(image, width, height), blockModel);
        } else {
            result = new RenderResult(rejectedReason == null ? "null" : rejectedReason, blockModel);
        }
        Cache.putCache(cacheKey, result, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
        return result;
    }

    private Model generateItemRenderModel(double width, double height, double depth, BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        double intervalX = (1.0 / (double) w) * width;
        double intervalY = (1.0 / (double) h) * height;
        double z = depth / 2 - 0.5;
        List<Hexahedron> hexahedrons = new ArrayList<>();
        int[] colors = image.getRGB(0, 0, w, h, null, 0, w);
        hexahedrons.add(Hexahedron.fromCorners(new Point3D(0, 0, z), new Point3D(width, height, z + 1), false, new BufferedImage[] {null, null, ImageUtils.copyImage(image), null, ImageUtils.copyImage(image), null}));
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = colors[y * w + x];
                if (ColorUtils.getAlpha(color) > 0) {
                    BufferedImage pixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                    pixel.setRGB(0, 0, color);
                    BufferedImage[] imageArray = new BufferedImage[6];
                    imageArray[0] = ColorUtils.getAlpha(ImageUtils.getRGB(colors, x, y + 1, w, h)) <= 0 ? ImageUtils.copyImage(pixel) : null; //u
                    imageArray[1] = ColorUtils.getAlpha(ImageUtils.getRGB(colors, x, y - 1, w, h)) <= 0 ? ImageUtils.copyImage(pixel) : null; //d
                    imageArray[2] = null; //n
                    imageArray[3] = ColorUtils.getAlpha(ImageUtils.getRGB(colors, x + 1, y, w, h)) <= 0 ? ImageUtils.copyImage(pixel) : null; //e
                    imageArray[4] = null; //s
                    imageArray[5] = ColorUtils.getAlpha(ImageUtils.getRGB(colors, x - 1, y, w, h)) <= 0 ? ImageUtils.copyImage(pixel) : null; //w
                    if (!CustomArrayUtils.allNull(imageArray)) {
                        double scaledX = (double) x * intervalX;
                        double scaledY = height - (double) y * intervalY;
                        hexahedrons.add(Hexahedron.fromCorners(new Point3D(scaledX, scaledY, z), new Point3D(scaledX + intervalX, scaledY - intervalY, z + 1), false, imageArray));
                    }
                }
            }
        }
        return new Model(hexahedrons);
    }

    private Model generateStandardRenderModel(BlockModel blockModel, ResourceManager manager, Map<String, TextureResource> providedTextures, TintIndexData tintIndexData, boolean enchanted, boolean skin) {
        Map<String, BufferedImage> cachedResize = new ConcurrentHashMap<>();
        List<ModelElement> elements = new ArrayList<>(blockModel.getElements());
        List<Hexahedron> hexahedrons = new ArrayList<>(elements.size());
        Queue<Future<Hexahedron>> tasks = new ConcurrentLinkedQueue<>();
        Iterator<ModelElement> itr = elements.iterator();
        while (itr.hasNext()) {
            ModelElement element = itr.next();
            tasks.add(modelResolvingService.submit(() -> {
                ModelElementRotation rotation = element.getRotation();
                boolean ignoreZFight = false;
                if (rotation != null) {
                    ignoreZFight = rotation.isRescale();
                }
                Hexahedron hexahedron = Hexahedron.fromCorners(new Point3D(element.getFrom().getX(), element.getFrom().getY(), element.getFrom().getZ()), new Point3D(element.getTo().getX(), element.getTo().getY(), element.getTo().getZ()), ignoreZFight, new BufferedImage[6]);
                BufferedImage[] images = new BufferedImage[6];
                BufferedImage[] overlayImages = new BufferedImage[6];
                int i = 0;
                for (ModelFaceSide side : ModelFaceSide.values()) {
                    ModelFace faceData = element.getFace(side);
                    if (faceData == null) {
                        images[i] = null;
                    } else {
                        TextureUV uv = faceData.getUV();
                        TextureResource resource = providedTextures.get(faceData.getTexture());
                        if (resource == null) {
                            resource = manager.getTextureManager().getTexture(faceData.getTexture(), false);
                        }
                        if (resource == null || !resource.isTexture()) {
                            images[i] = null;
                        } else if (uv != null && (uv.getXDiff() == 0 || uv.getYDiff() == 0)) {
                            images[i] = null;
                        } else {
                            BufferedImage cached = cachedResize.get(faceData.getTexture());
                            if (cached == null) {
                                cached = resource.getTexture();
                                if (resource.hasTextureMeta()) {
                                    TextureMeta meta = resource.getTextureMeta();
                                    if (meta.hasAnimation()) {
                                        TextureAnimation animation = meta.getAnimation();
                                        if (animation.hasWidth() && animation.hasHeight()) {
                                            cached = ImageUtils.copyAndGetSubImage(cached, 0, 0, animation.getWidth(), animation.getHeight());
                                        } else {
                                            cached = ImageUtils.copyAndGetSubImage(cached, 0, 0, cached.getWidth(), cached.getWidth());
                                        }
                                    }
                                }
                                if (cached.getWidth() > cached.getHeight()) {
                                    cached = ImageUtils.resizeImageFillWidth(cached, skin ? SKIN_RESOLUTION : TEXTURE_RESOLUTION);
                                } else {
                                    cached = ImageUtils.resizeImageFillHeight(cached, skin ? SKIN_RESOLUTION : TEXTURE_RESOLUTION);
                                }
                                cachedResize.put(faceData.getTexture(), cached);
                            }
                            images[i] = ImageUtils.copyImage(cached);

                            if (uv == null) {
                                Point3D[] points;
                                switch (side) {
                                    case DOWN:
                                        points = hexahedron.getDownFace().getPoints();
                                        break;
                                    case EAST:
                                        points = hexahedron.getEastFace().getPoints();
                                        break;
                                    case NORTH:
                                        points = hexahedron.getNorthFace().getPoints();
                                        break;
                                    case SOUTH:
                                        points = hexahedron.getSouthFace().getPoints();
                                        break;
                                    case UP:
                                        points = hexahedron.getUpFace().getPoints();
                                        break;
                                    case WEST:
                                    default:
                                        points = hexahedron.getWestFace().getPoints();
                                        break;
                                }
                                double x1;
                                double y1;
                                double x2;
                                double y2;
                                if (points[0].x == points[2].x) {
                                    x1 = points[0].y;
                                    y1 = points[0].z;
                                    x2 = points[2].y;
                                    y2 = points[2].z;
                                } else if (points[0].y == points[2].y) {
                                    x1 = points[0].z;
                                    y1 = points[0].x;
                                    x2 = points[2].z;
                                    y2 = points[2].x;
                                } else {
                                    x1 = points[0].y;
                                    y1 = points[0].x;
                                    x2 = points[2].y;
                                    y2 = points[2].x;
                                }
                                uv = new TextureUV(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
                            }
                            uv = uv.getScaled(1, (double) images[i].getHeight() / (double) images[i].getWidth());
                            uv = uv.getScaled((double) images[i].getWidth() / 16.0);
                            double x1 = uv.getX1();
                            double y1 = uv.getY1();
                            double dX = Math.abs(uv.getXDiff());
                            double dY = Math.abs(uv.getYDiff());
                            if (uv.isVerticallyFlipped()) {
                                images[i] = ImageUtils.flipVertically(images[i]);
                                y1 = images[i].getHeight() - y1;
                            }
                            if (uv.isHorizontallyFlipped()) {
                                images[i] = ImageUtils.flipHorizontal(images[i]);
                                x1 = images[i].getWidth() - x1;
                            }
                            images[i] = ImageUtils.rotateImageByDegrees(ImageUtils.copyAndGetSubImage(images[i], (int) x1, (int) y1, Math.max(1, (int) dX), Math.max(1, (int) dY)), faceData.getRotation());
                            images[i] = tintIndexData.applyTint(images[i], faceData.getTintindex());
                            if (enchanted) {
                                overlayImages[i] = ImageGeneration.getRawEnchantedImage(images[i]);
                            }
                        }
                    }
                    i++;
                }
                hexahedron.setImage(images);
                hexahedron.setOverlay(overlayImages);
                hexahedron.setOverlayAdditionFactor(OVERLAY_ADDITION_FACTORS);
                if (rotation != null) {
                    hexahedron.translate(-rotation.getOrigin().getX(), -rotation.getOrigin().getY(), -rotation.getOrigin().getZ());
                    switch (rotation.getAxis()) {
                        case X:
                            hexahedron.rotate(rotation.getAngle(), 0, 0, false);
                            break;
                        case Y:
                            hexahedron.rotate(0, rotation.getAngle(), 0, false);
                            break;
                        case Z:
                        default:
                            hexahedron.rotate(0, 0, rotation.getAngle(), false);
                            break;
                    }
                    hexahedron.translate(rotation.getOrigin().getX(), rotation.getOrigin().getY(), rotation.getOrigin().getZ());
                }
                return hexahedron;
            }));
            itr.remove();
        }
        while (!tasks.isEmpty() || !elements.isEmpty()) {
            Future<Hexahedron> task = tasks.poll();
            if (task == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                try {
                    hexahedrons.add(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Model(hexahedrons);
    }

    private void renderPlayerModel(Model renderModel, BufferedImage image, ModelGUILight lightData) {
        AffineTransform baseTransform = AffineTransform.getTranslateInstance(image.getWidth() / 2.0, (double) image.getHeight() / 7 * 5);
        baseTransform.concatenate(AffineTransform.getScaleInstance(image.getWidth() / 39.09375, image.getWidth() / 39.09375));
        renderModel.translate(-16 / 2.0, -16 / 2.0, -16 / 2.0);
        renderModel.updateLighting(lightData.getLightVector(), lightData.getAmbientLevel(), lightData.getMaxLevel());
        long start = System.currentTimeMillis();
        renderModel.render(image, true, baseTransform, renderingService).join();
        InteractiveChatDiscordSrvAddon.plugin.playerModelRenderingTimes.add((int) Math.min(System.currentTimeMillis() - start, Integer.MAX_VALUE));
    }

    private void renderBlockModel(Model renderModel, BufferedImage image, ModelDisplay displayData, ModelGUILight lightData) {
        AffineTransform baseTransform = AffineTransform.getTranslateInstance(image.getWidth() / 2.0, image.getHeight() / 2.0);
        baseTransform.concatenate(AffineTransform.getScaleInstance(image.getWidth() / 16.0, image.getHeight() / 16.0));
        renderModel.translate(-16 / 2.0, -16 / 2.0, -16 / 2.0);
        if (displayData != null) {
            Coordinates3D scale = displayData.getScale();
            renderModel.scale(scale.getX(), scale.getY(), scale.getZ());
            Coordinates3D rotation = displayData.getRotation();
            renderModel.rotate(rotation.getX(), rotation.getY(), rotation.getZ(), false);
            Coordinates3D transform = displayData.getTranslation();
            renderModel.translate(transform.getX(), transform.getY(), transform.getZ());
        }
        renderModel.updateLighting(lightData.getLightVector(), lightData.getAmbientLevel(), lightData.getMaxLevel());
        renderModel.render(image, renderModel.getComponents().size() <= QUALITY_THRESHOLD, baseTransform, renderingService).join();
    }

    private String cacheKey(Object... obj) {
        return Stream.of(obj).map(each -> {
            if (each == null) {
                return "null";
            } else if (each instanceof Map) {
                return cacheKeyMap((Map<?, ?>) each);
            }
            return each.toString();
        }).collect(Collectors.joining("/", CACHE_KEY + "/", ""));
    }

    private String cacheKeyMap(Map<?, ?> map) {
        Comparator<Entry<?, ?>> c = Comparator.comparing(entry -> entry.getKey() == null ? "null" : entry.getKey().toString());
        c = c.thenComparing(entry -> entry.getValue() == null ? "null" : entry.getValue().toString());
        return map.entrySet().stream().sorted(c).map(entry -> {
            return (entry.getKey() == null ? "null" : entry.getKey().toString()) + ":" + (entry.getValue() == null ? "null" : entry.getValue().toString());
        }).collect(Collectors.joining(", ", "{", "}"));
    }

    private String cacheKeyProvidedTextures(Map<String, TextureResource> providedTextures) {
        return providedTextures.entrySet().stream().map(entry -> {
            TextureResource resource = entry.getValue();
            if (resource.isTexture()) {
                return entry.getKey() + ":" + ImageUtils.hash(resource.getTexture());
            } else if (resource.hasFile()) {
                return entry.getKey() + ":" + resource.getFile().getAbsolutePath();
            }
            return entry.getKey() + ":" + resource;
        }).collect(Collectors.joining(", ", "{", "}"));
    }

    private String cacheKeyModelItems(Map<PlayerModelItemPosition, PlayerModelItem> modelItems) {
        return modelItems.entrySet().stream().map(entry -> {
            PlayerModelItem resource = entry.getValue();
            return entry.getKey() + ":[" + resource.getPosition() + ", " + resource.getModelKey() + ", " + cacheKeyMap(resource.getPredicate()) + ", " + resource.isEnchanted() + ", " + cacheKeyProvidedTextures(resource.getProvidedTextures()) + "]";
        }).collect(Collectors.joining(", ", "{", "}"));
    }

    public enum PlayerModelItemPosition {

        HELMET(new Coordinates3D(3.05, 22.05, 3.05), 0.62, false, false, ModelDisplayPosition.HEAD),
        RIGHT_HAND(new Coordinates3D(7.3, -2.5, 4.05), 0.9, false, true, ModelDisplayPosition.THIRDPERSON_RIGHTHAND),
        LEFT_HAND(new Coordinates3D(-5.6, -2.5, 4.05), 0.9, true, true, ModelDisplayPosition.THIRDPERSON_LEFTHAND);

        private Coordinates3D defaultTranslate;
        private double scale;
        private boolean literalFlipped;
        private boolean yIsZAxis;
        private ModelDisplayPosition modelDisplayPosition;

        PlayerModelItemPosition(Coordinates3D defaultTranslate, double scale, boolean literalFlipped, boolean yIsZAxis, ModelDisplayPosition modelDisplayPosition) {
            this.defaultTranslate = defaultTranslate;
            this.scale = scale;
            this.literalFlipped = literalFlipped;
            this.yIsZAxis = yIsZAxis;
            this.modelDisplayPosition = modelDisplayPosition;
        }

        public ModelDisplayPosition getModelDisplayPosition() {
            return modelDisplayPosition;
        }

        public Coordinates3D getDefaultTranslate() {
            return defaultTranslate;
        }

        public double getScale() {
            return scale;
        }

        public boolean isLiteralFlipped() {
            return literalFlipped;
        }

        public boolean yIsZAxis() {
            return yIsZAxis;
        }

    }

    public static class PlayerModelItem {

        private PlayerModelItemPosition position;
        private String modelKey;
        private Map<ModelOverrideType, Float> predicate;
        private boolean enchanted;
        private Map<String, TextureResource> providedTextures;
        private TintIndexData tintIndexData;

        public PlayerModelItem(PlayerModelItemPosition position, String modelKey, Map<ModelOverrideType, Float> predicate, boolean enchanted, Map<String, TextureResource> providedTextures, TintIndexData tintIndexData) {
            this.position = position;
            this.modelKey = modelKey;
            this.predicate = predicate;
            this.enchanted = enchanted;
            this.providedTextures = providedTextures;
            this.tintIndexData = tintIndexData;
        }

        public PlayerModelItemPosition getPosition() {
            return position;
        }

        public String getModelKey() {
            return modelKey;
        }

        public Map<ModelOverrideType, Float> getPredicate() {
            return predicate;
        }

        public boolean isEnchanted() {
            return enchanted;
        }

        public Map<String, TextureResource> getProvidedTextures() {
            return providedTextures;
        }

        public TintIndexData getTintIndexData() {
            return tintIndexData;
        }

    }

    public static class RenderResult {

        private BufferedImage image;
        private String rejectedReason;
        private BlockModel model;

        public RenderResult(BufferedImage image, BlockModel model) {
            this.image = image;
            this.rejectedReason = null;
            this.model = model;
        }

        public RenderResult(String rejectedReason, BlockModel model) {
            this.image = null;
            this.rejectedReason = rejectedReason;
            this.model = model;
        }

        public boolean isSuccessful() {
            return image != null;
        }

        public BufferedImage getImage() {
            return ImageUtils.copyImage(image);
        }

        public String getRejectedReason() {
            return rejectedReason;
        }

        public BlockModel getModel() {
            return model;
        }

        public boolean hasModel() {
            return model != null;
        }

    }

}
