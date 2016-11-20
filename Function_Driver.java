import java.io.IOException;

public class Function_Driver {

	public static void main(String[] args) {
		Function f = new Function();
		
		String trainingFile = ".\\src\\training.txt";
		String validationFile = ".\\src\\validation.txt";
		String testFile = ".\\src\\test.txt";
		String outputFile = ".\\src\\output.txt";
		
		try {
			f.loadTrainingData(trainingFile);
			f.setParameters(4, 10000, 0.9, 1738);
			f.train();
			f.validate(validationFile);
			f.testData(testFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
