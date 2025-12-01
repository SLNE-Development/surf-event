package dev.slne.surf.event.buildit.random;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SecureRandomHolder {

  private static SecureRandom secureRandom;

  @Contract(value = " -> fail", pure = true)
  private SecureRandomHolder() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static SecureRandom getSecureRandom() {
    if (secureRandom == null) {
      secureRandom = createSecureRandom();
    }
    return secureRandom;
  }

  private static @NotNull SecureRandom createSecureRandom() {
    try {
      return SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to create secure random instance", e);
    }
  }
}
