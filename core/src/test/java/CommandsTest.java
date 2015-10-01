import com.antonioaltieri.telegram.botapi.types.Message;
import com.antonioaltieri.telegram.mugiwara.bot.objects.Formazione;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio on 11/09/2015.
 */
public class CommandsTest {
    @Test
    public void estraiFormazioneTest(){
        String txtForm="#formazioneguerra \n 1. @asdamp\n2. @scarlet\n3.@falella";
        Formazione f=new Formazione(txtForm);
        System.out.println(f.toString());
    }


}
