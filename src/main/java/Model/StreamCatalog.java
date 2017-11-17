package Model;

import java.util.ArrayList;
import java.util.List;

public class StreamCatalog {

    public static Integer streamCode = 0;
    private List<Stream> streams = null;

    public StreamCatalog() {
        this.streams = new ArrayList<Stream>();
    }

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public void addStreamer(Stream stream){
        stream.setCode(streamCode);
        streams.add(streamCode, stream);
        streamCode++;
    }

    public Stream getStreamer(int code){
        try {
            return streams.get(code);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String generateCatalogToClient(){
        if(streams.size() == 0)
            return "empty";

        String result = "";
            for (Stream stream : streams) {
                result += stream.getCode() + " - " + stream.getTitle() + "#";
            }
        return  result;
    }
}
