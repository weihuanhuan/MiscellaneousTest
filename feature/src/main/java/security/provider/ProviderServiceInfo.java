package security.provider;

/**
 * Created by JasonFitch on 10/12/2020.
 */
public class ProviderServiceInfo {

    String provider;
    String type;
    String algorithm;

    public ProviderServiceInfo(String provider, String type, String algorithm) {
        this.provider = provider;
        this.type = type;
        this.algorithm = algorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderServiceInfo)) return false;

        ProviderServiceInfo that = (ProviderServiceInfo) o;

        if (!provider.equals(that.provider)) return false;
        if (!type.equals(that.type)) return false;
        return algorithm.equals(that.algorithm);
    }

    @Override
    public int hashCode() {
        int result = provider.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + algorithm.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return provider + ":" + type + ":" + algorithm;
    }


}
