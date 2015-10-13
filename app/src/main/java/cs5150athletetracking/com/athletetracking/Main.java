// Main Class

public class Main {
	public static void main(String[] args) {
		Uploader uploader = new Uploader("Emilio Torres", 21, 0, 25.5, 26.6, 0);
		Thread thread = new Thread(uploader);
		thread.start();
	}
}