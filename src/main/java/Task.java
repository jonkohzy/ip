public class Task {
	private String description;
	private boolean isDone;

	public Task(String description){
		this.description = description;
		this.isDone = false;
	}

	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return "[" + (this.isDone ? "X" : " ") + "] " + this.description;
	}

	public void markAsDone() {
		isDone = true;
	}

	public void markAsUndone() {
		isDone = false;
	}
}
