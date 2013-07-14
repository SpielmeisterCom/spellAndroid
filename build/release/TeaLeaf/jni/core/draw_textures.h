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

#ifndef DRAW_TEXTURES_H
#define DRAW_TEXTURES_H

#include "geometry.h"
#include "rgba.h"

#ifdef __cplusplus
extern "C" {
#endif

void draw_textures_flush();
void draw_textures_item(const matrix_3x3 *model_view, int name, int src_width, int src_height, int orig_width, int orig_height, rect_2d src, rect_2d dest, rect_2d clip, float opacity, int composite_op, rgba *filter_color, int filter_type);
void draw_textures_init();

#ifdef __cplusplus
}
#endif

#endif
