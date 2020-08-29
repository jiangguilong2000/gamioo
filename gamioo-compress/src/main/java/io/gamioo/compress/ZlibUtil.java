package io.gamioo.compress;

import io.gamioo.core.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
public class ZlibUtil
{
	private static final Logger logger = LogManager.getLogger(ZlibUtil.class);
	
	public static byte[] compress(byte[] input) throws ServiceException
	{
		int inputLength = input.length;
		byte[] ret = null;
		Deflater deflater = new Deflater();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(inputLength);
		try
		{
			deflater.setInput(input);
			deflater.finish();
			byte[] buf = new byte[1024];
			while (!deflater.finished())
			{
				int got = deflater.deflate(buf);
				baos.write(buf, 0, got);
			}
			ret = baos.toByteArray();
		}
		finally
		{
			deflater.end();
			try
			{
				baos.close();
			}
			catch (IOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		return ret;
	}
	public static byte[] uncompress(byte[] input) throws ServiceException
	{
		if (input.length == 0)
		{
			return input;
		}
		byte[] ret = null;
		Inflater decompresser = new Inflater();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
		try
		{
			decompresser.reset();
			decompresser.setInput(input, 0, input.length);
			byte[] buf = new byte[1024];
			int got = 0;
			while (!decompresser.finished())
			{
				try
				{
					got = decompresser.inflate(buf);
				}
				catch (DataFormatException e)
				{
					throw new ServiceException(e);
				}
				baos.write(buf, 0, got);
			}
			ret = baos.toByteArray();
		}
		finally
		{
			decompresser.end();
			try
			{
				baos.close();
			}
			catch (IOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		return ret;
	}
}
