package tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by sambaumgarten on 4/3/16
 */
public class TemporaryDirectoryManagerTest {
    @BeforeMethod
    public void setUp() throws Exception {
        if (TemporaryDirectoryManager.exists()) TemporaryDirectoryManager.delete();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (TemporaryDirectoryManager.exists()) TemporaryDirectoryManager.delete();
    }

    @Test
    public void testTDM() {
        assertEquals(TemporaryDirectoryManager.exists(), false);

        assertTrue(TemporaryDirectoryManager.create());

        assertEquals(TemporaryDirectoryManager.exists(), true);

        assertTrue(TemporaryDirectoryManager.delete());

        assertEquals(TemporaryDirectoryManager.exists(), false);
    }

    @Test
    public void testCreation() throws Exception {
        TemporaryDirectoryManager temporaryDirectoryManager = new TemporaryDirectoryManager();
    }
}