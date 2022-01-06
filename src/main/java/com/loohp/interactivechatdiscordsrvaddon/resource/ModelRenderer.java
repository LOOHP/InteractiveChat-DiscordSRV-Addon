package com.loohp.interactivechatdiscordsrvaddon.resource;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.loohp.blockmodelrenderer.render.Hexahedron;
import com.loohp.blockmodelrenderer.render.Model;
import com.loohp.blockmodelrenderer.render.Point3D;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registies.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.Coordinates3D;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelDisplay;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelElement;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelElement.ModelElementRotation;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelFace;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelFace.ModelFaceSide;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.TextureUV;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureAnimation;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureMeta;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils;

public class ModelRenderer implements AutoCloseable {
	
	public static final int INTERNAL_W = 64;
	public static final int INTERNAL_H = 64;
	
	public static final int QUALITY_THRESHOLD = 70;
	
	public static final int SKIN_RESOLUTION = 1600;
	public static final int TEXTURE_RESOLUTION = 800;
	
	public static final String CACHE_KEY = "ModelRender";
	public static final String MODEL_NOT_FOUND = "notfound";
	
	private static final double[] OVERLAY_ADDITION_FACTORS = new double[6];
	
	private static final String PLAYER_MODEL_RESOURCELOCATION = ResourceRegistry.BUILTIN_ENTITY_LOCATION + "player_model";
	private static final String PLAYER_MODEL_SLIM_RESOURCELOCATION = ResourceRegistry.BUILTIN_ENTITY_LOCATION + "player_model_slim";
	
	static {
		Arrays.fill(OVERLAY_ADDITION_FACTORS, ImageGeneration.ENCHANTMENT_GLINT_FACTOR);
	}
	
	private ExecutorService executor;
	private AtomicBoolean isValid;
	
	public ModelRenderer(int threads) {
		this.isValid = new AtomicBoolean(true);
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("InteractiveChatDiscordSRVAddon Async Model Processing Thread #%d").build();
		this.executor = new ThreadPoolExecutor(0, threads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), factory);
	}
	
	@Override
	public synchronized void close() {
		isValid.set(false);
		executor.shutdown();
	}
	
	public RenderResult renderPlyer(int width, int height, ResourceManager manager, boolean slim, String helmetModelKey, Map<ModelOverrideType, Float> helmetPredicate, boolean helmetEnchanted, Map<String, TextureResource> providedTextures) {
		BlockModel helmetBlockModel = helmetModelKey == null ? null : manager.getModelManager().resolveBlockModel(helmetModelKey, helmetPredicate);
		Model helmetRenderModel = null;
		if (helmetBlockModel != null) {
			if (helmetBlockModel.getRawParent() == null || helmetBlockModel.getRawParent().indexOf("/") < 0) {
				helmetRenderModel = generateStandardRenderModel(helmetBlockModel, manager, providedTextures, helmetEnchanted, false);
			} else if (helmetBlockModel.getRawParent().equals(ModelManager.ITEM_BASE)) {
				BufferedImage image = new BufferedImage(INTERNAL_W, INTERNAL_H, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				for (int i = 0; helmetBlockModel.getTextures().containsKey(ModelManager.ITEM_BASE_LAYER + i); i++) {
					String resourceLocation = helmetBlockModel.getTextures().get(ModelManager.ITEM_BASE_LAYER + i);
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
				if (helmetEnchanted) {
					image = ImageGeneration.getEnchantedImage(image);
				}
				helmetRenderModel = generateItemRenderModel(16, 16, 16, image);
			}
		}
		BlockModel playerModel = manager.getModelManager().resolveBlockModel(slim ? PLAYER_MODEL_SLIM_RESOURCELOCATION : PLAYER_MODEL_RESOURCELOCATION, Collections.emptyMap());
		if (playerModel == null) {
			return new RenderResult(MODEL_NOT_FOUND, null);
		}
		Model playerRenderModel = generateStandardRenderModel(playerModel, manager, providedTextures, false, true);
		if (helmetRenderModel != null) {
			helmetRenderModel.translate(-16 / 2, -16 / 2, -16 / 2);
			ModelDisplay displayData = helmetBlockModel.getDisplay(ModelDisplayPosition.HEAD);
			if (displayData != null) {
				Coordinates3D scale = displayData.getScale();
				helmetRenderModel.scale(scale.getX(), scale.getY(), scale.getZ());
				Coordinates3D rotation = displayData.getRotation();
				helmetRenderModel.rotate(rotation.getX(), rotation.getY(), rotation.getZ(), false);
				Coordinates3D transform = displayData.getTranslation();
				helmetRenderModel.translate(transform.getX(), transform.getY(), transform.getZ());
			}
			double scale = 0.62;
			helmetRenderModel.scale(scale, scale, scale);
			helmetRenderModel.translate((16 * scale) / 2, (16 * scale) / 2, (16 * scale) / 2);
			helmetRenderModel.translate(3.05, 22.05, 3.05);
			playerRenderModel.append(helmetRenderModel);
		}
		playerRenderModel.translate(-16 / 2, -16 / 2, -16 / 2);
		playerRenderModel.rotate(0, 180, 0, false);
		playerRenderModel.translate(16 / 2, 16 / 2, 16 / 2);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		renderPlayerModel(playerRenderModel, image);
		return new RenderResult(image, null);
	}
	
	public RenderResult render(int width, int height, ResourceManager manager, String modelKey, ModelDisplayPosition displayPosition) {
		return render(width, height, manager, modelKey, displayPosition, Collections.emptyMap());
	}
	
	public RenderResult render(int width, int height, ResourceManager manager, String modelKey, ModelDisplayPosition displayPosition, Map<ModelOverrideType, Float> predicate) {
		return render(width, height, manager, modelKey, displayPosition, predicate, Collections.emptyMap());
	}
	
	public RenderResult render(int width, int height, ResourceManager manager, String modelKey, ModelDisplayPosition displayPosition, Map<ModelOverrideType, Float> predicate, Map<String, TextureResource> providedTextures) {
		return render(width, height, manager, modelKey, displayPosition, predicate, providedTextures, false);
	}
	
	public RenderResult render(int width, int height, ResourceManager manager, String modelKey, ModelDisplayPosition displayPosition, Map<ModelOverrideType, Float> predicate, Map<String, TextureResource> providedTextures, boolean enchanted) {
		String cacheKey = CACHE_KEY + "/" + modelKey + "/" + predicate.entrySet().stream().map(entry -> entry.getKey().name().toLowerCase() + ":" + entry.getValue().toString()).collect(Collectors.joining(";")) + "/" + providedTextures.entrySet().stream().map(entry -> entry.getKey() + ":" + (entry.getValue().isTexture() ? hash(entry.getValue().getTexture()) : "null")).collect(Collectors.joining(":")) + "/" + enchanted;
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
		if (blockModel.getRawParent() == null || blockModel.getRawParent().indexOf("/") < 0) {
			renderBlockModel(generateStandardRenderModel(blockModel, manager, providedTextures, enchanted, false), image, blockModel.getDisplay(displayPosition));
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
		double intervalX = (1.0 / (double) image.getWidth()) * width;
		double intervalY = (1.0 / (double) image.getHeight()) * height;
		double z = depth / 2 - 0.5;
		List<Hexahedron> hexahedrons = new ArrayList<>();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = new Color(image.getRGB(x, y), true);
				if (color.getAlpha() > 0) {
					BufferedImage pixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
					pixel.setRGB(0, 0, color.getRGB());
					BufferedImage[] imageArray = new BufferedImage[6];
					for (int i = 0; i < imageArray.length; i++) {
						imageArray[i] = ImageUtils.copyImage(pixel);
					}
					double scaledX = (double) x * intervalX;
					double scaledY = height - (double) y * intervalY;
					hexahedrons.add(Hexahedron.fromCorners(new Point3D(scaledX, scaledY, z), new Point3D(scaledX + intervalX, scaledY - intervalY, z + 1), false, imageArray));
				}
			}
		}
		return new Model(hexahedrons);
	}
	
	private Model generateStandardRenderModel(BlockModel blockModel, ResourceManager manager, Map<String, TextureResource> providedTextures, boolean enchanted, boolean skin) {
		Map<String, BufferedImage> cachedResize = new ConcurrentHashMap<>();
		List<ModelElement> elements = new ArrayList<>(blockModel.getElements());
		List<Hexahedron> hexahedrons = new ArrayList<>(elements.size());
		Queue<Future<Hexahedron>> tasks = new ConcurrentLinkedQueue<>();
		Iterator<ModelElement> itr = elements.iterator();
		while (itr.hasNext()) {
			ModelElement element = itr.next();
			tasks.add(executor.submit(() -> {
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
							images[i] = TintUtils.applyTint(images[i], faceData.getTintindex());
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
				} catch (InterruptedException e) {}
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
	
	private void renderPlayerModel(Model renderModel, BufferedImage image) {
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.translate(image.getWidth() / 2, (double) image.getHeight() / 4 * 3);
		g.scale(image.getWidth() / 18, image.getWidth() / 18);
		renderModel.translate(-16 / 2, -16 / 2, -16 / 2);
		renderModel.updateLightingRatio(0.7, 0.7, 0.7, 0.7, 0.7, 0.7);
		renderModel.render(image.getWidth(), image.getHeight(), g, image, false);
		g.dispose();
	}
	
	private void renderBlockModel(Model renderModel, BufferedImage image, ModelDisplay displayData) {
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.translate(image.getWidth() / 2, image.getHeight() / 2);
		g.scale(image.getWidth() / 16, image.getHeight() / 16);
		renderModel.translate(-16 / 2, -16 / 2, -16 / 2);
		if (displayData != null) {
			Coordinates3D scale = displayData.getScale();
			renderModel.scale(scale.getX(), scale.getY(), scale.getZ());
			Coordinates3D rotation = displayData.getRotation();
			renderModel.rotate(rotation.getX(), rotation.getY(), rotation.getZ(), false);
			Coordinates3D transform = displayData.getTranslation();
			renderModel.translate(transform.getX(), transform.getY(), transform.getZ());
		}
		renderModel.updateLightingRatio(0.98, 0.98, 0.608, 0.8, 0.608, 0.8);
		renderModel.render(image.getWidth(), image.getHeight(), g, image, renderModel.getComponents().size() <= QUALITY_THRESHOLD);
		g.dispose();
	}
	
	private String hash(BufferedImage image) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				sb.append(Integer.toHexString(image.getRGB(x, y)));
			}
		}
		return sb.toString();
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
