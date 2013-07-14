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

#ifndef TEALEAF_CANVAS_H
#define TEALEAF_CANVAS_H

#include "platform/gl.h"
#include "core/types.h"

typedef struct context_2d_t *context_2d_p;

typedef struct tealeaf_canvas_t {
	const char *dest_tex_url;
	int framebuffer_width;
	int framebuffer_height;
	int framebuffer_offset_bottom;
	GLuint view_framebuffer;
	GLuint offscreen_framebuffer;
	GLuint depth_buffer;
	GLuint fill_rect_tex;
	bool should_resize;
	bool on_screen;
	context_2d_p onscreen_ctx;
	context_2d_p active_ctx;
} tealeaf_canvas;

#ifdef __cplusplus
extern "C" {
#endif

void tealeaf_canvas_bind_render_buffer(context_2d_p ctx);
void tealeaf_canvas_bind_texture_buffer(context_2d_p ctx);
void tealeaf_canvas_resize(int w, int h);
bool tealeaf_canvas_context_2d_bind(context_2d_p ctx);

tealeaf_canvas *tealeaf_canvas_get();
void tealeaf_canvas_init(int framebuffer_name);

#ifdef __cplusplus
}
#endif

#endif // TEALEAF_CANVAS_H
