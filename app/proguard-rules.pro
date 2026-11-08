# ProGuard Rules for BCA Mobile Android Native

# Keep Kotlin & Jetpack Compose
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**

# Security: Strip all debug logging in release build
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
