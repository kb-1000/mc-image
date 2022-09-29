/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.mc;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import com.oracle.svm.core.annotate.*;
import de.kb1000.mcimage.util.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

// TODO: this is MC version specific
@TargetClass(className = "net/minecraft/unmapped/C_atzfxoih", classNameProvider = MinecraftClassNameProvider.class, onlyWith = Environment.ClientOnly.class)
final class Target_RealmsTextureManager {
    @Alias
    @TargetElement(name = "f_qxtojezw")
    static Map<String, RealmsTexture> TEXTURES;
    @Alias
    @TargetElement(name = "f_xurhhxcg")
    static Map<String, Boolean> SKIN_FETCH_STATUS;
    @Alias
    @TargetElement(name = "f_psrpwdsj")
    static Map<String, byte[]> FETCHED_SKINS;
    @Alias
    @TargetElement(name = "f_egewkiul")
    static Logger LOGGER;
    @Alias
    @TargetElement(name = "f_rgamgian")
    static Target_Identifier ISLES;

    @Substitute
    @TargetElement(name = "m_eayjzxtv")
    static void bindWorldTemplate(String id, @Nullable String image) {
        if (image == null) {
            Target_RenderSystem.setShaderTexture(0, ISLES);
        } else {
            java.util.Base64.getDecoder().decode(image);
            int i = RealmsTextureManagerSupport.getTextureId(id, Base64.getDecoder().decode(image));
            RenderSystem.setShaderTexture(0, i);
        }
    }

    @Alias
    @TargetElement(name = "m_gbixadgi")
    static native void bindDefaultFace(UUID uuid);

    @Substitute
    @TargetElement(name = "m_ycivhwow")
    static void bindFace(final String uuidStr) {
        UUID uuid = UUIDTypeAdapter.fromString(uuidStr);
        if (TEXTURES.containsKey(uuidStr)) {
            int i = TEXTURES.get(uuidStr).textureId;
            RenderSystem.setShaderTexture(0, i);
        } else if (SKIN_FETCH_STATUS.containsKey(uuidStr)) {
            if (!SKIN_FETCH_STATUS.get(uuidStr)) {
                bindDefaultFace(uuid);
            } else if (FETCHED_SKINS.containsKey(uuidStr)) {
                int j = RealmsTextureManagerSupport.getTextureId(uuidStr, FETCHED_SKINS.get(uuidStr));
                RenderSystem.setShaderTexture(0, j);
            } else {
                bindDefaultFace(uuid);
            }

        } else {
            SKIN_FETCH_STATUS.put(uuidStr, false);
            bindDefaultFace(uuid);
            Thread thread = new Thread("Realms Texture Downloader") {
                public void run() {
                    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Target_RealmsUtil.getTextures(uuidStr);
                    if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                        MinecraftProfileTexture minecraftProfileTexture = map.get(MinecraftProfileTexture.Type.SKIN);
                        String url = minecraftProfileTexture.getUrl();
                        HttpURLConnection httpURLConnection = null;
                        LOGGER.debug("Downloading http texture from {}", url);

                        try {
                            httpURLConnection = (HttpURLConnection) new URL(url).openConnection(Target_MinecraftClient.getInstance().getNetworkProxy());
                            httpURLConnection.connect();
                            if (httpURLConnection.getResponseCode() / 100 == 2) {
                                Target_NativeImage nativeImage;
                                try {
                                    nativeImage = Target_NativeImage.read(httpURLConnection.getInputStream());
                                } catch (Exception e) {
                                    SKIN_FETCH_STATUS.remove(uuidStr);
                                    return;
                                } finally {
                                    IOUtils.closeQuietly(httpURLConnection.getInputStream());
                                }

                                nativeImage = RealmsTextureManagerSupport.remapTexture(nativeImage);
                                FETCHED_SKINS.put(uuidStr, nativeImage.getBytes());
                                SKIN_FETCH_STATUS.put(uuidStr, true);
                                return;
                            }

                            SKIN_FETCH_STATUS.remove(uuidStr);
                        } catch (Exception e) {
                            LOGGER.error("Couldn't download http texture", e);
                            SKIN_FETCH_STATUS.remove(uuidStr);
                        } finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }

                        }

                    } else {
                        SKIN_FETCH_STATUS.put(uuidStr, true);
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Delete
    @TargetElement(name = "m_atnqscdp")
    private static native int getTextureId(String id, String image);

    @TargetClass(className = "net/minecraft/unmapped/C_atzfxoih$C_ugsypoll", classNameProvider = MinecraftClassNameProvider.class, onlyWith = Environment.ClientOnly.class)
    @Delete
    static final class Downloader {
    }

    @TargetClass(className = "net/minecraft/unmapped/C_atzfxoih$C_wlblgnzd", classNameProvider = MinecraftClassNameProvider.class, onlyWith = Environment.ClientOnly.class)
    static final class RealmsTexture {
        @Delete
        @TargetElement(name = "f_phsegolm")
        String imageBase64;

        @Inject
        byte[] image;

        @Alias
        @TargetElement(name = "f_dbwjijgo")
        int textureId;

        @Delete
        private RealmsTexture(String image, int textureId) {
        }

        RealmsTexture(byte[] image, int textureId) {
            this.image = image;
            this.textureId = textureId;
        }
    }
}

final class RealmsTextureManagerSupport {
    static Target_NativeImage remapTexture(Target_NativeImage sourceImage) {
        boolean is32 = sourceImage.getHeight() == 32;
        Target_NativeImage destImage;
        if (is32) {
            destImage = new Target_NativeImage(64, 64, true);
            destImage.copyFrom(sourceImage);
            sourceImage.close();
            destImage.fillRect(0, 32, 64, 32, 0);
            destImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
            destImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
            destImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
            destImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
            destImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
            destImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
            destImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
            destImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
            destImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
            destImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
            destImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
            destImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
        } else {
            destImage = sourceImage;
        }

        Target_PlayerSkinTexture.stripAlpha(destImage, 0, 0, 32, 16);
        if (is32) {
            Target_PlayerSkinTexture.stripColor(destImage, 32, 0, 64, 32);
        }

        Target_PlayerSkinTexture.stripAlpha(destImage, 0, 16, 64, 32);
        Target_PlayerSkinTexture.stripAlpha(destImage, 16, 48, 48, 64);
        return destImage;
    }

    static int getTextureId(String id, byte[] image) {
        Target_RealmsTextureManager.RealmsTexture realmsTexture = Target_RealmsTextureManager.TEXTURES.get(id);
        if (realmsTexture != null && Arrays.equals(realmsTexture.image, image)) {
            return realmsTexture.textureId;
        } else {
            int i;
            if (realmsTexture != null) {
                i = realmsTexture.textureId;
            } else {
                i = GlStateManager._genTexture();
            }

            Target_NativeImage nativeImage = null;
            try {
                try {
                    ByteBuffer buf = MemoryUtil.memAlloc(image.length);
                    try {
                        buf.put(0, image);
                        nativeImage = Target_NativeImage.read(buf);
                    } finally {
                        MemoryUtil.memFree(buf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RenderSystem.activeTexture(33984);
                RenderSystem.bindTextureForSetup(i);
                if (nativeImage != null)
                    nativeImage.upload(0, 0, 0, true);
                else
                    TextureUtil.initTexture(null, 0, 0);
            } finally {
                if (nativeImage != null) {
                    nativeImage.close();
                }
            }
            Target_RealmsTextureManager.TEXTURES.put(id, new Target_RealmsTextureManager.RealmsTexture(image, i));
            return i;
        }
    }
}

