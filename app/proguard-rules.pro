# volley
-dontwarn com.android.volley.**
-keep class com.android.volley.** { *; }

# ormlite
-dontwarn com.j256.ormlite.**
-keep class com.j256.ormlite.** { *; }

# zxing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# AgentWeb
-dontwarn com.just.library.**
-keep class com.just.library.** { *; }

# ViewHolder
-keepclassmembers class * extends **.radapter.RViewHolder {
    public <init>(android.view.View);
}