import com.season.MycloudSsoApplication;
import org.jasypt.digest.StringDigester;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MycloudSsoApplication.class})// 指定启动类
public class JasyptTest {

    @Autowired
    StringEncryptor stringEncryptor;

    StringDigester digester;

    @Test
    public void encryptPwd() {
        String result = stringEncryptor.encrypt("789654123");
        System.out.println(result);
        System.out.println(digester.digest(result));
    }
}
