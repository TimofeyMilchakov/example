package ru.example;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import com.google.common.hash.Hashing;

import ru.example.packag.Message;
import ru.example.util.ByteArrUtil;
import ru.example.util.SHA256;

public class Main
{

	public static void main(String[] args) throws NoSuchAlgorithmException
	{
		final String hashed = Hashing.sha256()
						.hashString("test", StandardCharsets.UTF_8)
						.toString();
		SHA256 sha256 = new SHA256();
		byte[] test = "tes".getBytes(StandardCharsets.UTF_8);
		test = ByteArrUtil.convertByteArrInBinary(test,1);
		Message m = ByteArrUtil.initMessage(test);
		String q = sha256.startHash(m);
	}
}
