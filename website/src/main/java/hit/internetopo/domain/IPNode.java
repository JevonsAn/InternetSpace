package hit.internetopo.domain;

public class IPNode {
    private String ip;
    private int asn;
    private String isDest;

    public IPNode(String ip, int asn, String isDest) {
        this.ip = ip;
        this.asn = asn;
        this.isDest = isDest;
    }

    public String getIp() {
        return ip;
    }

    public int getAsn() {
        return asn;
    }

    public String getIsDest() {
        return isDest;
    }
}