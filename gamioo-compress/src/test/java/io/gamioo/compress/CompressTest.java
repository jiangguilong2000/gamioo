package io.gamioo.compress;

import com.github.luben.zstd.Zstd;
import io.gamioo.benchmark.Benchmark;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;

@DisplayName("压缩测试")
@TestMethodOrder (MethodOrderer.OrderAnnotation.class)
public class CompressTest {
	private static final Logger logger = LogManager.getLogger(CompressTest.class);
	private final Benchmark benchmark=new Benchmark(10000);
	private static byte[] array;
	private static byte[] compressArray;

	@BeforeAll
	public static void beforeAll() throws Exception {
		try {
			// 获取URL
			URL url = CompressTest.class.getClassLoader().getResource("mini.txt");
			// 通过url获取File的绝对路径
			File file = new File(url.getFile());
			array = FileUtils.readFileToByteArray(file);
			logger.debug("data size={}",array.length);

//					 try (InputStream stream = EmojiLoader.class.getResourceAsStream(PATH)) {
//						 
//				            return JSON.parseArray(StringUtils.readString(stream), klass);
//				        } catch (IOException e) {
//				            return Collections.emptyList();
//				        }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@BeforeEach
	public void beforeEach() throws Exception {
	//	System.out.printf("array size={},compress array={}", array.length,compressArray.length);
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		logger.debug("array size={},compress array size={},Compression Ratio={}", array.length,compressArray.length,array.length*1f/compressArray.length);
	}
	
//	@ParameterizedTest
//	@ValueSource(strings = {"one", "two", "three"})
//	@DisplayName("参数化测试")
//	public void parameterizedTest(String string) {
//	    System.out.println(string);
//	    Assertions.assertTrue(StringUtils.isNotBlank(string));
//	}

	@Test
	@DisplayName("Zstandard压缩")
	@Order(1)
	public void zstandardCompress() throws Exception {

		benchmark.run("Zstandard compress function test", () -> {
			compressArray = Zstd.compress(array);
		});
	}

	@Test
	@DisplayName("Zstandard解压缩")
	@Order(2)
	public void zstandarDecompress() throws Exception {
		benchmark.run("Zstandard decompress function test", () -> {
			if (compressArray != null) {
				int size = (int) Zstd.decompressedSize(compressArray);
				array = new byte[size];
				Zstd.decompress(array, compressArray);
			}

		});
	}

	@Test
	@DisplayName("Zlib压缩")
	@Order(3)
	public void zlibCompress() throws Exception {

		benchmark.run("zlib compress function test", () -> {
			// int length=array.length;
			compressArray = ZlibUtil.compress(array);
		});
	}
	
	@Test
	@DisplayName("Zlib解压缩")
	@Order(4)
	//@RepeatedTest(value = 3, name = "{displayName} 第 {currentRepetition}/{totalRepetitions} 次")
	public void zlibDecompress() throws Exception {
		benchmark.run("zlib decompress function test", () -> {
			if (compressArray != null) {
				array=ZlibUtil.uncompress(compressArray);
			}

		});
	}

	@AfterAll
	public static void afterAll() throws Exception {

		array = null;
		compressArray = null;
	}
}
