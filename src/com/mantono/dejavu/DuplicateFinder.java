package com.mantono.dejavu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Find duplicate files in a folder based on md5 sum.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public class DuplicateFinder
{
	private final Path folder;
	private final boolean recursive;

	/**
	 * @param path folder to search for duplicate files in.
	 * @param recursive if true, the DuplicateFinder will search recursively,
	 * including sub directories of the given path (<em>not yet implemented</em>
	 * ) .
	 */
	public DuplicateFinder(Path path, boolean recursive)
	{
		this.folder = path;
		this.recursive = recursive;
	}

	/**
	 * 
	 * @return a {@link SortedMap} containing hashes as key and a list of files
	 * corresponding to the hash as a value.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public SortedMap<String, List<File>> getDuplicates() throws NoSuchAlgorithmException, IOException
	{
		Hasher hasher = new Hasher("MD5");

		SortedMap<File, String> index = hasher.indexFiles(folder);
		SortedMap<String, List<File>> duplicates = reverseMap(index);
		removeNonDuplicates(duplicates);
		return duplicates;
	}

	private SortedMap<String, List<File>> reverseMap(SortedMap<File, String> index)
	{
		SortedMap<String, List<File>> duplicate = new TreeMap<String, List<File>>();
		for(Entry<File, String> fileEntry : index.entrySet())
		{
			final File file = fileEntry.getKey();
			final String hash = fileEntry.getValue();
			if(!duplicate.containsKey(hash))
				duplicate.put(hash, new ArrayList<File>());
			List<File> files = duplicate.get(hash);
			files.add(file);
		}
		return duplicate;
	}

	private void removeNonDuplicates(SortedMap<String, List<File>> duplicate)
	{
		final Iterator<Entry<String, List<File>>> iter = duplicate.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<String, List<File>> entry = iter.next();
			if(entry.getValue().size() < 2)
				iter.remove();
		}
	}
}
