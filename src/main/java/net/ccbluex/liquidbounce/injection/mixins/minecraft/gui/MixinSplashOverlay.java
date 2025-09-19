/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2025 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.injection.mixins.minecraft.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.ccbluex.liquidbounce.common.ClientLogoTexture;
import net.ccbluex.liquidbounce.common.RenderLayerExtensions;
import net.ccbluex.liquidbounce.event.EventManager;
import net.ccbluex.liquidbounce.event.events.ScreenRenderEvent;
import net.ccbluex.liquidbounce.features.misc.HideAppearance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * LiquidBounce Splash Screen
 */
@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private static final IntSupplier CLIENT_ARGB = () -> ColorHelper.getArgb(255, 24, 26, 27);

    @Unique
    private ButtonWidget skipButton;

    @Unique
    private boolean hasAutoAdvanced = false;

    @Unique
    private boolean skipButtonInitialized = false;

    @Inject(method = "init", at = @At("RETURN"))
    private static void initializeTexture(TextureManager textureManager, CallbackInfo ci) {
        textureManager.registerTexture(ClientLogoTexture.CLIENT_LOGO, new ClientLogoTexture());
    }

    @Unique
    private boolean wasMousePressed = false;
    
    @Unique
    private boolean wasMousePressedInsideButton = false;

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        EventManager.INSTANCE.callEvent(new ScreenRenderEvent(context, delta));
        
        // Initialize skip button on first render if not hiding appearance and texture manager is ready
        if (!skipButtonInitialized && !HideAppearance.INSTANCE.isHidingNow() && client.getTextureManager() != null) {
            try {
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();
                
                skipButton = ButtonWidget.builder(ScreenTexts.PROCEED, button -> advanceToGame())
                        .dimensions(width / 2 - 100, height / 4 + 120 + 12, 200, 20)
                        .build();
                skipButtonInitialized = true;
            } catch (Exception e) {
                // If button creation fails due to texture issues, we'll try again next frame
            }
        }
        
        // Render skip button if not hiding appearance and button is ready
        if (!HideAppearance.INSTANCE.isHidingNow() && skipButton != null) {
            try {
                skipButton.render(context, mouseX, mouseY, delta);
                
                // Simple mouse click detection for the button
                boolean isMousePressed = org.lwjgl.glfw.GLFW.glfwGetMouseButton(
                    client.getWindow().getHandle(), 
                    org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT
                ) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
                
                boolean isMouseOverButton = skipButton.isMouseOver(mouseX, mouseY);
                
                // Track if mouse was pressed while over the button
                if (!wasMousePressed && isMousePressed && isMouseOverButton) {
                    wasMousePressedInsideButton = true;
                }
                
                // Detect click (press and release both inside button)
                if (wasMousePressed && !isMousePressed && isMouseOverButton && wasMousePressedInsideButton) {
                    advanceToGame();
                }
                
                // Reset when mouse is released
                if (!isMousePressed) {
                    wasMousePressedInsideButton = false;
                }
                
                wasMousePressed = isMousePressed;
            } catch (Exception e) {
                // If button rendering fails due to texture atlas issues, skip it this frame
                // This prevents crashes during early loading phases
            }
        }
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V"))
    private boolean drawMojangLogo(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
        return HideAppearance.INSTANCE.isHidingNow();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;getProgress()F"))
    private void drawClientLogo(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        // Don't draw the logo if the appearance is hidden
        if (HideAppearance.INSTANCE.isHidingNow()) {
            return;
        }

        // Check if texture manager is ready to prevent atlas initialization crashes
        if (client.getTextureManager() == null) {
            return;
        }

        // Ensure texture is registered and available before drawing
        try {
            // Check if our texture exists in the texture manager
            var texture = client.getTextureManager().getTexture(ClientLogoTexture.CLIENT_LOGO);
            if (texture == null) {
                return;
            }

            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();

            float scaleFactor = Math.min(screenWidth * 0.4f / ClientLogoTexture.WIDTH, screenHeight * 0.25f / ClientLogoTexture.HEIGHT);

            int displayWidth = (int)(ClientLogoTexture.WIDTH * scaleFactor);
            int displayHeight = (int)(ClientLogoTexture.HEIGHT * scaleFactor);

            int x = (screenWidth - displayWidth) / 2;
            int y = (screenHeight - displayHeight) / 2;

            // Use the same color as the original brand color
            int color = ColorHelper.getArgb(255, 24, 26, 27);

            // Use standard texture layer instead of custom render layer to avoid sprite atlas issues
            context.drawTexture(
                    RenderLayer::getGuiTextured,
                    ClientLogoTexture.CLIENT_LOGO,
                    x,
                    y,
                    0.0F,
                    0.0F,
                    displayWidth,
                    displayHeight,
                    ClientLogoTexture.WIDTH,
                    ClientLogoTexture.HEIGHT
            );
        } catch (Exception e) {
            // Silently fail if texture system isn't ready yet - this prevents crashes during early loading
            // The logo simply won't be shown until the system is ready
        }
    }

    @Unique
    private float lastProgress = 0.0f;

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;getProgress()F"))
    private float captureProgress(float progress) {
        lastProgress = progress;
        return progress;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;getProgress()F", shift = At.Shift.AFTER))
    private void checkAutoAdvance(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        // Auto-advance when progress reaches 100% (1.0f)
        if (!hasAutoAdvanced && lastProgress >= 1.0f) {
            hasAutoAdvanced = true;
            advanceToGame();
        }
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;BRAND_ARGB:Ljava/util/function/IntSupplier;"))
    private IntSupplier withClientColor(IntSupplier original) {
        return HideAppearance.INSTANCE.isHidingNow() ? original : CLIENT_ARGB;
    }

    @Unique
    private void advanceToGame() {
        // Advance to the main game, similar to how DownloadingTerrainScreen proceeds
        if (client != null) {
            client.setScreen(null);
        }
    }

}


