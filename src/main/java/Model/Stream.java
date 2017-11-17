package Model;

public class Stream {
    public String adressSource;
    public String portSource;
    public String title;
    public Integer code;

    public Stream(String adressSource, String portSource, String title) {
        this.adressSource = adressSource;
        this.portSource = portSource;
        this.title = title;
    }

    public String getAdressSource() {
        return adressSource;
    }

    public void setAdressSource(String adressSource) {
        this.adressSource = adressSource;
    }

    public String getPortSource() {
        return portSource;
    }

    public void setPortSource(String portSource) {
        this.portSource = portSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Stream{" +
                "adressSource='" + adressSource + '\'' +
                ", portSource='" + portSource + '\'' +
                ", title='" + title + '\'' +
                ", code=" + code +
                '}';
    }
}
