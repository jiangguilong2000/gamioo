package io.gamioo.compress;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.luben.zstd.Zstd;

import io.gamioo.benchmark.Benchmark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZstandardTest {
	 private static Benchmark benchmark;
	 private static byte[] array;
	 private static byte[] compressArray;
	 @BeforeClass
	 public static void beforeAll() throws Exception{
		 benchmark = new Benchmark(10000);
		try {
			// 获取URL
						URL url = ZstandardTest.class.getClassLoader().getResource("readu.txt");
						// 通过url获取File的绝对路径
						File file = new File(url.getFile());
					 array=FileUtils.readFileToByteArray(file);
					 System.out.println(array.length);
					 
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
	 
	 @Test
	    public void a() throws Exception {
		
		 benchmark.run("compress function test", () -> {
			// int length=array.length;
			 compressArray=Zstd.compress(array);
		 });
		
		
	    //   
	    }

	 
	 @Test
	    public void b() throws Exception {
	     benchmark.run("decompress function test", () -> {
	    	 if(compressArray!=null) {
	    		 int size= (int)Zstd.decompressedSize(compressArray);
				 byte[] target =new byte[size];
				 Zstd.decompress(target, compressArray);
	    	 }
	    	 
	    	 
	     });
	    }
	 
	 @AfterClass
	 public static void afterAll() throws Exception{
		
		 array=null;
		 compressArray=null;
	 }
}
