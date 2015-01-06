package oul.web.tools.oauth;

import oul.web.tools.oauth.profile.Profile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author moroz
 */
public class ProfileTest {

    public ProfileTest() {
    }

    @Test(expected = IOException.class)
    public void testProfileConstructorEmptyEmailsJsonStr() throws FileNotFoundException, IOException {

        File testData = new File("target/test-classes");

        File[] jsonFiles = testData.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json.bad");
            }
        });

        for (File f : jsonFiles) {
            BufferedReader sr = new BufferedReader(new FileReader(f));

            String buf = sr.readLine();
            StringBuilder buldr = new StringBuilder();
            while (buf != null) {
                buldr.append(buf);
                buldr.append("\n");
                buf = sr.readLine();
            }

            Profile profile = new Profile(buldr.toString());
        }
    }

    @Test
    public void testProfileConstructorJsonStr() throws FileNotFoundException, IOException {

        File testData = new File("target/test-classes");

        File[] jsonFiles = testData.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });

        for (File f : jsonFiles) {
            BufferedReader sr = new BufferedReader(new FileReader(f));

            String buf = sr.readLine();
            StringBuilder buldr = new StringBuilder();
            while (buf != null) {
                buldr.append(buf);
                buldr.append("\n");
                buf = sr.readLine();
            }

            System.out.print("load file " + f.getName());

            Profile profile = new Profile(buldr.toString());
            assertNotNull(profile);
            assertNotNull(profile.getId());
            assertNotNull(profile.getDomain());
            assertNotNull(profile.getName());
            assertNotNull(profile.getEmails());
            assertTrue(!profile.getEmails().isEmpty());

        }
    }

 
}
