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

#ifndef TIMESTEP_VIEW_H
#define TIMESTEP_VIEW_H

#include "core/timestep/timestep.h"

timestep_view *timestep_view_init();
void timestep_view_delete(timestep_view *v);
void timestep_view_wrap_render(timestep_view *v, context_2d *ctx, JS_OBJECT_WRAPPER js_ctx, JS_OBJECT_WRAPPER js_opts);
void timestep_view_set_type(timestep_view *v, unsigned int type);

void timestep_view_wrap_tick(timestep_view *v, double dt);
void timestep_view_sort_subviews(timestep_view *v);
bool timestep_view_add_subview(timestep_view *v, timestep_view *subview);
bool timestep_view_remove_subview(timestep_view *v, timestep_view *subview);
timestep_view *timestep_view_get_superview(timestep_view *v);
void timestep_view_add_filter(timestep_view *v, rgba *color);
void timestep_view_clear_filters(timestep_view *v);

void timestep_view_add_animation(timestep_view *view, struct view_animation_t *anim);

// Shutdown timestep view subsystem
CEXPORT void timestep_view_shutdown();

#endif // TIMESTEP_VIEW_H
