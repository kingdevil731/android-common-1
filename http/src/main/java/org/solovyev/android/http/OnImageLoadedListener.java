/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.http;

import android.graphics.Bitmap;

import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 8/17/12
 * Time: 12:06 PM
 */
public interface OnImageLoadedListener {

	// NOTE: this method will be called on the background thread => if needed use runOnUIThread
	void onImageLoaded(@Nullable Bitmap image);

	// NOTE: this method will be called on the background thread => if needed use runOnUIThread
	void setDefaultImage();

}
