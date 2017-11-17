package Model;

public class Music {
    public String title;
    public Integer code;
    public String musicPath;

    public Music(int code, String title, String musicPath) {
        this.title = title;
        this.code = code;
        this.musicPath = musicPath;
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

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", code=" + code +
                ", musicPath='" + musicPath + '\'' +
                '}';
    }
}
