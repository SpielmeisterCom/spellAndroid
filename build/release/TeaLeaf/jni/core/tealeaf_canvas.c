/* @license
 * This file is part of the Game Closure SDK.
 *
 * The Game Closure SDK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 
 * The Game Closure SDK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with the Game Closure SDK.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @file	 tealeaf_canvas.c
 * @brief
 */
#include "core/tealeaf_canvas.h"

#include "core/texture_2d.h"
#include "core/texture_manager.h"
#include "core/tealeaf_context.h"
#include "core/draw_textures.h"
#include "core/config.h"
#include "core/log.h"
#include "geometry.h"

static tealeaf_canvas canvas;

/**
 * @name	tealeaf_canvas_get
 * @brief
 * @retval	tealeaf_canvas* -
 */
tealeaf_canvas *tealeaf_canvas_get() {
	return &canvas;
}

/**
 * @name	tealeaf_canvas_init
 * @brief	initilizes the tealeaf canvas object
 * @param	framebuffer_name - (int) gl id of the onscreen framebuffer
 * @retval	NONE
 */
void tealeaf_canvas_init(int framebuffer_name) {
	LOG("{canvas} Initializing Canvas");

	int width = config_get_screen_width();
	int height = config_get_screen_height();
	GLuint offscreen_buffer_name;
	glGenFramebuffers(1, &offscreen_buffer_name);
	canvas.offscreen_framebuffer = offscreen_buffer_name;
	canvas.view_framebuffer = framebuffer_name;
	canvas.onscreen_ctx = context_2d_init(&canvas, "onscreen", -1, true);
	canvas.onscreen_ctx->width = width;
	canvas.onscreen_ctx->height = height;
}

/**
 * @name	tealeaf_canvas_bind_texture_buffer
 * @brief	binds the given context's texture backing to gl to draw to
 * @param	ctx - (context_2d *) pointer to the context to bind
 * @retval	NONE
 */
void tealeaf_canvas_bind_texture_buffer(context_2d *ctx) {
	texture_2d *tex = texture_manager_get_texture(texture_manager_get(), ctx->url);

	if (!tex) {
		return;
	}

	glBindTexture(GL_TEXTURE_2D, tex->name);
	glFinish();
	glBindFramebuffer(GL_FRAMEBUFFER, canvas.offscreen_framebuffer);
	glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex->name, 0);
	canvas.framebuffer_width = tex->originalWidth;
	canvas.framebuffer_height = tex->originalHeight;
	canvas.framebuffer_offset_bottom = tex->height - tex->originalHeight;
}

/**
 * @name	tealeaf_canvas_bind_render_buffer
 * @brief	bind's the render buffer and set's it's height / width to the given context's props
 * @param	ctx - (context_2d *) pointer to the context to use the width / height from
 * @retval	NONE
 */
void tealeaf_canvas_bind_render_buffer(context_2d *ctx) {
	glBindFramebuffer(GL_FRAMEBUFFER, canvas.view_framebuffer);
	canvas.framebuffer_width = ctx->width;
	canvas.framebuffer_height = ctx->height;
	canvas.framebuffer_offset_bottom = 0;
}

/**
 * @name	tealeaf_canvas_context_2d_bind
 * @brief	uses the given texture to bind to either the render buffer or a fbo
 * @param	ctx - (context_2d *) pointer to the context to use for binding
 * @retval	bool - whether the bind failed or succeeded
 */
bool tealeaf_canvas_context_2d_bind(context_2d *ctx) {
	if (canvas.active_ctx != ctx) {
		canvas.active_ctx = ctx;
		draw_textures_flush();

		if (ctx->on_screen) {
			tealeaf_canvas_bind_render_buffer(ctx);
		} else {
			tealeaf_canvas_bind_texture_buffer(ctx);
		}

		tealeaf_context_update_viewport(ctx, false);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			LOG("{canvas} WARNING: Failed to make complete framebuffer %i", glCheckFramebufferStatus(GL_FRAMEBUFFER));
		}

		return true;
	} else {
		return false;
	}
}

/**
 * @name	tealeaf_canvas_resize
 * @brief	resize's the onscreen canvas
 * @param	w - (int) width to resize to
 * @param	h - (int) height to resize to
 * @retval	NONE
 */
void tealeaf_canvas_resize(int w, int h) {
	LOG("{canvas} Resizing screen to (%d, %d)", w, h);

	context_2d *ctx = canvas.onscreen_ctx;
	tealeaf_context_resize(ctx, w, h);

	if (canvas.active_ctx == canvas.onscreen_ctx) {
		tealeaf_canvas_bind_render_buffer(ctx);
		tealeaf_context_update_viewport(ctx, true);
		context_2d_clear(ctx);
	}

	glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
	glEnable(GL_BLEND);
	config_set_screen_width(w);
	config_set_screen_height(h);
	canvas.should_resize = true;
}

