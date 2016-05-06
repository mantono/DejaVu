package com.mantono.dejavu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Produce a hash sum for all files in a folder.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public class Hasher
{
	private final String algorithm;

	/**
	 * Constructor for Hasher.
	 * @param algorithm is the algorithm to use for computing hash sums.
	 */
	public Hasher(final String algorithm)
	{
		this.algorithm = algorithm;
	}

	/**
	 * 
	 * @param folder to retrieve hash sums for.
	 * @return a {@link SortedMap} were a {@link File} is the index and its hash
	 * sum is represented as a {@link String}.
	 * @throws NoSuchAlgorithmException if an invalid hash algorithm is given as argument.
	 * @throws IOException if an I/O error occurred when trying to read from a file in the folder.
	 */
	public SortedMap<File, String> indexFiles(final Path folder) throws NoSuchAlgorithmException, IOException
	{
		SortedMap<File, String> indexedFiles = new TreeMap<File, String>();
		File dir = folder.toFile();
		File[] directoryListing = dir.listFiles();
		if(directoryListing != null)
		{
			for(File file : directoryListing)
			{
				if(file.isDirectory())
					continue;
				final String hashSum = getHashSum(file);
				indexedFiles.put(file, hashSum);
			}
		}

		return indexedFiles;
	}

	private String getHashSum(File file) throws IOException, NoSuchAlgorithmException
	{
		MessageDigest hashDigest = MessageDigest.getInstance(algorithm);
		try(InputStream input = Files.newInputStream(file.toPath()))
		{
			DigestInputStream dStream = new DigestInputStream(input, hashDigest);
			final byte[] inputBuffer = new byte[16];
			int length;
			while((length = dStream.read(inputBuffer)) >= 0)
			{
				hashDigest.update(inputBuffer, 0, length);
			}
			final byte[] byteHash = hashDigest.digest();
			return Arrays.toString(byteHash);
		}
	}
}
