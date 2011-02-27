package dime.android.todo.logic;

public class Task
{
	public static final int	PRIORITY_LOW		= 0;
	public static final int	PRIORITY_NORMAL	= 1;
	public static final int	PRIORITY_HIGH		= 2;

	private int					id;
	private String				name;
	private int					priority;
	private boolean			completed;


	public Task (int id, String name, int priority, boolean completed)
	{
		this.id = id;
		this.name = name;
		this.priority = priority;
		this.completed = completed;
	}


	public int getId ( )
	{
		return id;
	}


	public void setId (int id)
	{
		this.id = id;
	}


	public String getName ( )
	{
		return name;
	}


	public void setName (String name)
	{
		this.name = name;
	}


	public int getPriority ( )
	{
		return priority;
	}


	public void setPriority (int priority)
	{
		this.priority = priority;
	}


	public boolean isCompleted ( )
	{
		return completed;
	}


	public void setCompleted (boolean completed)
	{
		this.completed = completed;
	}

}
