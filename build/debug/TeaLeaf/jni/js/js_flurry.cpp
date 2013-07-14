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

#include "js/js_flurry.h"
#include "platform/flurry.h"

using namespace v8;

Handle<Value> js_flurry_log_event(const Arguments &args) {
	String::Utf8Value str(args[0]);
	const char *eventName = ToCString(str);
	bool isTimed = args[1]->IsTrue();

	flurry_log_event(eventName, isTimed);

	return Undefined();
}

Handle<Value> js_flurry_end_timed_event(const Arguments &args) {
	String::Utf8Value str(args[0]);
	const char *eventName = ToCString(str);

	flurry_end_timed_event(eventName);

	return Undefined();
}

Handle<ObjectTemplate> js_flurry_get_template() {
	Handle<ObjectTemplate> flurry = ObjectTemplate::New();

	flurry->Set(STRING_CACHE_logEvent, FunctionTemplate::New(js_flurry_log_event));
	flurry->Set(STRING_CACHE_endTimedEvent, FunctionTemplate::New(js_flurry_end_timed_event));

	return flurry;
}
