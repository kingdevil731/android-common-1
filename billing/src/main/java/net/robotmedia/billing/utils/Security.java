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

package net.robotmedia.billing.utils;

import android.content.Context;
import android.util.Log;
import net.robotmedia.billing.security.BillingSecurity;
import net.robotmedia.billing.utils.AESObfuscator.ValidationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class Security {

	private static final Set<Long> knownNonces = new HashSet<Long>();
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String TAG = Security.class.getSimpleName();

	@Nullable
	private static AESObfuscator obfuscator = null;

	@Nonnull
	private static final Object obfuscatorLock = new Object();


	/**
	 * Generate and register nonce
	 *
	 * @return nonce. This method guarantees that created nonce will be unique (i.e. there is only one instance of registered nonce)
	 */
	public static long generateNonce() {
		long nonce;

		// todo serso: optimize
		// actually there we can fo some optimization but it seems that it is not a bottleneck
		synchronized (knownNonces) {
			do {
				nonce = RANDOM.nextLong();
			} while (knownNonces.contains(nonce));
			knownNonces.add(nonce);
		}

		return nonce;
	}

	public static boolean isNonceKnown(long nonce) {
		synchronized (knownNonces) {
			return knownNonces.contains(nonce);
		}
	}

	public static void removeNonce(long nonce) {
		synchronized (knownNonces) {
			knownNonces.remove(nonce);
		}
	}

	/**
	 * Obfuscates the source string using AES algorithm with specified salt
	 *
	 * @param context context
	 * @param salt    salt to beb used for obfuscation
	 * @param source  string to be obfuscated
	 * @return obfuscated string. Null can be returned only if source string is null
	 */
	@Nullable
	public static String obfuscate(@Nonnull Context context, @Nullable byte[] salt, @Nullable String source) {
		return salt == null ? source : getObfuscator(context, salt).obfuscate(source);
	}

	@Nonnull
	private static AESObfuscator getObfuscator(@Nonnull Context context, @Nonnull byte[] salt) {
		// todo serso: optimize synchronization
		// obfuscatorLock object used only in order not to lock the whole class by synchronizing method
		synchronized (obfuscatorLock) {
			if (obfuscator == null) {

				final String password = BillingSecurity.generatePassword(context);

				obfuscator = new AESObfuscator(salt, password);
			}

			return obfuscator;
		}
	}

	/**
	 * Method unobfuscates the string using AES algorithm with specified salt.
	 *
	 * @param context    context
	 * @param salt       unobfuscation salt (must be provided the same as was used in obfuscation)
	 * @param obfuscated string to be unobfuscated
	 * @return unobfuscated string. Null returned in two cases: either obfuscated string is null or unobfuscation failed due to some errors
	 */
	@Nullable
	public static String unobfuscate(@Nonnull Context context, @Nullable byte[] salt, @Nullable String obfuscated) {
		if (salt != null) {
			final AESObfuscator obfuscator = getObfuscator(context, salt);
			try {
				return obfuscator.unobfuscate(obfuscated);
			} catch (ValidationException e) {
				Log.w(TAG, "Invalid obfuscated data or key");
			}

			return null;

		} else {
			return obfuscated;
		}
	}

}
