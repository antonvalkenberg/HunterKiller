package hunterkiller;

import org.junit.BeforeClass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;

public class HunterKillerTest {

	@BeforeClass
	public static void setupFiles() {
		Gdx.graphics = new MockGraphics();
		Gdx.net = new HeadlessNet();
		Gdx.files = new HeadlessFiles();
	}

}
