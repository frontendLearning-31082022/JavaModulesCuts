package MocksJava_Null;

import MethodsUniversal.ObjectArticle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

public class DeserializeHashMapSQL   extends DeserializeHashMap {

    public static ObjectArticle deserFromBytes(String bytes) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(bytes );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        ObjectArticle o  = (ObjectArticle) ois.readObject();
        ois.close();
        return o;
    }

}
