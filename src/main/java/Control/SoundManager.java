package Control;

import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class SoundManager {
    public void playMusic(String musicPath){
        Player player = null;
        try {
            FileInputStream fis = new FileInputStream(musicPath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
            player.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
