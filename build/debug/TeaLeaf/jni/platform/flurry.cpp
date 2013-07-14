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

#include "platform/flurry.h"
#include "platform/platform.h"

void flurry_log_event(const char *eventName, bool isTimed) {
	native_shim *shim = get_native_shim();
	JNIEnv *env = shim->env;
	jstring jeventName = env->NewStringUTF(eventName);
	jmethodID method = env->GetMethodID(shim->type, "flurryLogEvent", "(Ljava/lang/String;Z)V");

	env->CallVoidMethod(shim->instance, method, jeventName, isTimed);
}

void flurry_end_timed_event(const char *eventName) {
	native_shim *shim = get_native_shim();
	JNIEnv *env = shim->env;
	jstring jeventName = env->NewStringUTF(eventName);
	jmethodID method = env->GetMethodID(shim->type, "flurryEndTimedEvent", "(Ljava/lang/String;)V");

	env->CallVoidMethod(shim->instance, method, jeventName);
}
