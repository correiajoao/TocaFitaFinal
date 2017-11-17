package Control;

public class MessageManager {

    public static String generateMessage(MessageType messageType, String ... parameters ) {
        String message = messageType.toString();

        for(String parameter: parameters){
            message += "&"+parameter ;
        }
        return message;
    }

    public static MessageType getMessageType(String message) {
        if(!message.equals("") && (message != null)) {
            String[] token = message.split("&");

            if (token[0].equals("REQUESTMUSIC")) {
                return MessageType.REQUESTMUSIC;
            } else if (token[0].equals("REQUESTSTREAM")) {
                return MessageType.REQUESTSTREAM;
            } else if (token[0].equals("REQUESTMUSICLIST")) {
                return MessageType.REQUESTMUSICLIST;
            } else if (token[0].equals("REQUESTSTREAMLIST")) {
                return MessageType.REQUESTSTREAMLIST;
            } else if (token[0].equals("NEWSTREAM")) {
                return MessageType.NEWSTREAM;
            } else if (token[0].equals("CONFIRMATION")) {
                return MessageType.CONFIRMATION;
            } else if (token[0].equals("CLOSE")) {
                return MessageType.CLOSE;
            } else if (token[0].equals("DENY")) {
                return MessageType.DENY;
            }
        }
        return MessageType.UNKNOW;
    }

    public static String[] getMessageParameters(String message) {
        return message.split("&");
    }

}
