package Model;

import java.io.File;
import java.util.ArrayList;

public class MusicCatalog {
    private String folderPath;
    private ArrayList<Music> catalog = null;

    public MusicCatalog(String folderPath) {
        this.folderPath = folderPath;
        catalog = getAllMusics();
    }

    public ArrayList<Music> getCatalog() {
        return catalog;
    }

    public void setCatalog(ArrayList<Music> catalog) {
        this.catalog = catalog;
    }

    public Music getMusic(int code){
        try{
            return catalog.get(code);
        }catch (Exception e){
            return null;
        }
    }

    public String generateCatalogToClient(){
        if(catalog.size() == 0)
            return "empty";

        String result = "";
        for (Music music: catalog) {
            result+=music.getCode()+" - "+ music.getTitle()+"#";
        }
        return result;
    }

    private ArrayList<Music> getAllMusics() {
        ArrayList<Music> musics = new ArrayList<Music>();
        int musicCode = 0;
        File folder = new File(folderPath);
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if(fileEntry.getName().contains(".mp3")) {
                    musics.add(new Music(musicCode, fileEntry.getName().replace(".mp3", ""), folderPath+""+fileEntry.getName()));
                    musicCode++;
                }
            }
        }
        return musics;
    }
}
