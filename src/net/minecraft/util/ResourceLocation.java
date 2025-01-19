package net.minecraft.util;

import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ResourceLocation {
    private final int dummy;
    protected final String resourceDomain;
    protected final String resourcePath;

    private static final Map<String, ResourceLocation> CACHE = new ConcurrentHashMap<>();

    protected ResourceLocation(int dummy, String... resourceName) {
        this.dummy = dummy;
        this.resourceDomain = (resourceName[0] == null || resourceName[0].isEmpty()) ? "minecraft" : resourceName[0].toLowerCase();
        this.resourcePath = resourceName[1];
        Validate.notNull(this.resourcePath, "Resource path cannot be null!");
    }

    public ResourceLocation(String resourceName) {
        this(0, splitObjectName(resourceName));
    }

    public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
        this(0, resourceDomainIn, resourcePathIn);
    }

    protected static String[] splitObjectName(String toSplit) {
        int i = toSplit.indexOf(':');
        if (i < 0) {
            return new String[] {null, toSplit};
        }
        String domain = i > 1 ? toSplit.substring(0, i).toLowerCase() : null;
        String path = toSplit.substring(i + 1);
        return new String[] {domain, path};
    }

    public static ResourceLocation getCachedResource(String resourceName) {
        return CACHE.computeIfAbsent(resourceName, ResourceLocation::new);
    }

    @Override
    public String toString() {
        return this.resourceDomain + ':' + this.resourcePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResourceLocation)) {
            return false;
        }
        ResourceLocation other = (ResourceLocation) obj;
        return resourceDomain.equals(other.resourceDomain) && resourcePath.equals(other.resourcePath);
    }

    @Override
    public int hashCode() {
        int result = resourceDomain.hashCode();
        result = 31 * result + resourcePath.hashCode();
        return result;
    }
}
