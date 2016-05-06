package com.mantono.dejavu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Finds and deletes duplicate files in a given folder.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public class DuplicateRemover
{
	private final DuplicateFinder finder;

	/**
	 * @param dir is the path from were duplicates will be searched and removed.
	 */
	public DuplicateRemover(Path dir)
	{
		this.finder = new DuplicateFinder(dir, false);
	}

	/**
	 * @param args argument vector for this program. Current only reads path for
	 * which this program will work on.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException
	{
		final File file = new File(args[0]);
		if(!file.exists())
		{
			System.err.println("Folder " + file + " does not exist.");
			System.exit(1);
		}
		final Path dir = file.toPath();
		DuplicateRemover dr = new DuplicateRemover(dir);
		dr.deleteDuplicates();
	}

	private void deleteDuplicates() throws NoSuchAlgorithmException, IOException
	{
		final SortedMap<String, List<File>> dupeMap = finder.getDuplicates();
		Collection<List<File>> dupes = dupeMap.values();
		Set<File> toDelete = new HashSet<File>();
		for(List<File> files : dupes)
		{
			assert files.size() > 1;
			final int lastIndex = files.size() - 1;
			List<File> redundant = files.subList(0, lastIndex);
			toDelete.addAll(redundant);
		}
		assert dupes.size() == toDelete.size();
		deleteFiles(toDelete);
	}

	private int deleteFiles(Collection<File> toDelete) throws IOException
	{
		int deleted = 0;
		for(File file : toDelete)
		{
			Files.delete(file.toPath());
			System.out.println("Deleted " + file);
			deleted++;
		}
		return deleted;
	}

}
