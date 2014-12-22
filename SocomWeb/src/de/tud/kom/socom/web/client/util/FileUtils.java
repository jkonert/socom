package de.tud.kom.socom.web.client.util;

public class FileUtils
{
	public static enum FileState
	{
		uninitialized(0),
		loading(1),
		error(-1),
		success(3);
		
		private int ordinal;

		private FileState(int ordinal)
		{
			this.ordinal = ordinal;			
		}
		
		public int getStateID()
		{
			return this.ordinal;
		}		
		
		
	}
}
