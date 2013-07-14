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

#include "js/js_advertisement.h"
#include "platform/advertisement.h"

using namespace v8;

Handle<Value> js_load_interstitial(const Arguments &args) {
	load_interstitial();

	return Undefined();
}

Handle<Value> js_show_interstitial(const Arguments &args) {
	show_interstitial();

	return Undefined();
}

Handle<ObjectTemplate> js_advertisement_get_template() {
	Handle<ObjectTemplate> advertisement = ObjectTemplate::New();
	advertisement->Set(STRING_CACHE_loadInterstitial, FunctionTemplate::New(js_load_interstitial));
	advertisement->Set(STRING_CACHE_showInterstitial, FunctionTemplate::New(js_show_interstitial));

	return advertisement;
}
